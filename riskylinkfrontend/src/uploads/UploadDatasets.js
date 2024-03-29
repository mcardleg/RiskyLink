import upload_icon from './upload-icon.png';
import './Upload.css';
import { useState } from 'react';
import { Link } from "react-router-dom";
import { GetSessionID, RedirectIfNoSessionID } from '../SessionIDHandling';
import { backendURL } from '../App';

function UploadDatasets() {
  RedirectIfNoSessionID()

  //Handle file upload
  const [selectedFile, setSelectedFile] = useState();
	const [isFilePicked, setIsFilePicked] = useState(false);
  const [isButtonClicked, setIsButtonClicked] = useState(false);

  const changeHandler = (event) => {
		setSelectedFile(event.target.files[0]);
		setIsFilePicked(true);
	};

  const handleSubmission = () => {
		const formData = new FormData();
		formData.append('file', selectedFile);

    fetch(backendURL + 'uploadDataset', {
      method: 'POST',
      headers: {
        'sessionID': GetSessionID(),
      },
      body: formData
    })
		.then((response) => {
			console.log('Success:', response);
		})
		.catch((error) => {
			console.error('Error:', error);
		});
    setIsButtonClicked(true);
	};

  return (
    <div className="Upload">
      <header className="Upload-header">
        <img src={upload_icon} className="icon" alt="icon" />
        {!isButtonClicked &&
          <div>Please upload your datasets, one by one.</div>
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

        {isButtonClicked && 
          <div>If you have uploaded all your datasets, click the link below.<br></br>
          If not, continue uploading your datasets one by one.</div>
        }
        <br></br>

        {isButtonClicked &&
        <Link to={'/UploadOntologies'} className="link" alt="link">Go to ontology upload page</Link>
        }
      </header>
    </div>
  );
}

export default UploadDatasets;