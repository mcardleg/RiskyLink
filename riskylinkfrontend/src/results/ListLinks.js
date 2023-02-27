import React, { useState, useEffect } from "react";
import link_icon from '../link-icon.png';
import './ListLinks.css';
import { GetSessionID, RedirectIfNoSessionID } from '../SessionIDHandling';

function ListLinks() {
  RedirectIfNoSessionID();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8080/runQueries', {
      method: 'GET',
      headers: {
        'sessionID': GetSessionID(),
      }
    })
    .then(response => response.json())
    .then(data => {
      setData(data);
      setLoading(false);
    })
    .catch(error => console.error(error));
  }, []);

  const handleClick = (key) => {
    // Fetch new data based on the clicked row's key
    // fetch(`http://localhost:8080/runQueries?key=${key}`, {
      fetch(`http://localhost:8080/sessionEnded`, {
      method: 'GET',
      headers: {
        'sessionID': GetSessionID(),
      }
    })
    .then(response => response.json())
    .then(data => {
      setData(data);
    })
    .catch(error => console.error(error));
  };

  if (!data) return null;

  const tableRows = Object.entries(data).map(([key, value]) => (
    <tr key={key} onClick={() => handleClick(key)}>
      <td>{key}</td>
      <td>{value}</td>
    </tr>
  ));

  return (
    <div className="ListLinks">
      <header className="ListLinks-header">
        {loading && 
          <div>Getting RiskyLinks.<br></br>This could take a few minutes.</div>
        }
        <br></br>
        {loading ? (
          <img src={link_icon} className="link_icon" alt="link_icon" />
        ) : (
          <table>
          <thead>
            <tr>
              <th>Demographic</th>
              <th>Sensitive Information</th>
            </tr>
          </thead>
          <tbody>
            {tableRows}
          </tbody>
        </table>          
        )}
      </header>
    </div>
  );
}

export default ListLinks;
