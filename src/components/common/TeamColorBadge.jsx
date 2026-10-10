import React from 'react';

export default function TeamColorBadge({ color = '#00F0FF', name = 'Team' }) {
  return (
    <div className="inline-flex items-center gap-1.5">
      <span className="w-2.5 h-2.5 rounded-sm shadow-sm" style={{ backgroundColor: color }}></span>
      <span className="text-xs font-semibold text-slate-300 tracking-wide">{name}</span>
    </div>
  );
}
