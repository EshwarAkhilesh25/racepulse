import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';

import OverviewPage from './pages/OverviewPage';
import LiveRacePage from './pages/LiveRacePage';
import DriversPage from './pages/DriversPage';
import DriverProfilePage from './pages/DriverProfilePage';
import ConstructorsPage from './pages/ConstructorsPage';
import ConstructorProfilePage from './pages/ConstructorProfilePage';
import SchedulePage from './pages/SchedulePage';
import TelemetryPage from './pages/TelemetryPage';
import AnalyticsPage from './pages/AnalyticsPage';
import PredictionsPage from './pages/PredictionsPage';
import RaceHistoryPage from './pages/RaceHistoryPage';
import SettingsPage from './pages/SettingsPage';

import './styles/globals.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 60000,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <div className="min-h-screen flex flex-col bg-[#0b0e14] text-slate-100 font-sans selection:bg-cyan-500 selection:text-black">
          <Navbar />
          <main className="flex-grow">
            <Routes>
              <Route path="/" element={<OverviewPage />} />
              <Route path="/live" element={<LiveRacePage />} />
              <Route path="/drivers" element={<DriversPage />} />
              <Route path="/driver/:driverNumber" element={<DriverProfilePage />} />
              <Route path="/drivers/:driverNumber" element={<DriverProfilePage />} />
              <Route path="/constructors" element={<ConstructorsPage />} />
              <Route path="/constructors/:id" element={<ConstructorProfilePage />} />
              <Route path="/calendar" element={<SchedulePage />} />
              <Route path="/schedule" element={<SchedulePage />} />
              <Route path="/telemetry" element={<TelemetryPage />} />
              <Route path="/analytics" element={<AnalyticsPage />} />
              <Route path="/predictions" element={<PredictionsPage />} />
              <Route path="/history" element={<RaceHistoryPage />} />
              <Route path="/settings" element={<SettingsPage />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
