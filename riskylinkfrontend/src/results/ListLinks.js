import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import link_icon from '../link-icon.png';
import './ListLinks.css';
import { GetSessionID, RedirectIfNoSessionID } from '../SessionIDHandling';
import { backendURL } from "../App";

function ListLinks() {
  RedirectIfNoSessionID();

  const [classes, setClasses] = useState([]);
  const [triples, setTriples] = useState([]);
  const [loading, setLoading] = useState(true);
  const [triplesLoading, setTriplesLoading] = useState(false);
  const [tickedRows, setTickedRows] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetch(backendURL + 'runQueries', {
      method: 'GET',
      headers: {
        'sessionID': GetSessionID(),
      }
    })
    .then(response => response.json())
    .then(classes => {
      setClasses(classes);
      setLoading(false);
    })
    .catch(error => console.error(error));
  }, []);

  const handleClassesTableClick = (key, value) => {
    setTriplesLoading(true);
    fetch(backendURL + 'getLinks', {
      method: 'GET',
      headers: {
        'sessionID': GetSessionID(),
        'demographic': key,
        'sensitiveInfo': value
      }
    })
    .then(response => response.json())
    .then(triples => {
      setTriples(triples);
      setTriplesLoading(false);
    })
    .catch(error => console.error(error));
  };

  const handleCheckboxClick = (index) => {
    const updatedRows = [...tickedRows];
    updatedRows[index] = !updatedRows[index];
    setTickedRows(updatedRows);
  };

  const handleDoneButtonClick = () => {
    const tickedRowsData = classes.filter((_, index) => tickedRows[index]);

    fetch(backendURL + 'saveTickedRows', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'sessionID': GetSessionID(),
      },
      body: JSON.stringify(tickedRowsData)
    })
    .then(navigate('/ThankYou'))
    .catch(error => console.error(error));
  };

  if (!classes) return null;
  if (!triples) return null;


  const classesRows = classes.map(({ demographic, sensitiveInfo }, index) => (
    <tr key={index} onClick={() => handleClassesTableClick(demographic, sensitiveInfo)}>
      <td>{demographic}</td>
      <td>{sensitiveInfo}</td>
      <td>
      <input type="checkbox" onClick={(event) => event.stopPropagation()} onChange={() => handleCheckboxClick(index)} />      </td>
    </tr>
  ));  

  const tripleRows = triples.map(({ subject, predicate, object }, index) => (
    <tr key={index}>
      <td>{subject}</td>
      <td>{predicate}</td>
      <td>{object}</td>
    </tr>
  ));

  return (
    <div className="ListLinks">
      <header className="ListLinks-header">
        {loading && 
          <div>Getting RiskyLinks!<br></br><br></br>
          This could take a few minutes.</div>
        }
        <br></br>
        {loading &&
          <img src={link_icon} className="link_icon" alt="link_icon" />
        }
        <br></br>
        {loading ? (
            <div className="Background-info">"Ethical objections to linking [health data with ethnic minorities] 
              include concerns about informed consent and the possibility of the findings 
              being misused against the interests of ethnic minority groups." <br></br><br></br>
              ~ Boyd, Kenneth M. 2007. “Ethnicity and the Ethics of Data Linkage.” 
              BMC Public Health 7 (1). doi:10.1186/1471-2458-7-318.<br></br><br></br>
              RiskyLink expands on this concept and flags links between demographics 
              and sensitive information.</div>
        ) : (
        <table className="Classes-table">
          <thead>
            <tr>
              <th>Demographic</th>
              <th>Sensitive Information</th>
              <th>Safe</th>
            </tr>
          </thead>
          <tbody>
            {classesRows}
          </tbody>
        </table> 
        )}
        {!loading &&
          <div className="Instruction">
            The classes of RiskyLinks within your datasets have been identified. 
            Click on any row and review the coresponding links in your datasets. 
            If you believe it is safe to publish the corresponding links, tick the box.
          </div>
        }
        {!loading &&
          <button onClick={handleDoneButtonClick} className="Done-button">Done</button>
        }
        {!loading && triplesLoading &&
          <img src={link_icon} className="link_icon2" alt="link_icon" />
        }
        {!loading && !triplesLoading &&
          <table className="Triples-table">
          <thead>
            <tr>
              <th>Subject</th>
              <th>Predicate</th>
              <th>Object</th>
            </tr>
          </thead>
          <tbody>
            {tripleRows}
          </tbody>
        </table>   
        }
      </header>
      </div>
  );
}

export default ListLinks;
