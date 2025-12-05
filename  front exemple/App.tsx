import React from 'react';
import { HashRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Home } from './pages/Home';
import { MyFeed } from './pages/MyFeed';
import { TraderDetail } from './pages/TraderDetail';
import { SignalDetail } from './pages/SignalDetail';
import { Profile } from './pages/Profile';

const App: React.FC = () => {
  return (
    <HashRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="my-feed" element={<MyFeed />} />
          <Route path="traders/:id" element={<TraderDetail />} />
          <Route path="signal/:id" element={<SignalDetail />} />
          <Route path="profile" element={<Profile />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </HashRouter>
  );
};

export default App;