import React, { useState, useEffect } from "react";
import './ListLinks.css';
import { GetCookie, RedirectIfNoSessionID } from '../Cookies';

const TripleTableRow = ({ triple }) => (
  <tr>
    <td>{triple.subject}</td>
    <td>{triple.predicate}</td>
    <td>{triple.object}</td>
  </tr>
);

const TripleTable = ({ triples }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Subject</th>
          <th>Predicate</th>
          <th>Object</th>
        </tr>
      </thead>
      <tbody>
        {triples.map((triple, index) => (
          <TripleTableRow key={index} triple={triple} />
        ))}
      </tbody>
    </table>
  );
};

function ListLinks() {
  RedirectIfNoSessionID();

  const [triples, setTriples] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/runQueries', {
      method: 'GET',
      headers: {
        'sessionID': GetCookie(),
      }
    })
      .then(response => response.json())
      .then(data => {
        const triples = data.basic_query.map(item => ({
          subject: item.types_of_sensitive_info,
          predicate: item.demographic,
          object: item.subject
        }));
        setTriples(triples);
      });
  }, []);

  return (
    <div className="ListLinks">
      <header className="ListLinks-header">
        <TripleTable className="table" triples={triples} />
      </header>
    </div>
  );
}

export default ListLinks;
