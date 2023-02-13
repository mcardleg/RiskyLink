import upload_icon from './upload-icon.png';
import './UploadOntologies.css';
import { useState } from "react";

function UploadOntologies() {
  const [response, setResponse] = useState("");

  const sendRunQueriesGET = () => {
    fetch("https://catfact.ninja/fact")
      .then(res => res.json())
      .then(data => setResponse(JSON.stringify(data)))
      .catch(error => console.error(error));
  };

  return (
    <div className="UploadOntologies">
      <header className="header">
        <img src={upload_icon} className="icon" alt="icon" />
        <p>Upload ontologies</p>
        <button onClick={sendRunQueriesGET} className="button" alt="button">Get risky links</button>
        <p>{response}</p>
      </header>
    </div>
  );
}

export default UploadOntologies;