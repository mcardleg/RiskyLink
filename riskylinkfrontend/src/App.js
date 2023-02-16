import './App.css';
import Home from './home/Home';
import UploadDatasets from './uploads/UploadDatasets';
import UploadOntologies from './uploads/UploadOntologies';
import ListLinks from './results/ListLinks';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/UploadDatasets" element={<UploadDatasets />} />
        <Route path="/UploadOntologies" element={<UploadOntologies />} />
        <Route path="/ListLinks" element={<ListLinks />} />
      </Routes>
    </Router>
  );
}


export default App;
