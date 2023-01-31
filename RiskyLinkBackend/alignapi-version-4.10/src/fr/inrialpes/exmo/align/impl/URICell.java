/*
 * $Id$
 *
 * Copyright (C) INRIA, 2007-2008, 2010, 2018
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package fr.inrialpes.exmo.align.impl; 

import java.net.URI;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Relation;

/**
 * Represents an ontology alignment correspondence between two URIs
 *
 * @author Jérôme Euzenat
 * @version $Id$ 
 */

public class URICell extends BasicCell {
    public void accept( AlignmentVisitor visitor) throws AlignmentException {
        visitor.visit( this );
    }

    /**
     * Creation
     *
     * @param id: the identifier of the correspondence (may be null)
     * @param ob1 and
     * @param ob2: the URI of the two objects related by the correspondence
     * @param rel: the relation between the objects
     * @param m: the confidence measure in the correspondence
     * @throws AlignmentException when something goes wrong (e.g., confidence out of bounds)
     **/
    public URICell( String id, URI ob1, URI ob2, Relation rel, double m ) throws AlignmentException {
    	super( id, ob1, ob2, rel, m );
    }

    public URI getObject1AsURI( Alignment al ) throws AlignmentException { 
	return (URI)object1; 
    };
    public URI getObject2AsURI( Alignment al ) throws AlignmentException { 
	return (URI)object2; 
    };

    public Cell inverse() throws AlignmentException {
	return (Cell)new URICell( (String)null, (URI)object2, (URI)object1, relation.inverse(), strength );
	// The same should be done for the measure
    }

}

