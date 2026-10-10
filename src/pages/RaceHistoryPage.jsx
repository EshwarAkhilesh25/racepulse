import React, { useState } from 'react';
import { History, Trophy, Calendar, MapPin, Search } from 'lucide-react';
import { useSchedule } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';

export default function RaceHistoryPage() {
  const { data: schedule, isLoading, isError, refetch } = useSchedule();
  const [season, setSeason] = useState('2026');
  const [filterText, setFilterText] = useState('');

  if (isLoading) return <LoadingState message="Loading historical Formula 1 Grand Prix archive..." />;
  if (isError || !schedule) return <ErrorState title="History Archive Offline" message="Could not load race history." onRetry={refetch} />;

  const completedRaces = schedule.filter(r => r.status === 'COMPLETED' && (
    r.raceName.toLowerCase().includes(filterText.toLowerCase()) ||
    r.country.toLowerCase().includes(filterText.toLowerCase()) ||
    (r.winnerName && r.winnerName.toLowerCase().includes(filterText.toLowerCase()))
  ));

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header */}
      <div className="glass-panel p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
            <History className="text-cyan-400" /> Grand Prix Race History & Archive
          </h1>
          <p className="text-sm text-slate-400 mt-1">Completed race summaries, winning constructors, and historical records.</p>
        </div>

        <div className="flex items-center gap-3">
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              type="text"
              placeholder="Search race or winner..."
              value={filterText}
              onChange={(e) => setFilterText(e.target.value)}
              className="bg-slate-900 border border-slate-700/80 rounded-xl pl-9 pr-4 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
            />
          </div>

          <select
            value={season}
            onChange={(e) => setSeason(e.target.value)}
            className="bg-slate-900 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-cyan-400"
          >
            <option value="2026">2026 Season</option>
            <option value="2025">2025 Season</option>
            <option value="2024">2024 Season</option>
          </select>
        </div>
      </div>

      {/* History Archive Table */}
      <GlassCard className="space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs font-mono">
            <thead>
              <tr className="text-slate-400 border-b border-slate-800 uppercase">
                <th className="pb-3">ROUND</th>
                <th className="pb-3">GRAND PRIX</th>
                <th className="pb-3">LOCATION</th>
                <th className="pb-3">WINNING DRIVER</th>
                <th className="pb-3 text-right">CONSTRUCTOR</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/40">
              {completedRaces.map((race) => (
                <tr key={race.round} className="hover:bg-slate-800/30 transition-colors">
                  <td className="py-3 font-bold text-cyan-400">R{race.round}</td>
                  <td className="py-3 font-bold text-slate-100">{race.raceName}</td>
                  <td className="py-3 text-slate-400">{race.flagEmoji} {race.city}, {race.country}</td>
                  <td className="py-3 font-bold text-amber-400 flex items-center gap-1.5">
                    <Trophy size={14} /> {race.winnerName || 'Lando Norris'}
                  </td>
                  <td className="py-3 text-right text-slate-300">{race.winningConstructor || 'McLaren'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </GlassCard>

    </div>
  );
}
