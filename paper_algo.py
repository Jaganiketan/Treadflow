#!/usr/bin/env python3
"""TradeFlow Paper Algo - offline/paper only.
Reads data/natural_gas_mini.csv and writes paper_trades.csv.
No broker/API calls are made.
"""
import csv, os
from dataclasses import dataclass

@dataclass
class Position:
    side: str
    entry: float
    stop: float
    target: float

CSV_IN = os.getenv('MARKET_DATA_FILE', 'data/natural_gas_mini.csv')
CSV_OUT = os.getenv('TRADES_FILE', 'paper_trades.csv')
FAST = int(os.getenv('EMA_FAST', '9'))
SLOW = int(os.getenv('EMA_SLOW', '21'))
RSI_PERIOD = int(os.getenv('RSI_PERIOD', '14'))
STOP_PCT = float(os.getenv('STOP_PCT', '0.005'))
TARGET_PCT = float(os.getenv('TARGET_PCT', '0.01'))


def ema(values, period):
    k = 2 / (period + 1)
    out=[]; prev=None
    for x in values:
        prev = x if prev is None else x*k + prev*(1-k)
        out.append(prev)
    return out


def rsi(values, period):
    out=[50.0]
    gains=[]; losses=[]
    for i in range(1,len(values)):
        d=values[i]-values[i-1]
        gains.append(max(d,0)); losses.append(max(-d,0))
        if len(gains)<period:
            out.append(50.0); continue
        ag=sum(gains[-period:])/period; al=sum(losses[-period:])/period
        out.append(100.0 if al==0 else 100-100/(1+ag/al))
    return out


def main():
    if not os.path.exists(CSV_IN):
        raise SystemExit(f'Missing {CSV_IN}. Put OHLCV CSV there first.')
    rows=[]
    with open(CSV_IN,newline='') as f:
        for r in csv.DictReader(f):
            rows.append({k.lower(): float(v) if k.lower() not in ('timestamp','time','date') else v for k,v in r.items()})
    if not rows: raise SystemExit('No market rows found.')
    closes=[float(r['close']) for r in rows]
    ef=ema(closes,FAST); es=ema(closes,SLOW); rs=rsi(closes,RSI_PERIOD)
    pos=None; trades=[]
    for i,r in enumerate(rows):
        px=closes[i]; ts=r.get('timestamp',r.get('time',r.get('date',str(i))))
        if pos:
            exit_reason=None
            if pos.side=='BUY':
                if px<=pos.stop: exit_reason='STOP_LOSS'
                elif px>=pos.target: exit_reason='TARGET'
            else:
                if px>=pos.stop: exit_reason='STOP_LOSS'
                elif px<=pos.target: exit_reason='TARGET'
            if exit_reason:
                pnl=(px-pos.entry) if pos.side=='BUY' else (pos.entry-px)
                trades.append([ts,'EXIT',pos.side,px,round(pnl,4),exit_reason])
                pos=None
        if pos is None and i>0:
            if ef[i]>es[i] and ef[i-1]<=es[i-1] and rs[i]>=50:
                pos=Position('BUY',px,px*(1-STOP_PCT),px*(1+TARGET_PCT))
                trades.append([ts,'ENTRY','BUY',px,0,'EMA_CROSS_RSI'])
            elif ef[i]<es[i] and ef[i-1]>=es[i-1] and rs[i]<=50:
                pos=Position('SELL',px,px*(1+STOP_PCT),px*(1-TARGET_PCT))
                trades.append([ts,'ENTRY','SELL',px,0,'EMA_CROSS_RSI'])
    with open(CSV_OUT,'w',newline='') as f:
        w=csv.writer(f); w.writerow(['timestamp','action','side','price','pnl_points','reason']); w.writerows(trades)
    print(f'Paper run complete: {len(trades)} events -> {CSV_OUT}')

if __name__=='__main__': main()
