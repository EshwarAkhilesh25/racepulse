import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Trophy, 
  Flag, 
  Clock, 
  MapPin, 
  CloudSun, 
  AlertCircle, 
  ChevronRight, 
  TrendingUp, 
  Zap, 
  ShieldCheck, 
  BarChart3 
} from 'lucide-react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { useDashboard } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';
import TeamColorBadge from '../components/common/TeamColorBadge';

export default function OverviewPage() {
  const { data: dashboard, isLoading, isError, refetch } = useDashboard();
  const [timeLeft, setTimeLeft] = useState({ days: 0, hours: 0, minutes: 0, seconds: 0 });

  useEffect(() => {
    if (!dashboard?.nextRace?.raceUTC) return;
    const target = new Date(dashboard.nextRace.raceUTC).getTime();

    const interval = setInterval(() => {
      const now = new Date().getTime();
      const diff = target - now;

      if (diff <= 0) {
        setTimeLeft({ days: 0, hours: 0, minutes: 0, seconds: 0 });
      } else {
        setTimeLeft({
          days: Math.floor(diff / (1000 * 60 * 60 * 24)),
          hours: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
          minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
          seconds: Math.floor((diff % (1000 * 60)) / 1000),
        });
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [dashboard]);

  if (isLoading) return <LoadingState message="Fetching live RacePulse intelligence telemetry..." />;
  if (isError || !dashboard) return <ErrorState title="Dashboard Unavailable" message="Failed to connect to RacePulse API service." onRetry={refetch} />;

  const { nextRace, latestRace, topDrivers, topConstructors, recentAlerts, trackInfo, currentWeather } = dashboard;

  const pointsTrendData = (topDrivers || []).slice(0, 5).map(d => ({
    name: d.code,
    Points: d.points,
    Wins: d.wins * 25,
  }));

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Top Hero Banner: Next Race Countdown & Track Overview */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Next Race Hero Card */}
        <div className="lg:col-span-2 glass-panel-accent p-6 relative overflow-hidden flex flex-col justify-between">
          <div className="absolute top-0 right-0 w-64 h-64 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none"></div>

          <div>
            <div className="flex items-center justify-between gap-2 mb-3">
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 text-xs font-mono font-bold tracking-wider">
                <Flag size={14} /> NEXT GRAND PRIX • ROUND {nextRace?.round || 18}
              </span>
              <span className="text-xs text-slate-400 font-mono">
                {nextRace?.flagEmoji} {nextRace?.country}
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-black text-white tracking-tight mb-1">
              {nextRace?.raceName || 'Singapore Grand Prix'}
            </h1>
            <p className="text-sm text-slate-400 flex items-center gap-2">
              <MapPin size={14} className="text-cyan-400" /> {nextRace?.circuitName || 'Marina Bay Street Circuit'} • {nextRace?.city}
            </p>
          </div>

          {/* Countdown timer blocks */}
          <div className="my-6 grid grid-cols-4 gap-3 max-w-md">
            {[
              { label: 'DAYS', val: timeLeft.days },
              { label: 'HOURS', val: timeLeft.hours },
              { label: 'MINS', val: timeLeft.minutes },
              { label: 'SECS', val: timeLeft.seconds }
            ].map((unit, idx) => (
              <div key={idx} className="bg-slate-900/80 border border-slate-800 rounded-xl p-3 text-center">
                <span className="block text-2xl sm:text-3xl font-black text-cyan-400 font-mono">
                  {String(unit.val).padStart(2, '0')}
                </span>
                <span className="text-[10px] text-slate-400 font-mono tracking-widest">{unit.label}</span>
              </div>
            ))}
          </div>

          <div className="flex flex-wrap items-center gap-4 pt-2 border-t border-slate-800/80">
            <Link
              to="/live"
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-gradient-to-r from-red-600 to-cyan-600 text-white font-bold text-sm shadow-lg hover:shadow-cyan-500/20 transition-all"
            >
              <Zap size={16} /> Enter Live Telemetry Center
            </Link>
            <Link
              to="/calendar"
              className="inline-flex items-center gap-2 text-xs text-slate-400 hover:text-cyan-400 transition-colors"
            >
              View Full Season Calendar <ChevronRight size={14} />
            </Link>
          </div>
        </div>

        {/* Live Weather & Track Specs Card */}
        <GlassCard className="flex flex-col justify-between space-y-4">
          <div>
            <h3 className="text-xs font-mono uppercase text-slate-400 tracking-wider mb-3 flex items-center gap-2">
              <CloudSun size={16} className="text-amber-400" /> Track & Environmental Specs
            </h3>

            {trackInfo && (
              <div className="space-y-2.5 mb-4 text-sm">
                <div className="flex justify-between py-1 border-b border-slate-800">
                  <span className="text-slate-400">Circuit Length</span>
                  <span className="font-mono text-slate-200 font-semibold">{trackInfo.lengthKm} km</span>
                </div>
                <div className="flex justify-between py-1 border-b border-slate-800">
                  <span className="text-slate-400">Corners</span>
                  <span className="font-mono text-slate-200 font-semibold">{trackInfo.corners}</span>
                </div>
                <div className="flex justify-between py-1 border-b border-slate-800">
                  <span className="text-slate-400">DRS Zones</span>
                  <span className="font-mono text-slate-200 font-semibold">{trackInfo.drsZones} Zones</span>
                </div>
                <div className="flex justify-between py-1">
                  <span className="text-slate-400">Lap Record</span>
                  <span className="font-mono text-cyan-400 font-semibold">{trackInfo.lapRecord}</span>
                </div>
              </div>
            )}

            {currentWeather && (
              <div className="bg-slate-900/60 rounded-xl p-3 border border-slate-800/80 grid grid-cols-2 gap-2 text-xs">
                <div>
                  <span className="text-slate-500 block text-[10px]">AIR TEMP</span>
                  <span className="font-mono text-slate-200 font-bold">{currentWeather.airTempCelsius}°C</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">TRACK TEMP</span>
                  <span className="font-mono text-amber-400 font-bold">{currentWeather.trackTempCelsius}°C</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">HUMIDITY</span>
                  <span className="font-mono text-slate-200">{currentWeather.humidityPercent}%</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px]">WIND SPEED</span>
                  <span className="font-mono text-slate-200">{currentWeather.windSpeedKmh} km/h</span>
                </div>
              </div>
            )}
          </div>
        </GlassCard>
      </div>

      {/* Main Grid: Drivers & Constructors Performance Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Driver Championship Leaderboard */}
        <GlassCard className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <Trophy size={18} className="text-amber-400" /> Driver Championship Intelligence
            </h2>
            <Link to="/drivers" className="text-xs text-cyan-400 hover:underline flex items-center gap-1">
              View All <ChevronRight size={14} />
            </Link>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="text-[11px] font-mono uppercase text-slate-500 border-b border-slate-800">
                  <th className="pb-2">POS</th>
                  <th className="pb-2">DRIVER</th>
                  <th className="pb-2">CONSTRUCTOR</th>
                  <th className="pb-2 text-right">WINS</th>
                  <th className="pb-2 text-right">POINTS</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/50">
                {(topDrivers || []).slice(0, 6).map((driver) => (
                  <tr key={driver.driverId} className="hover:bg-slate-800/30 transition-colors">
                    <td className="py-2.5 font-mono font-bold text-cyan-400">P{driver.position}</td>
                    <td className="py-2.5 font-bold text-slate-100">
                      <Link to={`/driver/${driver.driverId}`} className="hover:text-cyan-400 transition-colors flex items-center gap-2">
                        <span>{driver.fullName}</span>
                        <span className="text-[10px] text-slate-500 font-mono">#{driver.driverNumber}</span>
                      </Link>
                    </td>
                    <td className="py-2.5">
                      <TeamColorBadge color={driver.teamColor} name={driver.constructorName} />
                    </td>
                    <td className="py-2.5 text-right font-mono text-slate-300">{driver.wins}</td>
                    <td className="py-2.5 text-right font-mono font-black text-amber-400">{driver.points} PTS</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </GlassCard>

        {/* Constructor Performance Standings */}
        <GlassCard className="space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <ShieldCheck size={18} className="text-cyan-400" /> Constructor Standings
            </h2>
            <Link to="/constructors" className="text-xs text-cyan-400 hover:underline flex items-center gap-1">
              Details <ChevronRight size={14} />
            </Link>
          </div>

          <div className="space-y-3">
            {(topConstructors || []).slice(0, 5).map((team) => (
              <div key={team.constructorId} className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex items-center justify-between">
                <div>
                  <TeamColorBadge color={team.teamColor} name={team.name} />
                  <span className="block text-[10px] text-slate-500 font-mono mt-0.5">{team.wins} Victories • {team.podiums} Podiums</span>
                </div>
                <div className="text-right font-mono">
                  <span className="block text-sm font-bold text-white">{team.points} PTS</span>
                  <span className="text-[10px] text-cyan-400 font-semibold">P{team.position}</span>
                </div>
              </div>
            ))}
          </div>
        </GlassCard>
      </div>

      {/* Interactive Charts & Live Telemetry Alert Ticker */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Interactive Points Chart */}
        <GlassCard className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <BarChart3 size={18} className="text-indigo-400" /> Top Driver Championship Trajectory
            </h3>
            <span className="text-xs text-slate-400 font-mono">2026 Season Points</span>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={pointsTrendData}>
                <defs>
                  <linearGradient id="colorPoints" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#00F0FF" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#00F0FF" stopOpacity={0.0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="name" stroke="#64748b" />
                <YAxis stroke="#64748b" />
                <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
                <Area type="monotone" dataKey="Points" stroke="#00F0FF" strokeWidth={3} fillOpacity={1} fill="url(#colorPoints)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </GlassCard>

        {/* Live Race Alerts Feed */}
        <GlassCard className="space-y-4">
          <h3 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
            <AlertCircle size={18} className="text-red-400" /> Live RacePulse Intelligence Feed
          </h3>

          <div className="space-y-3 max-h-64 overflow-y-auto pr-1">
            {(recentAlerts || []).map((alert) => (
              <div key={alert.id} className="p-3 bg-slate-900/60 rounded-xl border border-slate-800/80 space-y-1">
                <div className="flex items-center justify-between text-[11px]">
                  <span className="font-mono font-bold text-cyan-400">{alert.category}</span>
                  <span className="text-slate-500">{alert.timestamp}</span>
                </div>
                <h4 className="text-xs font-bold text-slate-200">{alert.title}</h4>
                <p className="text-[11px] text-slate-400 leading-snug">{alert.message}</p>
              </div>
            ))}
          </div>
        </GlassCard>
      </div>

    </div>
  );
}
