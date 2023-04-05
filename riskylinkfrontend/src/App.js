import './App.css';
import Home from './home/Home';
import UploadDatasets from './uploads/UploadDatasets';
import UploadOntologies from './uploads/UploadOntologies';
import ListLinks from './results/ListLinks';
import ThankYou from './results/ThankYou';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useEffect } from 'react';
import { EndSession } from './SessionIDHandling';

const backendURL = "http://" + window.location.hostname + ":8080/";

function App() {

  useEffect(() => {
    const handleUnload = () => {
      if(window.performance.getEntries()[0].type  !== 'reload'){
        EndSession();
      }
    };
  
    window.addEventListener('unload', handleUnload);
  
    return () => {
      window.removeEventListener('unload', handleUnload);
    };
  }, []);  

  
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/UploadDatasets" element={<UploadDatasets />} />
        <Route path="/UploadOntologies" element={<UploadOntologies />} />
        <Route path="/ListLinks" element={<ListLinks />} />
        <Route path="/ThankYou" element={<ThankYou />} />
      </Routes>
    </Router>
  );
}


export { App, backendURL };
