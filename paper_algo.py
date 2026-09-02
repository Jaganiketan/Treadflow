#!/usr/bin/env python3
"""
TradeFlow Paper Algo v2
OFFLINE / PAPER TRADING ONLY.

Reads OHLCV market data from CSV.
Generates EMA + RSI paper signals.
Writes:
  - paper_trades.csv
  - paper_summary.csv

NO broker/API calls are made.
"""

import csv
import os
from dataclasses import dataclass


@dataclass
class Position:
    side: str
    entry: float
    stop: float
    target: float
    entry_timestamp: str


CSV_IN = os.getenv("MARKET_DATA_FILE", "data/natural_gas_mini.csv")
CSV_OUT = os.getenv("TRADES_FILE", "paper_trades.csv")
SUMMARY_OUT = os.getenv("SUMMARY_FILE", "paper_summary.csv")

FAST = int(os.getenv("EMA_FAST", "9"))
SLOW = int(os.getenv("EMA_SLOW", "21"))
RSI_PERIOD = int(os.getenv("RSI_PERIOD", "14"))

STOP_PCT = float(os.getenv("STOP_PCT", "0.005"))
TARGET_PCT = float(os.getenv("TARGET_PCT", "0.01"))


def ema(values, period):
    if period <= 0:
        raise ValueError("EMA period must be greater than 0")

    k = 2 / (period + 1)

    out = []
    prev = None

    for value in values:
        if prev is None:
            prev = value
        else:
            prev = value * k + prev * (1 - k)

        out.append(prev)

    return out


def rsi(values, period):
    if period <= 0:
        raise ValueError("RSI period must be greater than 0")

    if len(values) == 0:
        return []

    out = [50.0]

    gains = []
    losses = []

    for i in range(1, len(values)):
        change = values[i] - values[i - 1]

        gains.append(max(change, 0))
        losses.append(max(-change, 0))

        if len(gains) < period:
            out.append(50.0)
            continue

        avg_gain = sum(gains[-period:]) / period
        avg_loss = sum(losses[-period:]) / period

        if avg_loss == 0:
            value = 100.0
        else:
            rs = avg_gain / avg_loss
            value = 100 - (100 / (1 + rs))

        out.append(value)

    return out


def get_timestamp(row, index):
    return row.get(
        "timestamp",
        row.get(
            "time",
            row.get("date", str(index))
        )
    )


def load_market_data():
    if not os.path.exists(CSV_IN):
        raise SystemExit(
            f"Missing {CSV_IN}. Put OHLCV CSV there first."
        )

    rows = []

    with open(CSV_IN, newline="", encoding="utf-8") as file:
        reader = csv.DictReader(file)

        if not reader.fieldnames:
            raise SystemExit("Market CSV has no header.")

        for raw in reader:
            row = {}

            for key, value in raw.items():
                if key is None:
                    continue

                key_lower = key.strip().lower()

                if value is None:
                    continue

                value = value.strip()

                if key_lower in ("timestamp", "time", "date"):
                    row[key_lower] = value
                elif value != "":
                    try:
                        row[key_lower] = float(value)
                    except ValueError:
                        row[key_lower] = value

            rows.append(row)

    if not rows:
        raise SystemExit("No market rows found.")

    for index, row in enumerate(rows):
        if "close" not in row:
            raise SystemExit(
                f"Missing 'close' column at market row {index + 1}."
            )

    return rows


def main():
    rows = load_market_data()

    closes = [float(row["close"]) for row in rows]

    if len(closes) < 2:
        raise SystemExit("At least 2 market candles are required.")

    ef = ema(closes, FAST)
    es = ema(closes, SLOW)
    rs = rsi(closes, RSI_PERIOD)

    position = None
    trades = []

    total_pnl = 0.0
    wins = 0
    losses = 0

    for i, row in enumerate(rows):
        price = closes[i]
        timestamp = get_timestamp(row, i)

        # -------------------------------------------------
        # 1. Check existing position for STOP / TARGET
        # -------------------------------------------------

        if position is not None:
            exit_reason = None

            if position.side == "BUY":
                if price <= position.stop:
                    exit_reason = "STOP_LOSS"
                elif price >= position.target:
                    exit_reason = "TARGET"

            elif position.side == "SELL":
                if price >= position.stop:
                    exit_reason = "STOP_LOSS"
                elif price <= position.target:
                    exit_reason = "TARGET"

            if exit_reason is not None:
                if position.side == "BUY":
                    pnl = price - position.entry
                else:
                    pnl = position.entry - price

                pnl = round(pnl, 4)

                trades.append([
                    timestamp,
                    "EXIT",
                    position.side,
                    round(price, 4),
                    pnl,
                    exit_reason
                ])

                total_pnl += pnl

                if pnl > 0:
                    wins += 1
                elif pnl < 0:
                    losses += 1

                position = None

        # -------------------------------------------------
        # 2. Generate new signal
        # -------------------------------------------------

        if position is None and i > 0:

            bullish_cross = (
                ef[i] > es[i]
                and ef[i - 1] <= es[i - 1]
            )

            bearish_cross = (
                ef[i] < es[i]
                and ef[i - 1] >= es[i - 1]
            )

            # BUY
            if bullish_cross and rs[i] >= 50:

                stop = price * (1 - STOP_PCT)
                target = price * (1 + TARGET_PCT)

                position = Position(
                    side="BUY",
                    entry=price,
                    stop=stop,
                    target=target,
                    entry_timestamp=timestamp
                )

                trades.append([
                    timestamp,
                    "ENTRY",
                    "BUY",
                    round(price, 4),
                    0,
                    "EMA_CROSS_RSI"
                ])

            # SELL
            elif bearish_cross and rs[i] <= 50:

                stop = price * (1 + STOP_PCT)
                target = price * (1 - TARGET_PCT)

                position = Position(
                    side="SELL",
                    entry=price,
                    stop=stop,
                    target=target,
                    entry_timestamp=timestamp
                )

                trades.append([
                    timestamp,
                    "ENTRY",
                    "SELL",
                    round(price, 4),
                    0,
                    "EMA_CROSS_RSI"
                ])

    # -----------------------------------------------------
    # 3. Close any remaining open position
    # -----------------------------------------------------

    if position is not None:

        last_price = closes[-1]
        last_timestamp = get_timestamp(rows[-1], len(rows) - 1)

        if position.side == "BUY":
            pnl = last_price - position.entry
        else:
            pnl = position.entry - last_price

        pnl = round(pnl, 4)

        trades.append([
            last_timestamp,
            "EXIT",
            position.side,
            round(last_price, 4),
            pnl,
            "END_OF_DATA"
        ])

        total_pnl += pnl

        if pnl > 0:
            wins += 1
        elif pnl < 0:
            losses += 1

        position = None

    # -----------------------------------------------------
    # 4. Calculate summary
    # -----------------------------------------------------

    entries = sum(
        1 for trade in trades
        if trade[1] == "ENTRY"
    )

    exits = sum(
        1 for trade in trades
        if trade[1] == "EXIT"
    )

    completed_trades = exits

    if completed_trades > 0:
        win_rate = (wins / completed_trades) * 100
    else:
        win_rate = 0.0

    total_pnl = round(total_pnl, 4)

    # -----------------------------------------------------
    # 5. Write trade log
    # -----------------------------------------------------

    with open(
        CSV_OUT,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:

        writer = csv.writer(file)

        writer.writerow([
            "timestamp",
            "action",
            "side",
            "price",
            "pnl_points",
            "reason"
        ])

        writer.writerows(trades)

    # -----------------------------------------------------
    # 6. Write summary
    # -----------------------------------------------------

    with open(
        SUMMARY_OUT,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:

        writer = csv.writer(file)

        writer.writerow([
            "metric",
            "value"
        ])

        writer.writerow(["market_file", CSV_IN])
        writer.writerow(["candles", len(rows)])
        writer.writerow(["entries", entries])
        writer.writerow(["exits", exits])
        writer.writerow(["completed_trades", completed_trades])
        writer.writerow(["wins", wins])
        writer.writerow(["losses", losses])
        writer.writerow(["win_rate_percent", round(win_rate, 2)])
        writer.writerow(["total_pnl_points", total_pnl])
        writer.writerow(["ema_fast", FAST])
        writer.writerow(["ema_slow", SLOW])
        writer.writerow(["rsi_period", RSI_PERIOD])
        writer.writerow(["stop_percent", STOP_PCT * 100])
        writer.writerow(["target_percent", TARGET_PCT * 100])

    print("====================================")
    print("TradeFlow Paper Algo v2")
    print("====================================")
    print(f"Market candles : {len(rows)}")
    print(f"Entries        : {entries}")
    print(f"Exits          : {exits}")
    print(f"Completed      : {completed_trades}")
    print(f"Wins           : {wins}")
    print(f"Losses         : {losses}")
    print(f"Win rate       : {win_rate:.2f}%")
    print(f"Total P&L      : {total_pnl} points")
    print(f"Trade log      : {CSV_OUT}")
    print(f"Summary        : {SUMMARY_OUT}")
    print("====================================")


if __name__ == "__main__":
    main()
