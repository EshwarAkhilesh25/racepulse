import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { Shield, Trophy, ChevronLeft, TrendingUp, Users, Gauge, Activity } from 'lucide-react';
import { ResponsiveContainer, BarChart, Bar, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { useConstructorDetails } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';
import TeamColorBadge from '../components/common/TeamColorBadge';

export default function ConstructorProfilePage() {
  const { id } = useParams();
  const { data: statsData, isLoading, isError, refetch } = useConstructorDetails(id || 'mclaren');

  if (isLoading) return <LoadingState message="Analyzing constructor development & pace metrics..." />;
  if (isError || !statsData) return <ErrorState title="Constructor Details Offline" message="Could not load constructor details." onRetry={refetch} />;

  const { constructor, recentFormPoints, avgQualifyingPosition, avgRacePaceIndex, driverComparison, historicalPerformance } = statsData;

  const formChartData = (recentFormPoints || []).map((pts, idx) => ({
    Round: `R${idx + 1}`,
    Points: pts,
  }));

  const historyChartData = (historicalPerformance || []).map((h) => ({
    Season: h.season,
    Points: h.points,
    Wins: h.wins,
  }));

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      <Link to="/constructors" className="inline-flex items-center gap-1 text-xs font-mono text-cyan-400 hover:underline">
        <ChevronLeft size={16} /> Back to Constructors Directory
      </Link>

      {/* Header Hero Banner */}
      <div className="glass-panel-accent p-6 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-slate-900 border-2 border-cyan-500/40 flex items-center justify-center font-mono font-black text-2xl text-cyan-400">
            P{constructor.position}
          </div>
          <div>
            <div className="flex items-center gap-2 mb-1">
              <TeamColorBadge color={constructor.teamColor} name={constructor.nationality} />
            </div>
            <h1 className="text-3xl font-black text-white">{constructor.name}</h1>
            <span className="text-xs font-mono text-slate-400">CHAMPIONSHIP STANDINGS POSITION: P{constructor.position}</span>
          </div>
        </div>

        <div className="grid grid-cols-4 gap-3 bg-slate-900/80 p-4 rounded-xl border border-slate-800 text-center font-mono text-xs">
          <div>
            <span className="text-slate-500 block text-[10px]">TOTAL PTS</span>
            <span className="text-amber-400 font-black text-lg">{constructor.points}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[10px]">WINS</span>
            <span className="text-slate-200 font-bold text-lg">{constructor.wins}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[10px]">PODIUMS</span>
            <span className="text-slate-200 font-bold text-lg">{constructor.podiums}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[10px]">PACE INDEX</span>
            <span className="text-cyan-400 font-bold text-lg">{avgRacePaceIndex}%</span>
          </div>
        </div>
      </div>

      {/* Charts & Comparisons */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Recent Points Scoring Form */}
        <GlassCard className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <TrendingUp size={18} className="text-cyan-400" /> Scoring Progression (Recent Rounds)
            </h2>
            <span className="text-xs font-mono text-slate-400">Constructor Points / Race</span>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={formChartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="Round" stroke="#64748b" />
                <YAxis stroke="#64748b" />
                <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
                <Bar dataKey="Points" fill="#00F0FF" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </GlassCard>

        {/* Teammate Driver Points Distribution */}
        <GlassCard className="space-y-4">
          <h2 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
            <Users size={18} className="text-amber-400" /> Driver Points Breakdown
          </h2>

          {driverComparison && (
            <div className="space-y-4 font-mono text-xs">
              {driverComparison.driver1 && (
                <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex justify-between">
                    <span className="font-bold text-slate-200">{driverComparison.driver1.fullName}</span>
                    <span className="text-amber-400 font-bold">{driverComparison.driver1Points} PTS</span>
                  </div>
                  <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden">
                    <div className="bg-cyan-400 h-full rounded-full" style={{ width: `${(driverComparison.driver1Points / (driverComparison.driver1Points + driverComparison.driver2Points)) * 100}%` }}></div>
                  </div>
                </div>
              )}

              {driverComparison.driver2 && (
                <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex justify-between">
                    <span className="font-bold text-slate-200">{driverComparison.driver2.fullName}</span>
                    <span className="text-amber-400 font-bold">{driverComparison.driver2Points} PTS</span>
                  </div>
                  <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden">
                    <div className="bg-purple-500 h-full rounded-full" style={{ width: `${(driverComparison.driver2Points / (driverComparison.driver1Points + driverComparison.driver2Points)) * 100}%` }}></div>
                  </div>
                </div>
              )}
            </div>
          )}
        </GlassCard>
      </div>

    </div>
  );
}
