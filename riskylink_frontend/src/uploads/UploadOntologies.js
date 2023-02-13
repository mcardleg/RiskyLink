import upload_icon from './upload-icon.png';
import './UploadDatasets.css';

function UploadDatasets() {
    return (
      <div className="UploadDatasets">
        <header className="header">
          <img src={upload_icon} className="icon" alt="icon" />
          <p>Upload datasets</p>
          <a
            className="App-link"
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            Learn React
          </a>
        </header>
      </div>
    );
}

export default UploadDatasets;