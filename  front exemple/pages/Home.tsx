import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Star, TrendingUp, Users, Filter } from 'lucide-react';
import { GlassCard, NeonBadge } from '../components/Shared';
import { MOCK_TRADERS } from '../constants';
import { Trader } from '../types';

export const Home: React.FC = () => {
  const navigate = useNavigate();

  // FILTER: Only show traders the user is NOT subscribed to
  const marketTraders = MOCK_TRADERS.filter(t => !t.isSubscribed);

  return (
    <div className="p-5 space-y-8 animate-fade-in">
      
      {/* Hero Section */}
      <div className="pt-2 pb-4 space-y-1">
        <h2 className="text-3xl font-bold text-white tracking-tight leading-tight">
          Find your <br/>
          <span className="text-transparent bg-clip-text bg-shingo-gradient">Winning Edge.</span>
        </h2>
        <p className="text-zinc-400 text-sm">Discover elite traders. Subscribe to unlock signals.</p>
      </div>

      {/* Stats/Filters Bar Mock */}
      <div className="flex gap-3 overflow-x-auto no-scrollbar">
          <button className="flex items-center gap-2 bg-white/5 border border-white/5 rounded-full px-4 py-2 text-xs font-bold text-white whitespace-nowrap">
              <Filter size={14} /> All Strategies
          </button>
          <button className="bg-transparent border border-white/5 rounded-full px-4 py-2 text-xs font-bold text-zinc-500 whitespace-nowrap">
              High ROI
          </button>
           <button className="bg-transparent border border-white/5 rounded-full px-4 py-2 text-xs font-bold text-zinc-500 whitespace-nowrap">
              Scalping
          </button>
      </div>

      {/* Featured Grid */}
      <div className="grid grid-cols-1 gap-4">
        <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-zinc-500 uppercase tracking-wider">Top Performers</h3>
            <span className="text-xs text-primary-glow font-bold">{marketTraders.length} available</span>
        </div>
        
        {marketTraders.map((trader, idx) => (
          <TraderMarketCard 
            key={trader.id} 
            trader={trader} 
            rank={idx + 1}
            onClick={() => navigate(`/traders/${trader.id}`)} 
          />
        ))}
        
        {marketTraders.length === 0 && (
            <div className="py-10 text-center text-zinc-500 text-sm">
                You are subscribed to all available traders!
            </div>
        )}
      </div>
    </div>
  );
};

const TraderMarketCard: React.FC<{ trader: Trader; rank: number; onClick: () => void }> = ({ trader, rank, onClick }) => {
  return (
    <GlassCard onClick={onClick} className="group hover:border-primary-glow/30 relative overflow-hidden">
      {/* Background Glow */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-primary-glow/5 blur-3xl rounded-full -translate-y-1/2 translate-x-1/2 group-hover:bg-primary-glow/10 transition-colors"></div>
      
      <div className="flex items-center gap-4 relative z-10">
        {/* Rank & Avatar */}
        <div className="flex items-center gap-3">
            <img 
                src={trader.avatar} 
                alt={trader.name} 
                className="w-14 h-14 rounded-xl object-cover ring-2 ring-white/5 group-hover:ring-primary-glow/50 transition-all" 
            />
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
            <div className="flex justify-between items-start mb-1">
                <h3 className="font-bold text-white text-lg truncate">{trader.name}</h3>
                <div className="flex items-center gap-1 text-xs font-bold text-yellow-500 bg-yellow-500/10 px-1.5 py-0.5 rounded">
                    <Star size={10} fill="currentColor" /> {trader.rating}
                </div>
            </div>
            
            <div className="flex items-center gap-3 text-xs mb-1">
                <span className="text-zinc-500 flex items-center gap-1">
                    <Users size={12} /> {trader.subscribers} subs
                </span>
                <span className="text-zinc-600">•</span>
                <span className="text-primary-glow font-bold">${trader.price}/mo</span>
            </div>
        </div>

        {/* ROI Badge */}
        <div className="flex flex-col items-end gap-1">
            <span className="text-[10px] uppercase text-zinc-500 font-bold tracking-wider">ROI</span>
            <span className={`text-xl font-bold font-mono ${trader.roi >= 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
                {trader.roi >= 0 ? '+' : ''}{trader.roi}%
            </span>
        </div>
      </div>
    </GlassCard>
  );
};