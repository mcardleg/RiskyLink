import logo from './logo.png';
import link from './link.png';
import './App.css';
import { Link } from "react-router-dom";

function App() {
  // const navigate = useNavigate();

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <img src={link} className="App-link" alt="link" />
        <p>
          A tool for highlighting links that could be unethical to publish in integrated datasets
        </p>
        <Link to={'/UploadDatasets'} className="App-uploaddatasets">Upload Datasets</Link>

      </header>
    </div>
  );
}

export default App;
