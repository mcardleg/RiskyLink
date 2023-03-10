/*
 * $Id$
 *
 * Copyright (C) INRIA, 2015
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

package fr.inrialpes.exmo.align.impl.rel;

import fr.inrialpes.exmo.align.impl.BaseRelation;

import java.io.PrintWriter;

/**
 * The Base relatins for the A2 algebra
 */

public enum A2BaseRelation implements BaseRelation {
    EQUIV ( "=" ),
    DIFF( "<>" );

    /**********************************************************************
     * NO CHANGE IS NECESSARY BEYOND THIS POINT (only change the class name)
     * Unfortunately, this MUST be copied in all class.
     **********************************************************************/

    public final String relation;
    public int index;
    public A2BaseRelation inverse;

    public String getString() { return relation; }
    public int getIndex() { return index; }
    public A2BaseRelation getInverse() { return inverse; }

    public void init( int idx, A2BaseRelation inv ) {
	index = idx;
	inverse = inv;
    }

    A2BaseRelation ( String label ) {
	relation = label;
    }

    public void write( PrintWriter writer ) {
	if ( this == DIFF ) writer.print( "&lt;>" );
	else writer.print( relation );
    }
}
