import React, { useState } from 'react';
import { Settings, Plus, X, Trash2, ChevronDown, ArrowRight } from 'lucide-react';
import { GlassCard, Button } from '../components/Shared';

interface TakeProfitInput {
  price: string;
  percentage: string;
}

const BASE_ASSETS = ['BTC', 'ETH', 'SOL', 'BNB', 'XRP', 'ADA', 'AVAX', 'LINK', 'DOGE'];
const QUOTE_ASSETS = ['USDT', 'USDC', 'USD', 'EUR', 'BTC', 'ETH'];

const TIMEFRAMES = [
  { label: 'M1 (1 min)', value: 'M1' },
  { label: 'M5 (5 min)', value: 'M5' },
  { label: 'M15 (15 min)', value: 'M15' },
  { label: 'M30 (30 min)', value: 'M30' },
  { label: 'H1 (1 hour)', value: 'H1' },
  { label: 'H4 (4 hours)', value: 'H4' },
  { label: 'D1 (1 day)', value: 'D1' },
  { label: 'W1 (1 week)', value: 'W1' },
  { label: 'MN1 (1 month)', value: 'MN1' },
];

export const Profile: React.FC = () => {
  const [showCreateForm, setShowCreateForm] = useState(false);
  
  // Form State
  const [baseAsset, setBaseAsset] = useState('BTC');
  const [quoteAsset, setQuoteAsset] = useState('USDT');
  const [confidence, setConfidence] = useState(5);
  const [takeProfits, setTakeProfits] = useState<TakeProfitInput[]>([
    { price: '', percentage: '100' }
  ]);

  const addTp = () => {
    setTakeProfits([...takeProfits, { price: '', percentage: '' }]);
  };

  const removeTp = (index: number) => {
    if (takeProfits.length > 1) {
      setTakeProfits(takeProfits.filter((_, i) => i !== index));
    }
  };

  const updateTp = (index: number, field: keyof TakeProfitInput, value: string) => {
    const newTps = [...takeProfits];
    newTps[index][field] = value;
    setTakeProfits(newTps);
  };

  return (
    <div className="p-5 space-y-8 animate-fade-in">
      
      {/* Profile Header */}
      <div className="flex items-center gap-5">
        <div className="w-20 h-20 rounded-full bg-gradient-to-tr from-primary-glow to-purple-500 flex items-center justify-center text-3xl font-bold text-white shadow-2xl shadow-primary-glow/20 ring-4 ring-white/5">
            ME
        </div>
        <div>
            <h2 className="text-2xl font-bold text-white">John Doe</h2>
            <p className="text-zinc-400 text-sm">Pro Member</p>
        </div>
      </div>

      {/* Action Grid */}
      <div className="grid grid-cols-2 gap-3">
        <GlassCard className="flex flex-col items-center py-5 bg-zinc-900/50">
            <span className="text-3xl font-bold text-white font-mono">12</span>
            <span className="text-[10px] text-zinc-500 uppercase font-bold tracking-wider mt-1">Following</span>
        </GlassCard>
        <button 
            onClick={() => setShowCreateForm(true)}
            className="flex flex-col items-center justify-center py-5 rounded-2xl border border-dashed border-zinc-700 hover:border-primary-glow/50 hover:bg-primary-glow/5 transition-all group"
        >
            <div className="w-10 h-10 rounded-full bg-zinc-800 flex items-center justify-center text-zinc-400 group-hover:bg-primary-glow group-hover:text-white transition-colors mb-2">
                <Plus size={24} />
            </div>
            <span className="text-[10px] text-zinc-500 uppercase font-bold tracking-wider group-hover:text-primary-glow">Create Signal</span>
        </button>
      </div>

      {/* Settings List */}
      <div className="space-y-2">
        <h3 className="text-sm font-bold text-zinc-500 uppercase tracking-wider px-1">Settings</h3>
        <GlassCard className="p-0 overflow-hidden divide-y divide-white/5">
             <button className="w-full flex items-center justify-between p-4 hover:bg-white/5 transition-colors text-left">
                <span className="text-sm font-medium text-zinc-300">Notifications</span>
                <Settings size={16} className="text-zinc-500" />
            </button>
            <button className="w-full flex items-center justify-between p-4 hover:bg-white/5 transition-colors text-left">
                <span className="text-sm font-medium text-zinc-300">Wallet Connection</span>
                <span className="text-xs text-zinc-500">Not Connected</span>
            </button>
        </GlassCard>
      </div>

      {/* Create Signal Modal Overlay */}
      {showCreateForm && (
          <div className="fixed inset-0 z-[60] bg-black/80 backdrop-blur-sm flex items-end sm:items-center justify-center p-4">
              <div className="bg-[#121214] border border-white/10 w-full max-w-sm rounded-3xl p-6 shadow-2xl relative animate-in slide-in-from-bottom duration-300 max-h-[90vh] overflow-y-auto no-scrollbar">
                  <button 
                    onClick={() => setShowCreateForm(false)}
                    className="absolute top-4 right-4 p-2 text-zinc-500 hover:text-white bg-zinc-900 rounded-full"
                  >
                      <X size={20} />
                  </button>
                  
                  <h3 className="text-xl font-bold text-white mb-6">New Signal</h3>
                  
                  <form className="space-y-5" onSubmit={(e) => { e.preventDefault(); setShowCreateForm(false); }}>
                      
                      {/* Pair Selection Split */}
                      <div>
                          <label className="block text-xs font-bold text-zinc-500 uppercase mb-1.5">Market Pair</label>
                          <div className="flex items-center gap-2">
                              <div className="relative flex-1">
                                <select 
                                    value={baseAsset}
                                    onChange={(e) => setBaseAsset(e.target.value)}
                                    className="w-full bg-black/50 border border-zinc-700 rounded-xl p-3 text-white focus:border-primary-glow outline-none appearance-none font-mono"
                                >
                                    {BASE_ASSETS.map(a => <option key={a} value={a}>{a}</option>)}
                                </select>
                                <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-500 pointer-events-none" size={16} />
                              </div>
                              <span className="text-zinc-600"><ArrowRight size={16} /></span>
                              <div className="relative w-28">
                                <select 
                                    value={quoteAsset}
                                    onChange={(e) => setQuoteAsset(e.target.value)}
                                    className="w-full bg-black/50 border border-zinc-700 rounded-xl p-3 text-white focus:border-primary-glow outline-none appearance-none font-mono"
                                >
                                    {QUOTE_ASSETS.map(a => <option key={a} value={a}>{a}</option>)}
                                </select>
                                <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-500 pointer-events-none" size={16} />
                              </div>
                          </div>
                      </div>
                      
                      <div className="grid grid-cols-2 gap-4">
                          <div>
                            <label className="block text-xs font-bold text-zinc-500 uppercase mb-1.5">Action</label>
                            <div className="relative">
                                <select className="w-full bg-black/50 border border-zinc-700 rounded-xl p-3 text-white focus:border-primary-glow outline-none appearance-none">
                                    <option value="LONG">LONG (Buy)</option>
                                    <option value="SHORT">SHORT (Sell)</option>
                                </select>
                                <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-500 pointer-events-none" size={16} />
                            </div>
                          </div>
                          <div>
                            <label className="block text-xs font-bold text-zinc-500 uppercase mb-1.5">Entry Price</label>
                            <input type="number" placeholder="0.00" className="w-full bg-black/50 border border-zinc-700 rounded-xl p-3 text-white focus:border-primary-glow focus:outline-none placeholder-zinc-600 font-mono" />
                          </div>
                      </div>

                      {/* Timeframe & Confidence */}
                      <div className="grid grid-cols-2 gap-4">
                          <div>
                            <label className="block text-xs font-bold text-zinc-500 uppercase mb-1.5">Validity</label>
                            <div className="relative">
                                <select className="w-full bg-black/50 border border-zinc-700 rounded-xl p-3 text-white focus:border-primary-glow outline-none appearance-none text-sm">
                                    {TIMEFRAMES.map(tf => (
                                        <option key={tf.value} value={tf.value}>{tf.label}</option>
                                    ))}
                                </select>
                                <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-500 pointer-events-none" size={16} />
                            </div>
                          </div>
                          <div>
                             <label className="block text-xs font-bold text-zinc-500 uppercase mb-1.5 flex justify-between">
                                Confidence <span className="text-primary-glow">{confidence}/10</span>
                             </label>
                             <input 
                                type="range" 
                                min="1" 
                                max="10" 
                                value={confidence}
                                onChange={(e) => setConfidence(parseInt(e.target.value))}
                                className="w-full accent-primary-glow h-2 bg-zinc-800 rounded-lg appearance-none cursor-pointer mt-2"
                             />
                          </div>
                      </div>

                      {/* STOP LOSS HIGHLIGHTED */}
                      <div className="p-4 rounded-xl bg-rose-500/5 border border-rose-500/20">
                        <label className="block text-xs font-bold text-rose-400 uppercase mb-1.5">Stop Loss (Required)</label>
                        <div className="relative">
                            <input 
                                type="number" 
                                placeholder="0.00" 
                                className="w-full bg-black/50 border border-rose-500/30 rounded-lg p-3 text-white focus:border-rose-500 focus:ring-1 focus:ring-rose-500/50 focus:outline-none placeholder-zinc-600 font-mono" 
                            />
                            <div className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-rose-500/50 font-bold pointer-events-none">
                                RISK
                            </div>
                        </div>
                      </div>

                      {/* Dynamic Take Profits */}
                      <div className="space-y-3 pt-1">
                        <div className="flex justify-between items-center">
                             <label className="block text-xs font-bold text-emerald-500/70 uppercase">Take Profit Targets</label>
                             <button 
                                type="button" 
                                onClick={addTp} 
                                className="text-[10px] bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 px-2 py-1 rounded transition-colors"
                             >
                                + Add Level
                             </button>
                        </div>
                        
                        {takeProfits.map((tp, idx) => (
                            <div key={idx} className="flex gap-2 items-center animate-in slide-in-from-left-2 duration-300">
                                <div className="flex-1 relative">
                                     <div className="absolute left-3 top-1/2 -translate-y-1/2 text-[10px] font-bold text-zinc-500 font-mono pointer-events-none">
                                        TP{idx + 1}
                                     </div>
                                     <input
                                        type="number"
                                        placeholder="Price"
                                        value={tp.price}
                                        onChange={(e) => updateTp(idx, 'price', e.target.value)}
                                        className="w-full bg-black/50 border border-emerald-900/50 rounded-xl py-3 pl-10 pr-3 text-white text-sm focus:border-emerald-500 focus:outline-none placeholder-zinc-700 font-mono"
                                     />
                                </div>
                                <div className="w-24 relative">
                                     <input
                                        type="number"
                                        placeholder="100"
                                        value={tp.percentage}
                                        onChange={(e) => updateTp(idx, 'percentage', e.target.value)}
                                        className="w-full bg-black/50 border border-emerald-900/50 rounded-xl p-3 text-white text-sm focus:border-emerald-500 focus:outline-none text-center placeholder-zinc-700 font-mono"
                                     />
                                     <span className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-600 text-[10px] font-bold pointer-events-none">%</span>
                                </div>
                                {takeProfits.length > 1 && (
                                    <button 
                                        type="button" 
                                        onClick={() => removeTp(idx)} 
                                        className="p-3 text-zinc-500 hover:text-rose-500 bg-zinc-900 hover:bg-rose-500/10 rounded-xl border border-zinc-800 transition-colors"
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                )}
                            </div>
                        ))}
                      </div>

                      <Button fullWidth className="mt-4">Publish Signal</Button>
                  </form>
              </div>
          </div>
      )}

    </div>
  );
};