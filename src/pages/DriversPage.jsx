import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Users, Search, Trophy, Medal, ChevronRight, BarChart2 } from 'lucide-react';
import { useDrivers } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';
import TeamColorBadge from '../components/common/TeamColorBadge';

export default function DriversPage() {
  const { data: drivers, isLoading, isError, refetch } = useDrivers();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTeam, setSelectedTeam] = useState('ALL');

  if (isLoading) return <LoadingState message="Loading driver intelligence database..." />;
  if (isError || !drivers) return <ErrorState title="Driver Database Offline" message="Could not load driver profiles." onRetry={refetch} />;

  const teams = ['ALL', ...new Set(drivers.map(d => d.constructorName))];

  const filteredDrivers = drivers.filter(d => {
    const matchesSearch = d.fullName.toLowerCase().includes(searchTerm.toLowerCase()) || d.code.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesTeam = selectedTeam === 'ALL' || d.constructorName === selectedTeam;
    return matchesSearch && matchesTeam;
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header & Filters */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 glass-panel p-6">
        <div>
          <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
            <Users className="text-cyan-400" /> Driver Intelligence Hub
          </h1>
          <p className="text-sm text-slate-400 mt-1">Detailed performance metrics, qualifying pace, teammate head-to-head analysis.</p>
        </div>

        {/* Search & Team Filter */}
        <div className="flex flex-wrap items-center gap-3">
          <div className="relative min-w-[200px]">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              type="text"
              placeholder="Search driver or code..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700/80 rounded-xl pl-9 pr-4 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
            />
          </div>

          <select
            value={selectedTeam}
            onChange={(e) => setSelectedTeam(e.target.value)}
            className="bg-slate-900 border border-slate-700/80 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-cyan-400"
          >
            {teams.map(t => (
              <option key={t} value={t}>{t === 'ALL' ? 'All Constructors' : t}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Driver Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredDrivers.map((driver) => (
          <GlassCard key={driver.driverId} className="flex flex-col justify-between space-y-4 hover:border-cyan-500/40 transition-all">
            <div>
              <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-3">
                <div className="flex items-center gap-2">
                  <span className="text-xl font-black text-cyan-400 font-mono">P{driver.position}</span>
                  <TeamColorBadge color={driver.teamColor} name={driver.constructorName} />
                </div>
                <span className="text-xs font-mono font-bold text-slate-500">#{driver.driverNumber}</span>
              </div>

              <div className="flex items-center justify-between mb-4">
                <div>
                  <h2 className="text-lg font-bold text-white leading-tight">{driver.fullName}</h2>
                  <span className="text-xs font-mono text-cyan-400 tracking-wider font-semibold">{driver.code}</span>
                </div>
                <div className="text-right font-mono">
                  <span className="text-lg font-black text-amber-400 block">{driver.points} PTS</span>
                </div>
              </div>

              {/* Stats Summary Grid */}
              <div className="grid grid-cols-3 gap-2 p-2.5 bg-slate-900/60 rounded-xl text-center border border-slate-800 text-xs font-mono mb-4">
                <div>
                  <span className="text-slate-500 block text-[10px]">WINS</span>
                  <span className="font-bold text-slate-200">{driver.wins}</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">PODIUMS</span>
                  <span className="font-bold text-slate-200">{driver.podiums}</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">AVG FINISH</span>
                  <span className="font-bold text-cyan-400">P{driver.averageFinishPosition}</span>
                </div>
              </div>
            </div>

            <Link
              to={`/driver/${driver.driverId}`}
              className="w-full inline-flex items-center justify-center gap-2 py-2 rounded-xl bg-cyan-500/10 hover:bg-cyan-500/20 text-cyan-400 border border-cyan-500/30 text-xs font-semibold transition-colors"
            >
              <BarChart2 size={14} /> View Full Driver Intelligence <ChevronRight size={14} />
            </Link>
          </GlassCard>
        ))}
      </div>

    </div>
  );
}
