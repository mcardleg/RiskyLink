import './App.css';
import Home from './home/Home';
import UploadDatasets from './uploads/UploadDatasets';
import UploadOntologies from './uploads/UploadOntologies';
import ListLinks from './results/ListLinks';
import GenerateOntodia from './results/OntodiaDisplay';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useEffect } from 'react';
import { GetCookie, DeleteSessionID } from './Cookies';


function App() {
  // Close session when user leaves app

  useEffect(() => {
    const handleUnload = () => {
      if(window.performance.getEntries()[0].type  !== 'reload'){
        fetch('http://localhost:8080/sessionEnded', {
          method: 'GET',
          headers: {
            'sessionID': document.cookie,
            // 'sessionID': GetCookie(),
          },
        });
        DeleteSessionID();
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
        <Route path="/Ontodia" element={<GenerateOntodia />} />
      </Routes>
    </Router>
  );
}


export default App;
