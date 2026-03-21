import React from 'react'
import Navbar from './components/Navbar'
import SignUpPage from './page/SignupPage'
import LoginPage from './page/LoginPage'
import HomePage from './page/HomePage'
import ProfilePage from './page/ProfilePage'
import { Route, Routes } from 'react-router-dom'
import { Toaster } from "react-hot-toast";
import { useAuthStore } from './store/useAuthStore';
import { useEffect } from 'react';

const App = () => {
  const { authUser, checkAuth } = useAuthStore();

  return (
    <div >
      <Navbar />
      <Routes>
        <Route path='/' element={<HomePage/>} />
        <Route path='/signup' element={<SignUpPage/>} />
        <Route path='/login' element={<LoginPage/>} />
        <Route path='/profile' element={<ProfilePage/>} />
      </Routes>
      <Toaster />
    </div>
    
  )
}

export default App