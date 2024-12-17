import React, { useEffect } from 'react'
import { Spinner } from 'react-bootstrap'
import './LoadingOverlay.css'

function LoadingOverlay() {
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      event.preventDefault();
    };

    // Attach the event listener to the document
    document.addEventListener('keydown', handleKeyDown);

    // Clean up the event listener when the component is unmounted
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  return (
    <div className='full-screen-loading'><Spinner></Spinner></div>
  )
}

export default LoadingOverlay