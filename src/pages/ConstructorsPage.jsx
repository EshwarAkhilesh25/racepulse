import React from 'react';
import { Link } from 'react-router-dom';
import { Shield, Trophy, Medal, ChevronRight, BarChart2 } from 'lucide-react';
import { useConstructors } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';
import TeamColorBadge from '../components/common/TeamColorBadge';

export default function ConstructorsPage() {
  const { data: constructors, isLoading, isError, refetch } = useConstructors();

  if (isLoading) return <LoadingState message="Loading constructor championship database..." />;
  if (isError || !constructors) return <ErrorState title="Constructors Database Offline" message="Could not load constructor data." onRetry={refetch} />;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header Banner */}
      <div className="glass-panel p-6">
        <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
          <Shield className="text-cyan-400" /> Constructor Intelligence Directory
        </h1>
        <p className="text-sm text-slate-400 mt-1">Car development pace index, points progression, and driver team dynamics.</p>
      </div>

      {/* Grid of Constructor Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {constructors.map((c) => (
          <GlassCard key={c.constructorId} className="flex flex-col justify-between space-y-4 hover:border-cyan-500/40 transition-all">
            <div>
              <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-3">
                <span className="text-xl font-black text-cyan-400 font-mono">P{c.position}</span>
                <TeamColorBadge color={c.teamColor} name={c.nationality} />
              </div>

              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-white">{c.name}</h2>
                <span className="text-lg font-black text-amber-400 font-mono">{c.points} PTS</span>
              </div>

              <div className="grid grid-cols-3 gap-2 p-2.5 bg-slate-900/60 rounded-xl text-center border border-slate-800 text-xs font-mono mb-4">
                <div>
                  <span className="text-slate-500 block text-[10px]">WINS</span>
                  <span className="font-bold text-slate-200">{c.wins}</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">PODIUMS</span>
                  <span className="font-bold text-slate-200">{c.podiums}</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">POLES</span>
                  <span className="font-bold text-cyan-400">{c.poles}</span>
                </div>
              </div>

              {c.drivers && c.drivers.length > 0 && (
                <div className="space-y-1.5 text-xs font-mono">
                  <span className="text-slate-500 text-[10px] uppercase">DRIVERS</span>
                  <div className="flex items-center gap-2">
                    {c.drivers.map(d => (
                      <span key={d.driverId} className="px-2 py-1 bg-slate-900 rounded border border-slate-800 text-slate-300">
                        {d.code} #{d.driverNumber}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <Link
              to={`/constructors/${c.constructorId}`}
              className="w-full inline-flex items-center justify-center gap-2 py-2 rounded-xl bg-cyan-500/10 hover:bg-cyan-500/20 text-cyan-400 border border-cyan-500/30 text-xs font-semibold transition-colors"
            >
              <BarChart2 size={14} /> Constructor Pace Analytics <ChevronRight size={14} />
            </Link>
          </GlassCard>
        ))}
      </div>

    </div>
  );
}
