import React, { useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MOCK_TRADERS, MOCK_SIGNALS } from '../constants';
import { SignalStatus } from '../types';
import { StatusBadge, DirectionBadge, GlassCard, Button, NeonBadge } from '../components/Shared';
import { Star, Lock, Clock, ShieldCheck } from 'lucide-react';

export const TraderDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const trader = MOCK_TRADERS.find((t) => t.id === id);
  const signals = MOCK_SIGNALS.filter((s) => s.traderId === id);
  
  // If not subscribed, active signals are blurred/hidden
  const activeSignals = signals.filter(s => s.status === SignalStatus.ACTIVE);
  const expiredSignals = signals.filter(s => s.status === SignalStatus.EXPIRED);

  // --- Dynamic Chart Logic ---
  const { chartPath, chartGradientPath, totalPnl } = useMemo(() => {
    // 1. Sort expired signals by date (Oldest first)
    const history = [...expiredSignals].sort((a, b) => 
        new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    );

    // 2. Calculate Cumulative PnL points
    let currentPnl = 0;
    const points: number[] = [0]; // Start at 0
    history.forEach(s => {
        if (s.resultPnl !== undefined) {
            currentPnl += s.resultPnl;
            points.push(currentPnl);
        }
    });

    const finalPnl = currentPnl;

    // 3. Normalize for SVG (ViewBox 0 0 100 40)
    if (points.length < 2) return { chartPath: "", chartGradientPath: "", totalPnl: 0 };

    const minPnl = Math.min(...points);
    const maxPnl = Math.max(...points);
    const range = maxPnl - minPnl || 1; // Prevent divide by zero

    // X axis: distribute points evenly
    const stepX = 100 / (points.length - 1);
    
    // Y axis: flip because SVG 0 is top
    // We map minPnl to 35 (bottom with padding) and maxPnl to 5 (top with padding)
    const mapY = (val: number) => {
        const normalized = (val - minPnl) / range; // 0 to 1
        return 35 - (normalized * 30); // 35 down to 5
    };

    const pathD = points.map((val, idx) => {
        const x = idx * stepX;
        const y = mapY(val);
        return `${idx === 0 ? 'M' : 'L'} ${x.toFixed(1)},${y.toFixed(1)}`;
    }).join(' ');

    // For the gradient area, we need to close the path
    // Go to bottom right (100, 40) then bottom left (0, 40) then close
    const areaD = `${pathD} L 100,45 L 0,45 Z`;

    return { chartPath: pathD, chartGradientPath: areaD, totalPnl: finalPnl };
  }, [expiredSignals]);

  if (!trader) return <div>Trader not found</div>;

  return (
    <div className="pb-8 animate-fade-in">
      {/* Header Profile */}
      <div className="relative pt-8 px-5 pb-6 bg-gradient-to-b from-primary-glow/10 to-bg">
        <div className="flex gap-4">
            <img src={trader.avatar} className="w-20 h-20 rounded-2xl object-cover ring-4 ring-bg shadow-xl" alt="" />
            <div className="flex-1 min-w-0 pt-1">
                <h1 className="text-2xl font-bold text-white truncate">{trader.name}</h1>
                <div className="flex items-center gap-2 mt-1">
                    <span className="text-xs font-bold text-yellow-500 flex items-center gap-1">
                        <Star size={12} fill="currentColor" /> {trader.rating}
                    </span>
                    <span className="text-zinc-600 text-xs">•</span>
                    <span className="text-xs text-zinc-400">{trader.reviewsCount} reviews</span>
                </div>
            </div>
        </div>
        
        <p className="mt-4 text-sm text-zinc-300 leading-relaxed">{trader.bio}</p>

        {/* Action Button */}
        <div className="mt-6">
            {trader.isSubscribed ? (
                 <Button variant="secondary" fullWidth className="bg-emerald-500/10 text-emerald-400 border-emerald-500/20">
                    <ShieldCheck size={18} /> Active Subscriber
                 </Button>
            ) : (
                 <Button fullWidth>
                    Subscribe for ${trader.price}/mo
                 </Button>
            )}
        </div>
      </div>

      <div className="p-5 space-y-8">
        {/* Performance Chart Dynamic */}
        <div className="space-y-3">
             <div className="flex justify-between items-end">
                <h3 className="text-sm font-bold text-zinc-500 uppercase tracking-wider">Performance</h3>
                <NeonBadge color={totalPnl >= 0 ? "green" : "red"}>
                    {totalPnl >= 0 ? '+' : ''}{totalPnl.toFixed(2)}% Recorded
                </NeonBadge>
             </div>
             <GlassCard className="h-40 flex items-end p-0 overflow-hidden relative">
                 {/* Chart Container */}
                 <div className="w-full h-full relative">
                    <div className="absolute inset-0 bg-gradient-to-t from-bg to-transparent opacity-50 z-10"></div>
                    <svg viewBox="0 0 100 40" className="w-full h-full preserve-3d absolute inset-0 z-20">
                        {/* Gradient Fill */}
                        <defs>
                            <linearGradient id="chartGrad" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="0%" stopColor="#10b981" stopOpacity="0.4" />
                                <stop offset="100%" stopColor="#10b981" stopOpacity="0" />
                            </linearGradient>
                        </defs>
                        <path 
                            d={chartGradientPath} 
                            fill="url(#chartGrad)" 
                            stroke="none"
                        />
                        {/* Line */}
                        <path 
                            d={chartPath} 
                            fill="none" 
                            stroke="#10b981" 
                            strokeWidth="1.5"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            className="drop-shadow-[0_0_8px_rgba(16,185,129,0.5)]"
                        />
                    </svg>
                    {/* Grid lines */}
                    <div className="absolute inset-0 grid grid-cols-4 pointer-events-none opacity-10">
                        <div className="border-r border-white"></div>
                        <div className="border-r border-white"></div>
                        <div className="border-r border-white"></div>
                    </div>
                 </div>
             </GlassCard>
             <div className="grid grid-cols-3 gap-2">
                 <div className="bg-zinc-900 rounded-xl p-2 text-center border border-zinc-800">
                    <span className="block text-emerald-400 font-bold font-mono text-lg">{trader.winRate}%</span>
                    <span className="text-[9px] text-zinc-500 uppercase font-bold">Win Rate</span>
                 </div>
                  <div className="bg-zinc-900 rounded-xl p-2 text-center border border-zinc-800">
                    <span className="block text-white font-bold font-mono text-lg">{trader.totalSignals}</span>
                    <span className="text-[9px] text-zinc-500 uppercase font-bold">Total Calls</span>
                 </div>
                  <div className="bg-zinc-900 rounded-xl p-2 text-center border border-zinc-800">
                    <span className="block text-primary-glow font-bold font-mono text-lg">1:3.5</span>
                    <span className="text-[9px] text-zinc-500 uppercase font-bold">Avg R:R</span>
                 </div>
             </div>
        </div>

        {/* Live Signals Section */}
        <div className="space-y-3">
             <h3 className="text-sm font-bold text-zinc-500 uppercase tracking-wider flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-rose-500 animate-pulse"></div> Live Signals
             </h3>
             
             {activeSignals.length > 0 ? (
                 <div className="space-y-3">
                    {activeSignals.map(signal => (
                        <div key={signal.id} className="relative">
                            {!trader.isSubscribed && (
                                <div className="absolute inset-0 z-20 backdrop-blur-sm bg-bg/50 flex flex-col items-center justify-center border border-white/5 rounded-2xl">
                                    <div className="p-3 bg-zinc-900 rounded-full border border-zinc-700 shadow-xl mb-2">
                                        <Lock size={20} className="text-zinc-400" />
                                    </div>
                                    <span className="text-xs font-bold text-zinc-300">Subscribe to unlock</span>
                                </div>
                            )}
                            <GlassCard onClick={trader.isSubscribed ? () => navigate(`/signal/${signal.id}`) : undefined} className={!trader.isSubscribed ? 'opacity-50' : ''}>
                                <div className="flex justify-between items-center mb-2">
                                    <span className="font-mono font-bold text-white">{signal.pair}</span>
                                    <StatusBadge status={signal.status} />
                                </div>
                                <div className="flex justify-between items-center text-xs text-zinc-400">
                                    <span className="flex items-center gap-1"><Clock size={12} /> {signal.timeframe}</span>
                                    <DirectionBadge direction={signal.direction} />
                                </div>
                            </GlassCard>
                        </div>
                    ))}
                 </div>
             ) : (
                 <p className="text-zinc-500 text-sm italic">No active positions currently.</p>
             )}
        </div>

        {/* Past History */}
        <div className="space-y-3">
             <h3 className="text-sm font-bold text-zinc-500 uppercase tracking-wider">History ({expiredSignals.length})</h3>
             <div className="space-y-2">
                {expiredSignals.map(signal => (
                    <GlassCard key={signal.id} className="py-3 px-4 flex items-center justify-between group hover:bg-white/5" onClick={() => navigate(`/signal/${signal.id}`)}>
                        <div className="flex items-center gap-3">
                            <DirectionBadge direction={signal.direction} />
                            <div className="flex flex-col">
                                <span className="font-bold text-white font-mono text-sm">{signal.pair}</span>
                                <span className="text-[10px] text-zinc-500">{new Date(signal.createdAt).toLocaleDateString()}</span>
                            </div>
                        </div>
                        <div className={`font-mono font-bold text-sm ${(signal.resultPnl || 0) > 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
                            {(signal.resultPnl || 0) > 0 ? '+' : ''}{signal.resultPnl}%
                        </div>
                    </GlassCard>
                ))}
             </div>
        </div>

      </div>
    </div>
  );
};