import React from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

export default function ErrorState({ title = 'Telemetry Connection Offline', message = 'Unable to reach motorsport intelligence service.', onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center p-8 glass-panel border-red-500/30 text-center my-4">
      <div className="p-3 bg-red-500/10 rounded-full text-red-400 mb-3 border border-red-500/20">
        <AlertTriangle size={28} />
      </div>
      <h3 className="text-lg font-bold text-slate-100 mb-1">{title}</h3>
      <p className="text-sm text-slate-400 mb-4 max-w-md">{message}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-cyan-500/10 hover:bg-cyan-500/20 text-cyan-400 border border-cyan-500/30 transition-all font-medium text-sm"
        >
          <RefreshCw size={16} /> Re-establish Stream
        </button>
      )}
    </div>
  );
}
