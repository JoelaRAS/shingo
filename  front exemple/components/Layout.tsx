import React from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Store, Radio, User, ChevronLeft, Hexagon } from 'lucide-react';

export const Layout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const isDetailView = location.pathname.includes('/traders/') || location.pathname.includes('/signal/');

  return (
    <div className="min-h-screen bg-bg text-slate-100 font-sans flex flex-col items-center selection:bg-primary-glow selection:text-white">
      <div className="w-full max-w-md min-h-screen bg-bg relative flex flex-col shadow-2xl shadow-black">
        
        {/* Header */}
        <header className="sticky top-0 z-50 bg-bg/80 backdrop-blur-xl border-b border-white/5 h-16 flex items-center px-5 justify-between">
            <div className="flex items-center gap-2">
                {isDetailView && (
                    <button 
                        onClick={() => navigate(-1)}
                        className="p-2 -ml-2 rounded-full hover:bg-white/5 text-zinc-400 hover:text-white transition-colors"
                    >
                        <ChevronLeft size={24} />
                    </button>
                )}
                {!isDetailView && (
                   <div className="flex items-center gap-2.5">
                       <div className="text-primary-glow animate-pulse-slow">
                            <Hexagon size={28} strokeWidth={2.5} />
                       </div>
                       <h1 className="text-2xl font-bold tracking-tight text-white">
                            Shingo
                       </h1>
                   </div>
                )}
            </div>
            
            {/* Header Title Spacer */}
            <div className="w-8"></div> 
        </header>

        {/* Main Content Area */}
        <main className="flex-1 overflow-y-auto no-scrollbar pb-28">
          <Outlet />
        </main>

        {/* Bottom Navigation */}
        <nav className="fixed bottom-0 w-full max-w-md bg-[#0a0a0c]/90 backdrop-blur-xl border-t border-white/5 pb-safe z-50">
          <div className="flex justify-around items-center h-20 px-2">
            <NavLink 
              to="/" 
              className={({ isActive }) => `flex flex-col items-center gap-1.5 p-3 rounded-2xl transition-all duration-300 ${isActive ? 'text-primary-glow bg-primary-glow/10' : 'text-zinc-500 hover:text-zinc-300'}`}
            >
              {({ isActive }) => (
                <>
                  <Store size={22} strokeWidth={isActive ? 2.5 : 2} />
                  <span className="text-[10px] font-bold uppercase tracking-wide">Market</span>
                </>
              )}
            </NavLink>
            <NavLink 
              to="/my-feed" 
              className={({ isActive }) => `flex flex-col items-center gap-1.5 p-3 rounded-2xl transition-all duration-300 ${isActive ? 'text-primary-glow bg-primary-glow/10' : 'text-zinc-500 hover:text-zinc-300'}`}
            >
              {({ isActive }) => (
                <>
                  <Radio size={22} strokeWidth={isActive ? 2.5 : 2} />
                  <span className="text-[10px] font-bold uppercase tracking-wide">My Signals</span>
                </>
              )}
            </NavLink>
            <NavLink 
              to="/profile" 
              className={({ isActive }) => `flex flex-col items-center gap-1.5 p-3 rounded-2xl transition-all duration-300 ${isActive ? 'text-primary-glow bg-primary-glow/10' : 'text-zinc-500 hover:text-zinc-300'}`}
            >
              {({ isActive }) => (
                <>
                  <User size={22} strokeWidth={isActive ? 2.5 : 2} />
                  <span className="text-[10px] font-bold uppercase tracking-wide">Profile</span>
                </>
              )}
            </NavLink>
          </div>
        </nav>
      </div>
    </div>
  );
};