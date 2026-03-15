import React from 'react'
import Navbar from './components/Navbar'
import { Route, Routes } from 'react-router-dom'

const App = () => {
  return (
    <div >
      <Navbar />
      <Routes>
        <Route path='/' element={<HomePage/>} />
      </Routes>
    </div>
    
  )
}

export default App