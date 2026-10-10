import React from 'react';
import { Sparkles, Info, TrendingUp, Target, Award } from 'lucide-react';
import { usePredictions } from '../hooks/useRacePulseApi';
import LoadingState from '../components/common/LoadingState';
import ErrorState from '../components/common/ErrorState';
import GlassCard from '../components/common/GlassCard';

export default function PredictionsPage() {
  const { data: predictions, isLoading, isError, refetch } = usePredictions();

  if (isLoading) return <LoadingState message="Calculating statistical trend projections & estimations..." />;
  if (isError || !predictions) return <ErrorState title="Prediction Engine Offline" message="Could not load prediction insights." onRetry={refetch} />;

  const { modelType, disclaimer, nextRaceName, driverPredictions, driverChampionshipProjections } = predictions;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header Banner */}
      <div className="glass-panel p-6">
        <div className="flex items-center gap-2 text-cyan-400 font-mono text-xs font-bold mb-1">
          <Sparkles size={16} /> STATISTICAL ESTIMATE ENGINE
        </div>
        <h1 className="text-2xl sm:text-3xl font-black text-white font-mono">
          Race Outcome & Points Projections
        </h1>
        <p className="text-sm text-slate-400 mt-1">
          Expected finishing ranges for <span className="text-white font-semibold">{nextRaceName}</span>.
        </p>

        {/* Mandatory Transparency Disclaimer */}
        <div className="mt-4 p-3 bg-cyan-500/10 border border-cyan-500/30 rounded-xl text-xs text-cyan-300 flex items-start gap-2.5">
          <Info size={18} className="shrink-0 mt-0.5" />
          <div>
            <span className="font-bold block">{modelType}</span>
            <p className="text-slate-300 leading-snug">{disclaimer}</p>
          </div>
        </div>
      </div>

      {/* Grid: Driver Expected Finishing Ranges */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {driverPredictions.map((pred) => (
          <GlassCard key={pred.driverCode} className="space-y-3 flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between border-b border-slate-800 pb-2.5 mb-2.5">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-sm" style={{ backgroundColor: pred.teamColor }}></span>
                  <span className="font-bold text-white text-base">{pred.driverName}</span>
                </div>
                <span className="text-xs font-mono font-bold text-cyan-400">{pred.driverCode}</span>
              </div>

              <div className="p-3 bg-slate-900/80 rounded-xl border border-slate-800 text-center mb-3">
                <span className="text-[10px] text-slate-500 font-mono uppercase block">EXPECTED FINISHING RANGE</span>
                <span className="text-xl font-black text-cyan-400 font-mono">
                  P{pred.expectedFinishMin} – P{pred.expectedFinishMax}
                </span>
                <span className="text-[11px] text-slate-400 font-mono block mt-0.5">
                  Median Projected: P{pred.expectedFinishMedian}
                </span>
              </div>

              <div className="space-y-2 text-xs font-mono">
                <div className="flex justify-between py-1 border-b border-slate-800/60">
                  <span className="text-slate-400">Recent Trend</span>
                  <span className="text-emerald-400 font-bold">{pred.recentTrend}</span>
                </div>
                <div className="flex justify-between py-1 border-b border-slate-800/60">
                  <span className="text-slate-400">Quali Conversion %</span>
                  <span className="text-slate-200">{pred.qualifyingConversionRate.toFixed(0)}%</span>
                </div>
                <div className="flex justify-between py-1">
                  <span className="text-slate-400">Model Confidence</span>
                  <span className="text-cyan-400 font-semibold">{pred.confidenceIndex.toFixed(0)}%</span>
                </div>
              </div>
            </div>

            <p className="text-[10px] text-slate-500 font-mono italic pt-2 border-t border-slate-800/50">
              {pred.statisticalNote}
            </p>
          </GlassCard>
        ))}
      </div>

      {/* End of Season Championship Projections */}
      <GlassCard className="space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <Award size={18} className="text-amber-400" /> End-of-Season Championship Points Projection
          </h2>
          <span className="text-xs font-mono text-slate-400">Monte-Carlo Statistical Simulation</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs font-mono">
            <thead>
              <tr className="text-slate-400 border-b border-slate-800 uppercase">
                <th className="pb-2">DRIVER</th>
                <th className="pb-2">CURRENT PTS</th>
                <th className="pb-2">PROJECTED MIN</th>
                <th className="pb-2">PROJECTED MEDIAN</th>
                <th className="pb-2 text-right">PROJECTED MAX</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/40">
              {driverChampionshipProjections.map((proj) => (
                <tr key={proj.name} className="hover:bg-slate-800/30">
                  <td className="py-2.5 font-bold text-slate-100">{proj.name}</td>
                  <td className="py-2.5 text-slate-400">{proj.currentPoints} PTS</td>
                  <td className="py-2.5 text-slate-300">{proj.projectedEndSeasonPointsMin} PTS</td>
                  <td className="py-2.5 text-cyan-400 font-bold">{proj.projectedEndSeasonPointsMedian} PTS</td>
                  <td className="py-2.5 text-right font-bold text-amber-400">{proj.projectedEndSeasonPointsMax} PTS</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </GlassCard>

    </div>
  );
}
