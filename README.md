# TradeFlow Algo — Paper Mode

This folder contains the first GitHub Actions paper-trading engine.

## Important
- **Paper trading only.** It does not connect to Angel One and cannot place real orders.
- The sample CSV is only test data. Replace it with properly sourced historical/market data before evaluating a strategy.
- GitHub Actions is being used as a free test runner; it is not a suitable production host for Angel One live order execution because hosted-runner IPs are not fixed.

## Run locally
```bash
python3 algo/paper_algo.py
```
Output: `paper_trades.csv`.

## Strategy defaults
- EMA 9 / EMA 21 crossover
- RSI 14 confirmation (>= 50 long, <= 50 short)
- Stop loss: 0.5%
- Target: 1%

These are **example parameters, not financial advice or a claim of profitability**.
