import upload_icon from './upload-icon.png';
import './Upload.css';
import { Link } from "react-router-dom";

function UploadDatasets() {
  return (
    <div className="UploadDatasets">
      <header className="header">
        <img src={upload_icon} className="icon" alt="icon" />
        <p>Upload datasets</p>
        <Link to={'/UploadOntologies'} className="link" alt="link">Upload ontologies</Link>
      </header>
    </div>
  );
}

export default UploadDatasets;