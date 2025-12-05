import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { MOCK_SIGNALS, MOCK_TRADERS } from '../constants';
import { DirectionBadge, StatusBadge, GlassCard, Button, ConfidenceBar, NeonBadge } from '../components/Shared';
import { SignalStatus, Direction } from '../types';
import { Clock, Target, PlayCircle, Lock, AlertCircle, CheckCircle, Loader2 } from 'lucide-react';

export const SignalDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [copyState, setCopyState] = useState<'idle' | 'loading' | 'success'>('idle');

  const signal = MOCK_SIGNALS.find((s) => s.id === id);
  const trader = MOCK_TRADERS.find((t) => t.id === signal?.traderId);

  if (!signal || !trader) return <div className="p-10 text-center">Signal not found</div>;

  const isActive = signal.status === SignalStatus.ACTIVE;
  const isLong = signal.direction === Direction.LONG;

  const handleCopyTrade = () => {
    if (!isActive) return;
    setCopyState('loading');
    
    // Simulate API call
    setTimeout(() => {
        setCopyState('success');
        // Reset after 2 seconds
        setTimeout(() => setCopyState('idle'), 2000);
    }, 1200);
  };

  return (
    <div className="pb-8 relative min-h-full">
      <div className="p-5 space-y-6 pb-32">
        
        {/* Signal Header */}
        <div className="flex flex-col gap-4">
            <div className="flex justify-between items-start">
                 <div className="flex items-center gap-3">
                    <img src={trader.avatar} className="w-10 h-10 rounded-xl" alt="" />
                    <div>
                        <h1 className="text-2xl font-bold text-white font-mono tracking-tight leading-none">{signal.pair}</h1>
                        <p className="text-xs text-zinc-400 mt-1">by {trader.name}</p>
                    </div>
                 </div>
                 <StatusBadge status={signal.status} />
            </div>
            
            <div className="flex items-center gap-2">
                 <NeonBadge color={isLong ? 'green' : 'red'}>
                    <DirectionBadge direction={signal.direction} />
                 </NeonBadge>
                 <span className="text-xs text-zinc-500 font-mono flex items-center gap-1 bg-zinc-900 px-2 py-1 rounded-lg border border-zinc-800">
                     <Clock size={12} /> {signal.timeframe}
                 </span>
            </div>
        </div>

        {/* Entry / SL Card */}
        <div className="grid grid-cols-2 gap-3">
            <GlassCard className="flex flex-col justify-between h-24 relative overflow-hidden">
                <div className="absolute top-0 right-0 p-2 opacity-10">
                    <Target size={40} />
                </div>
                <span className="text-xs text-zinc-500 font-bold uppercase tracking-wider">Entry</span>
                <div>
                     <span className="text-xl font-mono text-white font-bold">{signal.entryPrice}</span>
                     <span className="block text-[10px] text-zinc-500">{signal.entryType}</span>
                </div>
            </GlassCard>
            <GlassCard className="flex flex-col justify-between h-24 relative overflow-hidden border-rose-500/20">
                <div className="absolute top-0 right-0 p-2 opacity-10 text-rose-500">
                    <AlertCircle size={40} />
                </div>
                <span className="text-xs text-rose-400/80 font-bold uppercase tracking-wider">Stop Loss</span>
                <div>
                     <span className="text-xl font-mono text-rose-400 font-bold">{signal.stopLoss}</span>
                     <span className="block text-[10px] text-rose-500/50">Risk Zone</span>
                </div>
            </GlassCard>
        </div>

        <ConfidenceBar level={signal.confidence} />

        {/* Targets */}
        <div className="space-y-4 pt-2">
            <h3 className="text-sm font-bold text-zinc-500 uppercase tracking-wider">Profit Targets</h3>
            
            <div className="space-y-2">
                {signal.takeProfits.map((tp, idx) => (
                    <div key={idx} className="relative overflow-hidden bg-zinc-900/50 border border-white/5 rounded-xl p-4 flex justify-between items-center group hover:border-emerald-500/30 transition-colors">
                        
                        <div className="flex items-center gap-4">
                            <div className={`w-6 h-6 rounded-full flex items-center justify-center text-[10px] font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20`}>
                                TP{idx + 1}
                            </div>
                            <span className="font-mono text-lg text-white font-bold">{tp.price}</span>
                        </div>
                        <span className="font-mono text-sm text-emerald-400 font-bold">
                            +{tp.percentage}%
                        </span>
                    </div>
                ))}
            </div>
        </div>
      </div>

      {/* Action Area - Floating above nav */}
      <div className="fixed bottom-[84px] left-0 w-full px-5 z-40 max-w-md mx-auto pointer-events-none">
        <div className="pointer-events-auto">
            <Button 
                fullWidth 
                onClick={handleCopyTrade} 
                disabled={!isActive || copyState !== 'idle'} 
                className={`shadow-2xl shadow-black/50 transition-all duration-300 ${copyState === 'success' ? 'bg-emerald-500 border-emerald-400 text-white' : ''}`}
            >
                {copyState === 'loading' ? (
                    <><Loader2 size={20} className="animate-spin" /> Executing...</>
                ) : copyState === 'success' ? (
                    <><CheckCircle size={20} /> Order Placed!</>
                ) : isActive ? (
                    <><PlayCircle size={20} /> Copy Trade Now</>
                ) : (
                    <>Signal Expired</>
                )}
            </Button>
        </div>
      </div>
    </div>
  );
};