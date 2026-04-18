import React from 'react'
import Navbar from './components/Navbar'
import SignUpPage from './page/SignupPage'
import LoginPage from './page/LoginPage'
import HomePage from './page/HomePage'
import ProfilePage from './page/ProfilePage'
import MatchPage from './page/MatchPage'
import { Navigate, Route, Routes } from 'react-router-dom'
import { Toaster } from "react-hot-toast";
import { useAuthStore } from './store/useAuthStore';
import { useEffect } from 'react';
import NotificationSocketBridge from './components/NotificationSocketBridge';

const App = () => {
  const { authUser, checkAuth, isCheckingAuth } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  if (isCheckingAuth && !authUser) {
    return null;
  }

  return (
    <div >
      {authUser && <NotificationSocketBridge />}
      <Navbar />
      <Routes>
        <Route path='/' element={authUser ? <HomePage/> : <Navigate to='/login' replace />} />
        <Route path='/signup' element={!authUser ? <SignUpPage/> : <Navigate to='/' replace />} />
        <Route path='/login' element={!authUser ? <LoginPage/> : <Navigate to='/' replace />} />
        <Route path='/profile' element={authUser ? <ProfilePage/> : <Navigate to='/login' replace />} />
        <Route path="/match" element={authUser ? <MatchPage /> : <Navigate to='/login' replace />} />
      </Routes>
      <Toaster />
    </div>
    
  )
}

export default App