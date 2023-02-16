import upload_icon from './upload-icon.png';
import './Upload.css';
import { Link } from "react-router-dom";

function UploadOntologies() {
  return (
    <div className="UploadOntologies">
      <header className="header">
        <img src={upload_icon} className="icon" alt="icon" />
        <p>Upload ontologies</p>
        <Link to={'/ListLinks'} className="link" alt="link">Get RiskyLinks</Link>
      </header>
    </div>
  );
}

export default UploadOntologies;
