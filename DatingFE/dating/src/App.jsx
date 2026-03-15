import React from 'react'
import Navbar from './components/Navbar'
import SignUpPage from './page/SignupPage'
import HomePage from './page/HomePage'
import { Route, Routes } from 'react-router-dom'

const App = () => {
  return (
    <div >
      <Navbar />
      <Routes>
        <Route path='/' element={<HomePage/>} />
        <Route path='/signup' element={<SignUpPage/>} />
      </Routes>
    </div>
    
  )
}

export default App