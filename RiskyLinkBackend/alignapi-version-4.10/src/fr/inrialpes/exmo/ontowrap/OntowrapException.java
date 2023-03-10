/*
 * $Id$
 *
 * Copyright (C) INRIA Rh?ne-Alpes, 2010
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */

package fr.inrialpes.exmo.ontowrap; 

import java.lang.Exception;

/**
 * Base class for all Ontowrap Exceptions.
 *
 *
 * @author J?r?me Euzenat
 * @version $Id$
 */

public class OntowrapException extends Exception {

    private static final long serialVersionUID = 400;

    public OntowrapException( String message )
    {
	super( message );
    }
    
    public OntowrapException( String message, Exception e )
    {
	super( message, e );
    }
    
}

