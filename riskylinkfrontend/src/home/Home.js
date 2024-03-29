import logo from '../logo.png';
import link_icon from '../link-icon.png';
import './Home.css';
import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import { GetSessionID, EnsureSessionID } from "../SessionIDHandling";
import { backendURL } from '../App';


function Home() {
  EnsureSessionID();

  //Set up session
  useEffect(() => {
    fetch(backendURL + 'startSession', {
      method: 'GET',
      headers: {
        'sessionID': GetSessionID(),
      }
    });
  }, []);

  return (
    <div className="Home">
      <header className="header">
        <img src={logo} className="logo" alt="logo" />
        <img src={link_icon} className="link_icon" alt="link_icon" />
        <br></br>
        <p>A tool for highlighting links that could be unethical 
          to publish within datasets</p>
        <Link to={'/UploadDatasets'} className="Home-link" alt="link">Start</Link>
      </header>
    </div>
  );
}

export default Home;
