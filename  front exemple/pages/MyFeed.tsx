import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Radio, ChevronRight } from 'lucide-react';
import { MOCK_TRADERS, MOCK_SIGNALS } from '../constants';
import { GlassCard, StatusBadge, DirectionBadge, Button } from '../components/Shared';
import { SignalStatus } from '../types';

export const MyFeed: React.FC = () => {
  const navigate = useNavigate();
  const subscribedTraders = MOCK_TRADERS.filter(t => t.isSubscribed);

  if (subscribedTraders.length === 0) {
    return (
        <div className="flex flex-col items-center justify-center h-[70vh] p-6 text-center space-y-6 animate-fade-in">
            <div className="w-20 h-20 bg-zinc-900 rounded-full flex items-center justify-center border border-zinc-800">
                <Radio size={32} className="text-zinc-600" />
            </div>
            <div>
                <h3 className="text-xl font-bold text-white">No active subscriptions</h3>
                <p className="text-zinc-500 text-sm mt-2 max-w-[250px] mx-auto">
                    Subscribe to traders in the marketplace to see their real-time signals here.
                </p>
            </div>
            <Button onClick={() => navigate('/')}>Go to Marketplace</Button>
        </div>
    );
  }

  return (
    <div className="p-5 space-y-8 animate-fade-in">
      <div className="flex items-center justify-between">
         <h2 className="text-xl font-bold text-white flex items-center gap-2">
            <Radio size={20} className="text-primary-glow" /> Live Feed
         </h2>
      </div>

      <div className="space-y-8">
        {subscribedTraders.map(trader => {
            // Get Active signals for this specific trader
            const activeSignals = MOCK_SIGNALS.filter(
                s => s.traderId === trader.id && s.status === SignalStatus.ACTIVE
            );

            return (
                <div key={trader.id} className="space-y-3">
                    {/* Trader Section Header */}
                    <div 
                        className="flex items-center justify-between cursor-pointer group"
                        onClick={() => navigate(`/traders/${trader.id}`)}
                    >
                        <div className="flex items-center gap-3">
                            <img src={trader.avatar} className="w-10 h-10 rounded-full border border-white/10" alt={trader.name} />
                            <div>
                                <h3 className="text-sm font-bold text-white">{trader.name}</h3>
                                <p className="text-[10px] text-zinc-500 uppercase tracking-wide">
                                    {activeSignals.length} Active Signals
                                </p>
                            </div>
                        </div>
                        <div className="p-2 rounded-full bg-white/5 text-zinc-500 group-hover:text-white transition-colors">
                            <ChevronRight size={16} />
                        </div>
                    </div>

                    {/* Signals List for this Trader */}
                    <div className="space-y-3 pl-2 border-l border-white/5">
                        {activeSignals.length > 0 ? (
                            activeSignals.map(signal => (
                                <GlassCard key={signal.id} onClick={() => navigate(`/signal/${signal.id}`)} className="ml-2">
                                    <div className="flex justify-between items-start mb-3">
                                        <div className="flex items-center gap-2">
                                            <span className="text-lg font-bold text-white font-mono">{signal.pair}</span>
                                            <span className="text-[10px] text-zinc-500 font-bold bg-white/5 px-2 py-0.5 rounded">{signal.timeframe}</span>
                                        </div>
                                        <StatusBadge status={signal.status} />
                                    </div>

                                    <div className="flex justify-between items-center mb-1">
                                         <DirectionBadge direction={signal.direction} />
                                         <div className="flex items-center gap-3 text-xs">
                                             <div className="flex flex-col items-end">
                                                 <span className="text-zinc-600 font-bold text-[10px] uppercase">Entry</span>
                                                 <span className="text-zinc-300 font-mono">{signal.entryPrice}</span>
                                             </div>
                                             <div className="w-px h-6 bg-white/10"></div>
                                             <div className="flex flex-col items-end">
                                                 <span className="text-zinc-600 font-bold text-[10px] uppercase">Target</span>
                                                 <span className="text-emerald-400 font-mono font-bold">{signal.takeProfits[signal.takeProfits.length - 1].price}</span>
                                             </div>
                                         </div>
                                    </div>
                                </GlassCard>
                            ))
                        ) : (
                            <div className="ml-2 py-4 px-4 rounded-xl bg-white/5 border border-dashed border-white/10 text-center">
                                <p className="text-xs text-zinc-500 italic">No active signals at the moment.</p>
                            </div>
                        )}
                    </div>
                </div>
            );
        })}
      </div>
    </div>
  );
};