import React, { useState } from 'react';
import { LineChart as LineChartIcon, Activity, Gauge, Zap, Compass } from 'lucide-react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import GlassCard from '../components/common/GlassCard';

export default function TelemetryPage() {
  const [selectedDriver1, setSelectedDriver1] = useState('NOR');
  const [selectedDriver2, setSelectedDriver2] = useState('VER');

  // Simulated telemetry distance trace data across lap
  const telemetryData = Array.from({ length: 30 }, (_, i) => {
    const dist = i * 150; // meters
    const speed1 = 180 + Math.sin(i * 0.4) * 140 + (i % 5 === 0 ? -60 : 20);
    const speed2 = 182 + Math.sin(i * 0.4 + 0.1) * 138 + (i % 5 === 0 ? -55 : 22);
    const throttle1 = Math.max(0, Math.min(100, 50 + Math.sin(i * 0.5) * 60));
    const throttle2 = Math.max(0, Math.min(100, 48 + Math.sin(i * 0.5 + 0.1) * 62));
    return {
      distance: `${dist}m`,
      [selectedDriver1]: Math.round(speed1),
      [selectedDriver2]: Math.round(speed2),
      [`${selectedDriver1}_Throttle`]: Math.round(throttle1),
      [`${selectedDriver2}_Throttle`]: Math.round(throttle2),
    };
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header & Driver Telemetry Selector */}
      <div className="glass-panel p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
            <LineChartIcon className="text-cyan-400" /> Driver Telemetry Trace Analyzer
          </h1>
          <p className="text-sm text-slate-400 mt-1">High-frequency speed, throttle, and braking trace comparison over lap distance.</p>
        </div>

        <div className="flex items-center gap-3 font-mono text-xs">
          <div>
            <label className="text-[10px] text-slate-500 block">DRIVER 1</label>
            <select
              value={selectedDriver1}
              onChange={(e) => setSelectedDriver1(e.target.value)}
              className="bg-slate-900 border border-cyan-500/40 rounded-lg px-3 py-1.5 text-cyan-400 font-bold focus:outline-none"
            >
              <option value="NOR">Lando Norris (#4)</option>
              <option value="LEC">Charles Leclerc (#16)</option>
              <option value="PIA">Oscar Piastri (#81)</option>
            </select>
          </div>
          <span className="text-slate-500 font-bold">VS</span>
          <div>
            <label className="text-[10px] text-slate-500 block">DRIVER 2</label>
            <select
              value={selectedDriver2}
              onChange={(e) => setSelectedDriver2(e.target.value)}
              className="bg-slate-900 border border-purple-500/40 rounded-lg px-3 py-1.5 text-purple-400 font-bold focus:outline-none"
            >
              <option value="VER">Max Verstappen (#1)</option>
              <option value="HAM">Lewis Hamilton (#44)</option>
              <option value="RUS">George Russell (#63)</option>
            </select>
          </div>
        </div>
      </div>

      {/* Speed Trace Comparison Chart */}
      <GlassCard className="space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <Gauge size={18} className="text-cyan-400" /> Speed Trace Comparison (km/h)
          </h2>
          <span className="text-xs font-mono text-slate-400">Singapore GP Sector 1-3</span>
        </div>

        <div className="h-72 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={telemetryData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
              <XAxis dataKey="distance" stroke="#64748b" />
              <YAxis domain={[100, 350]} stroke="#64748b" />
              <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
              <Line type="monotone" dataKey={selectedDriver1} stroke="#00F0FF" strokeWidth={2.5} dot={false} />
              <Line type="monotone" dataKey={selectedDriver2} stroke="#7A28FF" strokeWidth={2.5} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </GlassCard>

      {/* Throttle Application Chart */}
      <GlassCard className="space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <Zap size={18} className="text-amber-400" /> Throttle Application (%)
          </h2>
          <span className="text-xs font-mono text-slate-400">Throttle % vs Track Distance</span>
        </div>

        <div className="h-56 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={telemetryData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
              <XAxis dataKey="distance" stroke="#64748b" />
              <YAxis domain={[0, 100]} stroke="#64748b" />
              <Tooltip contentStyle={{ backgroundColor: '#0b0e14', borderColor: '#334155', color: '#fff' }} />
              <Line type="monotone" dataKey={`${selectedDriver1}_Throttle`} stroke="#00F0FF" strokeWidth={2} dot={false} />
              <Line type="monotone" dataKey={`${selectedDriver2}_Throttle`} stroke="#7A28FF" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </GlassCard>

    </div>
  );
}
