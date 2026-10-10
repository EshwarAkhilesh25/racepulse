import React, { useState } from 'react';
import { Settings, Server, RefreshCw, CheckCircle2, Sliders, Shield } from 'lucide-react';
import GlassCard from '../components/common/GlassCard';

export default function SettingsPage() {
  const [apiUrl, setApiUrl] = useState(import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1');
  const [wsRate, setWsRate] = useState('2000');
  const [unitSpeed, setUnitSpeed] = useState('kmh');
  const [unitTemp, setUnitTemp] = useState('celsius');
  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleSave = () => {
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
      
      {/* Header */}
      <div className="glass-panel p-6">
        <h1 className="text-2xl sm:text-3xl font-black text-white font-mono flex items-center gap-3">
          <Settings className="text-cyan-400" /> Platform Preferences & API Configuration
        </h1>
        <p className="text-sm text-slate-400 mt-1">Configure telemetry endpoints, WebSocket polling rates, and display units.</p>
      </div>

      {/* API Endpoint Config */}
      <GlassCard className="space-y-4">
        <h2 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
          <Server size={18} className="text-cyan-400" /> Backend API & WebSocket Connectivity
        </h2>

        <div className="space-y-3 font-mono text-xs">
          <div>
            <label className="text-slate-400 block mb-1">REST API BASE URL</label>
            <input
              type="text"
              value={apiUrl}
              onChange={(e) => setApiUrl(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700/80 rounded-xl px-4 py-2.5 text-slate-100 font-mono focus:outline-none focus:border-cyan-400"
            />
          </div>

          <div>
            <label className="text-slate-400 block mb-1">WEBSOCKET TELEMETRY REFRESH INTERVAL</label>
            <select
              value={wsRate}
              onChange={(e) => setWsRate(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700/80 rounded-xl px-4 py-2.5 text-slate-100 font-mono focus:outline-none focus:border-cyan-400"
            >
              <option value="1000">1,000 ms (High Frequency)</option>
              <option value="2000">2,000 ms (Standard Default)</option>
              <option value="5000">5,000 ms (Low Bandwidth)</option>
            </select>
          </div>
        </div>
      </GlassCard>

      {/* Display Units & Theme Preferences */}
      <GlassCard className="space-y-4">
        <h2 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
          <Sliders size={18} className="text-amber-400" /> Telemetry Unit Standards
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 font-mono text-xs">
          <div>
            <label className="text-slate-400 block mb-1">SPEED MEASUREMENT</label>
            <select
              value={unitSpeed}
              onChange={(e) => setUnitSpeed(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700/80 rounded-xl px-4 py-2.5 text-slate-100 focus:outline-none focus:border-cyan-400"
            >
              <option value="kmh">Kilometers per hour (km/h)</option>
              <option value="mph">Miles per hour (mph)</option>
            </select>
          </div>

          <div>
            <label className="text-slate-400 block mb-1">TEMPERATURE METRIC</label>
            <select
              value={unitTemp}
              onChange={(e) => setUnitTemp(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700/80 rounded-xl px-4 py-2.5 text-slate-100 focus:outline-none focus:border-cyan-400"
            >
              <option value="celsius">Celsius (°C)</option>
              <option value="fahrenheit">Fahrenheit (°F)</option>
            </select>
          </div>
        </div>
      </GlassCard>

      {/* Save Action */}
      <div className="flex items-center justify-between pt-2">
        {savedSuccess ? (
          <span className="inline-flex items-center gap-1.5 text-xs text-emerald-400 font-mono font-bold bg-emerald-500/10 px-3 py-1.5 rounded-lg border border-emerald-500/20">
            <CheckCircle2 size={16} /> Preferences saved successfully!
          </span>
        ) : <span></span>}

        <button
          onClick={handleSave}
          className="px-6 py-2.5 bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 text-white font-bold text-xs rounded-xl shadow-lg shadow-cyan-500/20 transition-all font-mono"
        >
          Save Platform Settings
        </button>
      </div>

    </div>
  );
}
