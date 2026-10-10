import React from 'react';
import { BarChart3, TrendingUp, Users, Target, Activity } from 'lucide-react';
import { ResponsiveContainer, BarChart, Bar, ScatterChart, Scatter, XAxis, YAxis, Tooltip, CartesianGrid, Legend } from 'recharts';
import { useAnalytics } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';

export default function AnalyticsPage() {
  const { data: analytics, isLoading, isError, refetch } = useAnalytics();

  if (isLoading) return <LoadingState message="Processing multi-variable driver & constructor analytics..." />;
  if (isError || !analytics) return <ErrorState title="Analytics Engine Offline" message="Could not fetch race analytics." onRetry={refetch} />;

  const { driverConsistency, qualiVsRacePerformance, positionGainLoss, constructorPaceAnalysis, teammateHeadToHead } = analytics;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header */}
      <div className="glass-panel p-6">
        <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
          <BarChart3 className="text-cyan-400" /> Race Intelligence Analytics
        </h1>
        <p className="text-sm text-slate-400 mt-1">Consistency scores, qualifying vs race conversion deltas, and constructor pace indices.</p>
      </div>

      {/* Grid: Consistency & Gain/Loss Bar Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        
        {/* Driver Consistency Index Chart */}
        <GlassCard className="space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Activity size={18} className="text-cyan-400" /> Driver Consistency Index (0-100)
            </h2>
            <span className="text-xs font-mono text-slate-400">Higher is more consistent</span>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={driverConsistency}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="driverCode" stroke="#64748b" />
                <YAxis domain={[50, 100]} stroke="#64748b" />
                <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
                <Bar dataKey="consistencyScore" fill="#00F0FF" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </GlassCard>

        {/* Position Gain/Loss Chart */}
        <GlassCard className="space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <TrendingUp size={18} className="text-emerald-400" /> Net Position Gains Total
            </h2>
            <span className="text-xs font-mono text-slate-400">Race Day Overtakes & Gains</span>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={positionGainLoss}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="driverCode" stroke="#64748b" />
                <YAxis stroke="#64748b" />
                <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
                <Bar dataKey="netPositionsGained" fill="#00E676" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </GlassCard>
      </div>

      {/* Qualifying vs Race Pace Analysis Matrix */}
      <GlassCard className="space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <Target size={18} className="text-amber-400" /> Qualifying Avg Pos vs Race Avg Pos
          </h2>
          <span className="text-xs font-mono text-slate-400">Qualifying conversion efficiency</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs font-mono">
            <thead>
              <tr className="text-slate-400 border-b border-slate-800 uppercase">
                <th className="pb-2">DRIVER</th>
                <th className="pb-2">AVG QUALI POS</th>
                <th className="pb-2">AVG RACE FINISH</th>
                <th className="pb-2 text-right">CONVERSION DELTA</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/40">
              {qualiVsRacePerformance.map((item) => (
                <tr key={item.driverCode} className="hover:bg-slate-800/30">
                  <td className="py-2.5 font-bold text-slate-100">{item.driverName} ({item.driverCode})</td>
                  <td className="py-2.5 text-slate-300">P{item.avgQualifyingPosition.toFixed(1)}</td>
                  <td className="py-2.5 text-cyan-400 font-semibold">P{item.avgFinishPosition.toFixed(1)}</td>
                  <td className="py-2.5 text-right font-bold text-emerald-400">{item.netDelta < 0 ? `+${Math.abs(item.netDelta).toFixed(1)} Pos` : `${item.netDelta.toFixed(1)} Pos`}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </GlassCard>

    </div>
  );
}
