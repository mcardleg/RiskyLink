import React, { useState, useEffect, useRef } from "react";
import './ListLinks.css';
// import { DiagramModel } from "@ontodia/core";
// import { DiagramView } from "@ontodia/view";
import { DiagramModel, DiagramView } from "ontodia";

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
  const [triples, setTriples] = useState([]);
  const diagramRef = useRef(null);

  useEffect(() => {
    fetch("http://localhost:8080/runQueries")
      .then(response => response.json())
      .then(data => {
        const triples = data.basic_query.map(item => ({
          subject: item.demographic,
          predicate: item.data,
          object: item.equivalentClass
        }));
        setTriples(triples);
        // Initialize the Ontodia diagram model
        const model = new DiagramModel();

        // Add some nodes to the model
        const node1 = model.createNode({ id: "node1", label: "Node 1" });
        const node2 = model.createNode({ id: "node2", label: "Node 2" });
        const node3 = model.createNode({ id: "node3", label: "Node 3" });

        // Add some edges to the model
        model.createLink({ id: "link1", source: node1, target: node2 });
        model.createLink({ id: "link2", source: node2, target: node3 });

        // Initialize the Ontodia diagram view with the model
        const view = new DiagramView({
          model,
          container: diagramRef.current,
        });

        // Render the diagram view
        view.render();
      });
  }, []);

  return (
    <div className="ListLinks">
      <header className="header">
        <TripleTable className="TripleTable" triples={triples} />
      </header>
      <div className="diagram-container" ref={diagramRef}></div>
    </div>
  );
}

export default ListLinks;
