import React from 'react';

export default function GlassCard({ children, className = '', glow = false }) {
  return (
    <div className={`glass-panel p-5 ${glow ? 'border-cyan-500/30 shadow-[0_0_20px_rgba(0,240,255,0.15)]' : ''} ${className}`}>
      {children}
    </div>
  );
}
