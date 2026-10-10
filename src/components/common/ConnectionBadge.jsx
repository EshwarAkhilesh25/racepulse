import React from 'react';
import { Wifi, WifiOff, RefreshCw } from 'lucide-react';

export default function ConnectionBadge({ status = 'Connected', isFallback = false }) {
  const getBadgeStyle = () => {
    switch (status) {
      case 'Connected':
        return {
          bg: 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400',
          dot: 'bg-emerald-400 animate-pulse',
          icon: <Wifi size={14} />
        };
      case 'Connecting':
      case 'Reconnecting':
        return {
          bg: 'bg-amber-500/10 border-amber-500/30 text-amber-400',
          dot: 'bg-amber-400 animate-ping',
          icon: <RefreshCw size={14} className="animate-spin" />
        };
      case 'Offline':
      default:
        return {
          bg: 'bg-red-500/10 border-red-500/30 text-red-400',
          dot: 'bg-red-400',
          icon: <WifiOff size={14} />
        };
    }
  };

  const style = getBadgeStyle();

  return (
    <div className={`inline-flex items-center gap-2 px-3 py-1 rounded-full border text-xs font-mono tracking-wide ${style.bg}`}>
      <span className={`w-2 h-2 rounded-full ${style.dot}`}></span>
      {style.icon}
      <span>{status.toUpperCase()}</span>
      {isFallback && <span className="text-[10px] text-amber-300 bg-amber-500/20 px-1.5 py-0.5 rounded ml-1">REST FALLBACK</span>}
    </div>
  );
}
