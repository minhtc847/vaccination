import React from 'react'
import { Container } from 'react-bootstrap'

function Home() {
  document.title = "Vaccine Management System";
  return (
    <Container className='pt-4'>
      <h5 className='pb-3' style={{ fontWeight: "bold" }}>WELCOME TO VACCINE MANAGEMENT SYSTEM</h5>
      <Container style={{ backgroundColor: "white", height: "100px" }}>

      </Container>
    </Container>
  )
}

export default Home