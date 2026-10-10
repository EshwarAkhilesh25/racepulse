import React from 'react';
import { 
  Radio, 
  Flag, 
  Clock, 
  AlertTriangle, 
  Gauge, 
  Activity, 
  ArrowUp, 
  ArrowDown, 
  Minus,
  RefreshCw
} from 'lucide-react';
import { useWebSocket } from '../hooks/useWebSocket';
import ConnectionBadge from '../components/common/ConnectionBadge';
import GlassCard from '../components/common/GlassCard';
import TeamColorBadge from '../components/common/TeamColorBadge';
import LoadingState from '../components/common/LoadingState';

export default function LiveRacePage() {
  const { data: sessionData, connectionStatus, isUsingFallback, refetchFallback } = useWebSocket('/topic/live', '/live/session');

  if (!sessionData && connectionStatus === 'Connecting') {
    return <LoadingState message="Connecting to RacePulse high-frequency WebSocket stream..." />;
  }

  const session = sessionData || {
    sessionName: 'Singapore Grand Prix - Live Race Telemetry',
    circuitName: 'Marina Bay Street Circuit',
    currentLap: 42,
    totalLaps: 62,
    sessionStatus: 'LIVE',
    positions: [],
    recentIncidents: [],
    weather: { airTempCelsius: 29.4, trackTempCelsius: 41.8, humidityPercent: 68, windSpeedKmh: 12.5 }
  };

  const positions = session.positions || [];
  const incidents = session.recentIncidents || [];
  const weather = session.weather || {};

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Session Top Bar */}
      <div className="glass-panel p-6 flex flex-col md:flex-row md:items-center justify-between gap-4 border-cyan-500/20">
        <div>
          <div className="flex items-center gap-3 mb-1">
            <span className="flex items-center gap-1.5 px-3 py-0.5 rounded-full bg-red-500/10 text-red-400 border border-red-500/20 text-xs font-mono font-bold animate-pulse">
              <Radio size={14} /> LIVE SESSION
            </span>
            <ConnectionBadge status={connectionStatus} isFallback={isUsingFallback} />
          </div>
          <h1 className="text-2xl sm:text-3xl font-black text-white font-mono tracking-tight">{session.sessionName}</h1>
          <p className="text-sm text-slate-400 font-mono mt-0.5">{session.circuitName} • LAP {session.currentLap} / {session.totalLaps}</p>
        </div>

        <div className="flex items-center gap-4 bg-slate-900/80 p-3 rounded-xl border border-slate-800 text-xs font-mono">
          <div>
            <span className="text-slate-500 block text-[10px]">TRACK TEMP</span>
            <span className="text-amber-400 font-bold text-base">{weather.trackTempCelsius || 41.8}°C</span>
          </div>
          <div className="h-8 w-px bg-slate-800"></div>
          <div>
            <span className="text-slate-500 block text-[10px]">AIR TEMP</span>
            <span className="text-slate-200 font-bold text-base">{weather.airTempCelsius || 29.4}°C</span>
          </div>
          <div className="h-8 w-px bg-slate-800"></div>
          <div>
            <span className="text-slate-500 block text-[10px]">RAINFALL</span>
            <span className="text-cyan-400 font-bold text-base">0%</span>
          </div>
          {isUsingFallback && (
            <button
              onClick={refetchFallback}
              className="p-2 bg-slate-800 hover:bg-slate-700 text-cyan-400 rounded-lg transition-colors ml-2"
              title="Manual Refresh"
            >
              <RefreshCw size={14} />
            </button>
          )}
        </div>
      </div>

      {/* Main Leaderboard Table */}
      <GlassCard className="space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h2 className="text-lg font-bold text-white flex items-center gap-2">
            <Gauge size={18} className="text-cyan-400" /> Driver Timing & Sector Telemetry Matrix
          </h2>
          <span className="text-xs font-mono text-slate-400">High-Frequency Stream</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs font-mono">
            <thead>
              <tr className="text-[10px] text-slate-400 border-b border-slate-800 uppercase tracking-wider">
                <th className="pb-3">POS</th>
                <th className="pb-3">DELTA</th>
                <th className="pb-3">DRIVER</th>
                <th className="pb-3">GAP TO LEADER</th>
                <th className="pb-3">INTERVAL</th>
                <th className="pb-3">LAST LAP</th>
                <th className="pb-3">BEST LAP</th>
                <th className="pb-3">TYRE</th>
                <th className="pb-3">S1</th>
                <th className="pb-3">S2</th>
                <th className="pb-3">S3</th>
                <th className="pb-3 text-right">STATUS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/40">
              {positions.map((driver) => {
                const delta = driver.positionDelta || 0;
                return (
                  <tr key={driver.driverNumber} className="hover:bg-slate-800/30 transition-colors">
                    <td className="py-3 font-bold text-cyan-400 text-sm">P{driver.position}</td>
                    <td className="py-3">
                      {delta > 0 ? (
                        <span className="inline-flex items-center text-emerald-400 font-bold"><ArrowUp size={12} />{delta}</span>
                      ) : delta < 0 ? (
                        <span className="inline-flex items-center text-red-400 font-bold"><ArrowDown size={12} />{Math.abs(delta)}</span>
                      ) : (
                        <span className="inline-flex items-center text-slate-600"><Minus size={12} /></span>
                      )}
                    </td>
                    <td className="py-3 font-bold text-slate-100">
                      <div className="flex items-center gap-2">
                        <TeamColorBadge color={driver.teamColor} name={driver.driverCode} />
                        <span className="text-slate-400 text-[11px] font-normal">{driver.driverName}</span>
                      </div>
                    </td>
                    <td className="py-3 font-semibold text-slate-200">{driver.gapToLeader}</td>
                    <td className="py-3 text-slate-400">{driver.intervalToAhead}</td>
                    <td className="py-3 text-cyan-300 font-semibold">{driver.lastLapTime}</td>
                    <td className="py-3 text-purple-400">{driver.bestLapTime}</td>
                    <td className="py-3">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        driver.tyreCompound === 'SOFT' ? 'bg-red-500/20 text-red-400 border border-red-500/30' :
                        driver.tyreCompound === 'MEDIUM' ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30' :
                        'bg-slate-200/20 text-slate-200 border border-slate-400/30'
                      }`}>
                        {driver.tyreCompound} ({driver.currentStintLaps}L)
                      </span>
                    </td>
                    <td className="py-3 text-slate-300">{driver.sector1}</td>
                    <td className="py-3 text-slate-300">{driver.sector2}</td>
                    <td className="py-3 text-slate-300">{driver.sector3}</td>
                    <td className="py-3 text-right">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        driver.isPitting ? 'bg-amber-500/20 text-amber-300 animate-pulse' : 'bg-emerald-500/10 text-emerald-400'
                      }`}>
                        {driver.isPitting ? 'IN PIT' : driver.status}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </GlassCard>

      {/* Incidents & Strategy Insights Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* Race Control Incidents Log */}
        <GlassCard className="space-y-3">
          <h3 className="text-sm font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-2">
            <AlertTriangle size={16} className="text-amber-400" /> FIA Race Control Incident Log
          </h3>

          <div className="space-y-2.5 max-h-48 overflow-y-auto pr-1">
            {incidents.map((inc, i) => (
              <div key={i} className="p-2.5 bg-slate-900/60 rounded-lg border border-slate-800 text-xs font-mono space-y-1">
                <div className="flex justify-between text-[10px]">
                  <span className="text-amber-400 font-bold">{inc.incidentType}</span>
                  <span className="text-slate-500">{inc.time} • {inc.lap}</span>
                </div>
                <p className="text-slate-300">{inc.description}</p>
              </div>
            ))}
          </div>
        </GlassCard>

        {/* Live Strategy & Pit Window Summary */}
        <GlassCard className="space-y-3">
          <h3 className="text-sm font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-2">
            <Activity size={16} className="text-cyan-400" /> Live Pit Strategy & Degradation
          </h3>

          <div className="space-y-2 text-xs font-mono">
            <div className="p-2.5 bg-slate-900/60 rounded-lg border border-slate-800">
              <span className="text-slate-400 block text-[10px]">OPTIMAL PIT WINDOW</span>
              <span className="text-slate-200 font-bold">Lap 44 - Lap 48 (Hard Compound Stint)</span>
            </div>
            <div className="p-2.5 bg-slate-900/60 rounded-lg border border-slate-800">
              <span className="text-slate-400 block text-[10px]">UNDERCUT RISK THRESHOLD</span>
              <span className="text-cyan-400 font-bold">1.842s Gap required to protect P1</span>
            </div>
          </div>
        </GlassCard>
      </div>

    </div>
  );
}
