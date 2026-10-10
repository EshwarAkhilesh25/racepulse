import React from 'react';
import { Activity } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-[#0b0e14] border-t border-slate-800/80 mt-16 py-8 text-slate-400 text-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          
          <div className="flex items-center gap-3">
            <div className="p-1.5 bg-cyan-500/10 rounded-lg border border-cyan-500/20 text-cyan-400">
              <Activity size={18} />
            </div>
            <div>
              <span className="font-bold text-slate-200 font-mono text-sm">RacePulse Platform</span>
              <p className="text-slate-500 text-[11px]">Real-Time Formula 1 Intelligence & Advanced Motorsport Analytics</p>
            </div>
          </div>

        </div>

        <div className="mt-6 pt-4 border-t border-slate-800/50 flex flex-col sm:flex-row justify-between items-center text-[11px] text-slate-500 gap-2">
          <p>© 2026 RacePulse Intelligence</p>
          <p>Unofficial Formula 1 fan analytics application.</p>
        </div>
      </div>
    </footer>
  );
}
