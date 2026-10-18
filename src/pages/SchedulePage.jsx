import React from 'react';
import { Calendar, MapPin, Flag, Trophy, Clock, CheckCircle2 } from 'lucide-react';
import { useSchedule } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';

export default function SchedulePage() {
  const { data: schedule, isLoading, isError, refetch } = useSchedule();

  if (isLoading) return <LoadingState message="Loading 2026 Formula 1 Season Calendar..." />;
  if (isError || !schedule) return <ErrorState title="Schedule Offline" message="Could not load race calendar." onRetry={refetch} />;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header Banner */}
      <div className="glass-panel p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
            <Calendar className="text-cyan-400" /> 2026 World Championship Calendar
          </h1>
          <p className="text-sm text-slate-400 mt-1">24 Championship Rounds • Grand Prix Schedule & Sprint Weekends</p>
        </div>

        <div className="flex items-center gap-3 text-xs font-mono">
          <span className="px-3 py-1 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-lg">
            {schedule.filter(r => r.status === 'COMPLETED').length} Completed
          </span>
          <span className="px-3 py-1 bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 rounded-lg">
            {schedule.filter(r => r.status === 'UPCOMING' || r.status === 'LIVE').length} Upcoming
          </span>
        </div>

      </div>

      {/* Race Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {schedule.map((race) => {
          const isCompleted = race.status === 'COMPLETED';
          const isUpcoming = race.status === 'UPCOMING';
          const isLive = race.status === 'LIVE';

          return (
            <GlassCard key={race.round} className={`flex flex-col justify-between space-y-4 ${isLive ? 'border-red-500/50 shadow-[0_0_20px_rgba(255,24,1,0.2)]' : ''}`}>
              <div>
                <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-3">
                  <div className="flex items-center gap-2 font-mono">
                    <span className="text-xs font-bold text-cyan-400">ROUND {race.round}</span>
                    <span className="text-lg">{race.flagEmoji}</span>
                  </div>

                  <span className={`px-2.5 py-0.5 rounded text-[10px] font-mono font-bold ${
                    isCompleted ? 'bg-slate-800 text-slate-400' :
                    isLive ? 'bg-red-500/20 text-red-400 animate-pulse' :
                    'bg-cyan-500/20 text-cyan-300'
                  }`}>
                    {race.status}
                  </span>
                </div>

                <h2 className="text-lg font-bold text-white mb-1">{race.raceName}</h2>
                <p className="text-xs text-slate-400 flex items-center gap-1 mb-3">
                  <MapPin size={12} className="text-cyan-400" /> {race.circuitName}, {race.city}
                </p>

                {race.hasSprint && (
                  <span className="inline-block mb-3 px-2 py-0.5 rounded bg-purple-500/15 text-purple-300 border border-purple-500/30 text-[10px] font-mono font-semibold">
                    ⚡ SPRINT WEEKEND
                  </span>
                )}
              </div>

              <div className="pt-3 border-t border-slate-800/80 text-xs font-mono">
                {isCompleted ? (
                  <div className="flex items-center justify-between text-slate-300 bg-slate-900/60 p-2.5 rounded-lg border border-slate-800">
                    <span className="flex items-center gap-1.5 text-amber-400 font-semibold">
                      <Trophy size={14} /> Winner
                    </span>
                    <span className="font-bold">{race.winnerName} ({race.winningConstructor})</span>
                  </div>
                ) : (
                  <div className="flex items-center justify-between text-slate-400 bg-slate-900/60 p-2.5 rounded-lg border border-slate-800">
                    <span className="flex items-center gap-1.5 text-cyan-400">
                      <Clock size={14} /> Race Start
                    </span>
                    <span className="text-slate-200">{new Date(race.raceUTC).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}</span>
                  </div>
                )}
              </div>
            </GlassCard>
          );
        })}
      </div>

    </div>
  );
}
