import React from 'react';
import Ontodia from 'ontodia';

const OntodiaDisplay = ({ workspace}) => {
    if (!workspace) { return; }

    const model = workspace.getModel();
    model.graph.on('action:iriClick', (iri) => {
        window.open(iri);
        console.log(iri);
    });

    model.importLayout({
        validateLinks: true,
        dataProvider: new Ontodia.SparqlDataProvider({
            endpointUrl: '/sparql-endpoint',
            imagePropertyUris: [
                'http://collection.britishmuseum.org/id/ontology/PX_has_main_representation',
                'http://xmlns.com/foaf/0.1/img',
            ],
        }, Ontodia.OWLStatsSettings),
    });
}

function GenerateOntodia() {
    return (
        <div className='OntodiaDisplay'>
            <header className='header'>
                <OntodiaDisplay className="OntodiaDisplay" workspace={Ontodia.Workspace}/>
                {/* <ontodia></ontodia> */}
            </header>
        </div>
    )
}

export default GenerateOntodia;