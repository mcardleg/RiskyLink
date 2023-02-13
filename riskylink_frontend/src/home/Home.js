import logo from './logo.png';
import link_icon from './link-icon.png';
import './Home.css';
import { Link } from "react-router-dom";


function Home() {
  return (
    <div className="Home">
      <header className="header">
        <img src={logo} className="logo" alt="logo" />
        <img src={link_icon} className="link_icon" alt="link_icon" />
        <p>
          A tool for highlighting links that could be unethical to publish in integrated datasets
        </p>
        <Link to={'/UploadDatasets'} className="link" alt="link">Upload ontologies</Link>
      </header>
    </div>
  );
}

export default Home;
