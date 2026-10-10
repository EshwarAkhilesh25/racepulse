import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { 
  Trophy, 
  Medal, 
  Flag, 
  ChevronLeft, 
  TrendingUp, 
  Activity, 
  Users, 
  Gauge, 
  Zap 
} from 'lucide-react';
import { ResponsiveContainer, LineChart, Line, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { useDriverDetails } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';
import TeamColorBadge from '../components/common/TeamColorBadge';

export default function DriverProfilePage() {
  const { driverNumber } = useParams();
  const { data: statsData, isLoading, isError, refetch } = useDriverDetails(driverNumber || 'norris');

  if (isLoading) return <LoadingState message="Calculating driver telemetry performance statistics..." />;
  if (isError || !statsData) return <ErrorState title="Driver Profile Offline" message="Could not load driver details." onRetry={refetch} />;

  const { driver, recentFormPositions, qualifyingAveragePosition, raceAveragePosition, positionGainsTotal, teammateComparison, historicalPerformance, telemetrySummary } = statsData;

  const formChartData = (recentFormPositions || []).map((pos, idx) => ({
    Race: `R${idx + 1}`,
    Position: pos,
  }));

  const historyChartData = (historicalPerformance || []).map((h) => ({
    Season: h.season,
    Points: h.points,
    Wins: h.wins,
  }));

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Back Button */}
      <Link to="/drivers" className="inline-flex items-center gap-1 text-xs font-mono text-cyan-400 hover:underline">
        <ChevronLeft size={16} /> Back to Drivers Directory
      </Link>

      {/* Driver Profile Header Hero */}
      <div className="glass-panel-accent p-6 flex flex-col md:flex-row md:items-center justify-between gap-6 relative overflow-hidden">
        <div className="flex items-center gap-5">
          <div className="w-16 h-16 rounded-2xl bg-slate-900 border-2 border-cyan-500/40 flex items-center justify-center font-mono font-black text-2xl text-cyan-400 shadow-[0_0_20px_rgba(0,240,255,0.2)]">
            #{driver.driverNumber}
          </div>
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="px-2.5 py-0.5 rounded-full bg-cyan-500/10 text-cyan-400 font-mono text-xs font-bold border border-cyan-500/20">
                P{driver.position} CHAMPIONSHIP
              </span>
              <TeamColorBadge color={driver.teamColor} name={driver.constructorName} />
            </div>
            <h1 className="text-3xl font-black text-white">{driver.fullName}</h1>
            <span className="text-xs font-mono text-slate-400 tracking-wider">DRIVER CODE: {driver.code}</span>
          </div>
        </div>

        {/* Primary Stats Block */}
        <div className="grid grid-cols-4 gap-3 bg-slate-900/80 p-4 rounded-xl border border-slate-800 text-center font-mono text-xs">
          <div>
            <span className="text-slate-500 block text-[10px]">POINTS</span>
            <span className="text-amber-400 font-black text-lg">{driver.points}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[10px]">WINS</span>
            <span className="text-slate-200 font-bold text-lg">{driver.wins}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[10px]">PODIUMS</span>
            <span className="text-slate-200 font-bold text-lg">{driver.podiums}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[10px]">POLES</span>
            <span className="text-cyan-400 font-bold text-lg">{driver.poles}</span>
          </div>
        </div>
      </div>

      {/* Main Analytics Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Interactive Recent Form Chart */}
        <GlassCard className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <TrendingUp size={18} className="text-cyan-400" /> Recent Race Finishing Form (Last 5 GP)
            </h2>
            <span className="text-xs font-mono text-slate-400">Lower is better</span>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={formChartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="Race" stroke="#64748b" />
                <YAxis reversed domain={[1, 10]} stroke="#64748b" />
                <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
                <Line type="monotone" dataKey="Position" stroke="#00F0FF" strokeWidth={3} dot={{ r: 6, fill: '#00F0FF' }} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </GlassCard>

        {/* Teammate Head-to-Head Comparison */}
        <GlassCard className="space-y-4">
          <h2 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
            <Users size={18} className="text-amber-400" /> Teammate H2H Comparison
          </h2>

          {teammateComparison && (
            <div className="space-y-3 font-mono text-xs">
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between items-center">
                <span className="text-slate-400">Teammate</span>
                <span className="font-bold text-slate-200">{teammateComparison.teammateName}</span>
              </div>
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between items-center">
                <span className="text-slate-400">Qualifying H2H</span>
                <span className="font-bold text-cyan-400">{teammateComparison.qualifyingHeadToHeadWins} Wins</span>
              </div>
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between items-center">
                <span className="text-slate-400">Race H2H</span>
                <span className="font-bold text-cyan-400">{teammateComparison.raceHeadToHeadWins} Wins</span>
              </div>
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between items-center">
                <span className="text-slate-400">Points Gap</span>
                <span className="font-bold text-emerald-400">+{teammateComparison.pointsDifference} PTS</span>
              </div>
            </div>
          )}
        </GlassCard>
      </div>

      {/* Historical Performance & Detailed Telemetry Metrics */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        
        {/* Historical Season Bar Chart */}
        <GlassCard className="space-y-4">
          <h2 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
            <Trophy size={18} className="text-amber-400" /> Multi-Season Points Career Progress
          </h2>

          <div className="h-60 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={historyChartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="Season" stroke="#64748b" />
                <YAxis stroke="#64748b" />
                <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
                <Bar dataKey="Points" fill="#7A28FF" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </GlassCard>

        {/* Telemetry Driving Style Specs */}
        <GlassCard className="space-y-4">
          <h2 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
            <Gauge size={18} className="text-cyan-400" /> Telemetry & Driving Style Indicators
          </h2>

          {telemetrySummary && (
            <div className="space-y-3 font-mono text-xs">
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between">
                <span className="text-slate-400">Full Throttle Time %</span>
                <span className="font-bold text-cyan-400">{telemetrySummary.maxThrottleTimePercent}%</span>
              </div>
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between">
                <span className="text-slate-400">Heavy Braking Efficiency</span>
                <span className="font-bold text-emerald-400">{telemetrySummary.heavyBrakingEfficiencyScore} / 100</span>
              </div>
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between">
                <span className="text-slate-400">Avg Corner Apex Speed</span>
                <span className="font-bold text-slate-200">{telemetrySummary.apexSpeedAvgKmh} km/h</span>
              </div>
              <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 flex justify-between">
                <span className="text-slate-400">Tyre Degradation Index</span>
                <span className="font-bold text-amber-400">{telemetrySummary.tyreDegradationIndex} (Low Wear)</span>
              </div>
            </div>
          )}
        </GlassCard>
      </div>

    </div>
  );
}
