import React from 'react';

export default function LoadingState({ message = 'Telemetry stream loading...' }) {
  return (
    <div className="flex flex-col items-center justify-center p-12 min-h-[300px] glass-panel text-center">
      <div className="relative w-16 h-16 mb-4">
        <div className="absolute inset-0 rounded-full border-4 border-cyan-500/20 animate-ping"></div>
        <div className="absolute inset-0 rounded-full border-4 border-t-cyan-400 border-r-transparent border-b-cyan-500 border-l-transparent animate-spin"></div>
      </div>
      <p className="text-cyan-400 font-mono tracking-wider text-sm animate-pulse">{message}</p>
    </div>
  );
}
