import upload_icon from './upload-icon.png';
import './Upload.css';
import { useState } from 'react';
import { Link } from "react-router-dom";
import { RedirectIfNoSessionID } from '../Cookies';

function UploadDatasets() {
  RedirectIfNoSessionID();

  const [selectedFile, setSelectedFile] = useState();
	const [isFilePicked, setIsFilePicked] = useState(false);
  const [buttonClicked, setButtonClicked] = useState(false);


  const changeHandler = (event) => {
		setSelectedFile(event.target.files[0]);
		setIsFilePicked(true);
	};

  const handleSubmission = () => {
		const formData = new FormData();

		formData.append('file', selectedFile);

    fetch('http://localhost:8080/uploadDataset', {
      method: 'POST',
      headers: {
        'sessionID': document.cookie,
      },
      body: formData
    })
		.then((response) => {
			console.log('Success:', response);
		})
		.catch((error) => {
			console.error('Error:', error);
		});

    setButtonClicked(true);
	};
  return (
    <div className="Upload">
      <header className="Upload-header">
        <img src={upload_icon} className="icon" alt="icon" />
        {!buttonClicked &&
          <div>You have uploaded all of your datasets.<br></br>
          Now please upload the ontologies that describe your datasets, one by one.</div>
        }

        <br></br>
        <input type="file" name="file" onChange={changeHandler}/>
        <br></br>

        {isFilePicked && 
          <button onClick={handleSubmission} className="button">
            Upload dataset
          </button>
        }
        <br></br>

        {buttonClicked && 
          <div>If you have uploaded all your datasets, click the link below.<br></br>
          If not, continue uploading your datasets one by one.</div>
        }
        <br></br>

        {buttonClicked &&
        <Link to={'/UploadOntologies'} className="link" alt="link">Go to ontology upload page</Link>
        }
      </header>
    </div>
  );
}

export default UploadDatasets;