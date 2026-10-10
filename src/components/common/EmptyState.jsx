import React from 'react';
import { Flag } from 'lucide-react';

export default function EmptyState({ title = 'No Active Telemetry Data', message = 'There are no active data records matching your selected query.' }) {
  return (
    <div className="flex flex-col items-center justify-center p-10 glass-panel text-center">
      <div className="p-3 bg-slate-800/50 rounded-full text-slate-400 mb-3 border border-slate-700/50">
        <Flag size={24} />
      </div>
      <h3 className="text-base font-semibold text-slate-200 mb-1">{title}</h3>
      <p className="text-xs text-slate-400 max-w-sm">{message}</p>
    </div>
  );
}
