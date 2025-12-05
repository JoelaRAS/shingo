import React from 'react';
import { Direction, SignalStatus } from '../types';
import { ArrowUp, ArrowDown, Lock, Unlock, Zap } from 'lucide-react';

// --- Visual Wrappers ---

export const GlassCard: React.FC<{ children: React.ReactNode; className?: string; onClick?: () => void }> = ({ children, className = '', onClick }) => {
  return (
    <div 
      onClick={onClick}
      className={`glass-panel rounded-2xl p-4 transition-all duration-300 ${onClick ? 'cursor-pointer hover:bg-white/5 active:scale-[0.99]' : ''} ${className}`}
    >
      {children}
    </div>
  );
};

export const NeonBadge: React.FC<{ children: React.ReactNode; color?: 'blue' | 'purple' | 'green' | 'red' }> = ({ children, color = 'blue' }) => {
  const colors = {
    blue: 'bg-blue-500/10 text-blue-400 border-blue-500/20 shadow-blue-500/10',
    purple: 'bg-purple-500/10 text-purple-400 border-purple-500/20 shadow-purple-500/10',
    green: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20 shadow-emerald-500/10',
    red: 'bg-rose-500/10 text-rose-400 border-rose-500/20 shadow-rose-500/10',
  };

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-bold border shadow-[0_0_10px_0px_rgba(0,0,0,0)] ${colors[color]}`}>
      {children}
    </span>
  );
};

// --- Functional Badges ---

export const StatusBadge: React.FC<{ status: SignalStatus }> = ({ status }) => {
  const isExpired = status === SignalStatus.EXPIRED;
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
      isExpired 
        ? 'bg-zinc-800 text-zinc-500 border border-zinc-700' 
        : 'bg-primary-glow/10 text-primary-glow border border-primary-glow/20 animate-pulse-slow'
    }`}>
      {isExpired ? <Unlock size={10} /> : <Lock size={10} />}
      {status}
    </span>
  );
};

export const DirectionBadge: React.FC<{ direction: Direction }> = ({ direction }) => {
  const isLong = direction === Direction.LONG;
  return (
    <div className={`flex items-center gap-1.5 ${isLong ? 'text-trade-long' : 'text-trade-short'} font-bold`}>
      <div className={`p-1 rounded bg-current/10`}>
        {isLong ? <ArrowUp size={14} strokeWidth={3} /> : <ArrowDown size={14} strokeWidth={3} />}
      </div>
      <span className="text-sm tracking-wide">{direction}</span>
    </div>
  );
};

// --- Charts & Bars ---

export const ConfidenceBar: React.FC<{ level: number }> = ({ level }) => {
  return (
    <div className="flex flex-col gap-1.5 w-full max-w-[100px]">
      <div className="flex justify-between text-[10px] uppercase text-zinc-500 font-bold tracking-wider">
        <span>Confidence</span>
        <span className="text-white">{level}/10</span>
      </div>
      <div className="flex gap-0.5 h-1.5 w-full">
        {[...Array(10)].map((_, i) => (
          <div 
            key={i}
            className={`flex-1 rounded-sm ${i < level ? (level > 7 ? 'bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]' : 'bg-primary-glow shadow-[0_0_8px_rgba(129,140,248,0.5)]') : 'bg-zinc-800'}`}
          />
        ))}
      </div>
    </div>
  );
};

// --- Buttons ---

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  fullWidth?: boolean;
}

export const Button: React.FC<ButtonProps> = ({ 
  children, 
  variant = 'primary', 
  fullWidth = false, 
  className = '',
  ...props 
}) => {
  const baseStyles = "px-5 py-3.5 rounded-xl font-bold text-sm tracking-wide transition-all active:scale-[0.97] disabled:opacity-50 disabled:cursor-not-allowed flex justify-center items-center gap-2";
  
  const variants = {
    primary: "bg-shingo-gradient text-white shadow-[0_4px_20px_-4px_rgba(99,102,241,0.5)] border border-white/10 hover:shadow-[0_4px_25px_-4px_rgba(99,102,241,0.7)]",
    secondary: "bg-zinc-800 text-white hover:bg-zinc-700 border border-zinc-700",
    outline: "bg-transparent border border-zinc-700 text-zinc-300 hover:border-zinc-500 hover:text-white",
    ghost: "bg-transparent text-zinc-400 hover:text-white"
  };

  return (
    <button 
      className={`${baseStyles} ${variants[variant]} ${fullWidth ? 'w-full' : ''} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};