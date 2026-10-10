import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  Activity, 
  Radio, 
  Users, 
  Shield, 
  Calendar, 
  LineChart, 
  BarChart3, 
  Sparkles, 
  History, 
  Settings, 
  Menu, 
  X 
} from 'lucide-react';
import ConnectionBadge from '../common/ConnectionBadge';

export default function Navbar({ connectionStatus = 'Connected', isFallback = false }) {
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navItems = [
    { label: 'Overview', path: '/', icon: Activity },
    { label: 'Live Race', path: '/live', icon: Radio, highlight: true },
    { label: 'Drivers', path: '/drivers', icon: Users },
    { label: 'Constructors', path: '/constructors', icon: Shield },
    { label: 'Race Calendar', path: '/calendar', icon: Calendar },
    { label: 'Telemetry', path: '/telemetry', icon: LineChart },
    { label: 'Analytics', path: '/analytics', icon: BarChart3 },
    { label: 'Predictions', path: '/predictions', icon: Sparkles },
    { label: 'Race History', path: '/history', icon: History },
    { label: 'Settings', path: '/settings', icon: Settings },
  ];

  return (
    <nav className="sticky top-0 z-50 bg-[#0b0e14]/90 backdrop-blur-md border-b border-slate-800/80">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          
          {/* Brand Identity */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-500 via-indigo-500 to-red-500 p-0.5 shadow-[0_0_15px_rgba(0,240,255,0.4)] group-hover:scale-105 transition-transform">
              <div className="w-full h-full bg-[#0b0e14] rounded-[10px] flex items-center justify-center">
                <Activity size={20} className="text-cyan-400 group-hover:rotate-12 transition-transform" />
              </div>
            </div>
            <div>
              <span className="text-xl font-black tracking-tight text-white font-mono">
                RACE<span className="text-cyan-400">PULSE</span>
              </span>
              <span className="block text-[10px] text-slate-400 tracking-wider uppercase font-semibold -mt-1">
                F1 Intelligence Platform
              </span>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden lg:flex items-center space-x-1">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                    isActive
                      ? 'bg-cyan-500/15 text-cyan-300 border border-cyan-500/30 shadow-[0_0_10px_rgba(0,240,255,0.2)]'
                      : item.highlight
                      ? 'text-red-400 hover:bg-red-500/10 hover:text-red-300'
                      : 'text-slate-300 hover:bg-slate-800/60 hover:text-white'
                  }`}
                >
                  <Icon size={14} className={item.highlight && !isActive ? 'animate-pulse' : ''} />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </div>

          {/* Status Badge & Mobile Trigger */}
          <div className="flex items-center gap-3">
            <div className="hidden sm:block">
              <ConnectionBadge status={connectionStatus} isFallback={isFallback} />
            </div>

            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="lg:hidden p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800/60"
            >
              {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Navigation Drawer */}
      {mobileMenuOpen && (
        <div className="lg:hidden bg-[#0b0e14] border-b border-slate-800 px-4 pt-2 pb-4 space-y-1">
          <div className="mb-3 sm:hidden">
            <ConnectionBadge status={connectionStatus} isFallback={isFallback} />
          </div>
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => setMobileMenuOpen(false)}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium ${
                  isActive
                    ? 'bg-cyan-500/15 text-cyan-300 border border-cyan-500/30'
                    : 'text-slate-300 hover:bg-slate-800/60 hover:text-white'
                }`}
              >
                <Icon size={16} />
                <span>{item.label}</span>
              </Link>
            );
          })}
        </div>
      )}
    </nav>
  );
}
