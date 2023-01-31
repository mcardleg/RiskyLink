/*
 * $Id$
 *
 * Copyright (C) INRIA, 2014-2018
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

/*
 * This implementation works. However, it may be inneficient in terms of time
 * or space taken in the database (too many tables and too many indirections).
 * It could be replaced by another.
 *
 * Caveats:
 * - Another problem is the status of this class. It may be better to make it a
 *   subclass of SQLCache...
 */

package fr.inrialpes.exmo.align.service;

import java.util.List;
import java.util.Vector;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import fr.inrialpes.exmo.align.parser.SyntaxElement.Constructor;

import fr.inrialpes.exmo.align.impl.edoal.Id;
import fr.inrialpes.exmo.align.impl.edoal.PathExpression;
import fr.inrialpes.exmo.align.impl.edoal.Expression;
import fr.inrialpes.exmo.align.impl.edoal.ClassExpression;
import fr.inrialpes.exmo.align.impl.edoal.ClassId;
import fr.inrialpes.exmo.align.impl.edoal.ClassConstruction;
import fr.inrialpes.exmo.align.impl.edoal.ClassRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassTypeRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassValueRestriction;
import fr.inrialpes.exmo.align.impl.edoal.ClassOccurenceRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyExpression;
import fr.inrialpes.exmo.align.impl.edoal.PropertyId;
import fr.inrialpes.exmo.align.impl.edoal.PropertyConstruction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyTypeRestriction;
import fr.inrialpes.exmo.align.impl.edoal.PropertyValueRestriction;
import fr.inrialpes.exmo.align.impl.edoal.RelationId;
import fr.inrialpes.exmo.align.impl.edoal.RelationExpression;
import fr.inrialpes.exmo.align.impl.edoal.RelationConstruction;
import fr.inrialpes.exmo.align.impl.edoal.RelationRestriction;
import fr.inrialpes.exmo.align.impl.edoal.RelationDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.RelationCoDomainRestriction;
import fr.inrialpes.exmo.align.impl.edoal.InstanceId;
import fr.inrialpes.exmo.align.impl.edoal.InstanceExpression;

import fr.inrialpes.exmo.align.impl.edoal.Transformation;
import fr.inrialpes.exmo.align.impl.edoal.ValueExpression;
import fr.inrialpes.exmo.align.impl.edoal.Value;
import fr.inrialpes.exmo.align.impl.edoal.Apply;
import fr.inrialpes.exmo.align.impl.edoal.Aggregate;
import fr.inrialpes.exmo.align.impl.edoal.Datatype;
import fr.inrialpes.exmo.align.impl.edoal.EDOALCell;
import fr.inrialpes.exmo.align.impl.edoal.EDOALVisitor;
import fr.inrialpes.exmo.align.impl.edoal.Linkkey;
import fr.inrialpes.exmo.align.impl.edoal.LinkkeyBinding;
import fr.inrialpes.exmo.align.impl.edoal.LinkkeyEquals;
import fr.inrialpes.exmo.align.impl.edoal.LinkkeyIntersects;
import fr.inrialpes.exmo.align.impl.edoal.Comparator;

/**
 * Stores an EDOAL expression in SQL
 * and extract EDOAL expressions from the database
 *
 * EDOALCache is not an extension of SQLCache
 * It is a subcomponent of SQLCache
 * It could be made otherwise later.
 *
 * @author Jérôme Euzenat
 * @version $Id$
 * 
 */

public class EDOALSQLCache {
    final static Logger logger = LoggerFactory.getLogger(EDOALSQLCache.class);

    private boolean isPattern = false;

    DBService service = null;

    // These identifiers are used for indicating in database tables in which table to look for element
    // (and additionally, what is the constructor)
    // id identifier
    public static final int ID = 0;
    // class constructor
    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;
    // property constructor
    public static final int COMP = 4;
    // relation constructor
    public static final int INV = 5;
    public static final int SYM = 6;
    public static final int TRANS = 7;
    public static final int REFL = 8;
    // class restriction
    public static final int OCC_GEQ = 11;
    public static final int OCC_LEQ = 12;
    public static final int OCC_EQ = 13;
    public static final int ALL = 14;
    public static final int EXIST = 15;
    // property restriction
    public static final int DOM = 21;
    public static final int TYP = 22;
    public static final int VAL = 23;
    // relation restriction
    public static final int COD = 24;
    // relation restriction
    // PATH and VALUE ENTITY
    public static final int LIT = 31;
    public static final int REL = 32;
    public static final int PPT = 33;
    public static final int INST = 34;
    // Binding types
    public static final int EQUAL_KEY = 41;
    public static final int INTER_KEY = 42;
    // Transformation types
    public static final int OO = 51;
    public static final int O_ = 52;
    public static final int _O = 53;

    public static final int VALUE = 1;
    public static final int INSTANCE = 2;
    public static final int PATH = 3;
    public static final int APPLY = 4;
    public static final int RELATION = 5;
    public static final int PROPERTY = 6;
    public static final int CLASS = 7;
    public static final int REST = 8;
    public static final int AGGREGATE = 9;

    public EDOALSQLCache( DBService serv ) {
	service = serv;
    }

    // Massively use prepared queries 

    private PreparedStatement findEDOALJoins;
    private PreparedStatement insertEDOALExpr;

    private PreparedStatement findClassUri;
    private PreparedStatement findClassIdByUri;
    private PreparedStatement insertClassId;

    private PreparedStatement findClassJoins;
    private PreparedStatement insertClassExpr;

    private PreparedStatement findClassConst;
    private PreparedStatement insertClassConst;

    private PreparedStatement findClassRestr;
    private PreparedStatement insertClassRestr;

    private PreparedStatement findClassValueRestr;
    private PreparedStatement insertClassValueRestr;

    private PreparedStatement findPathExpr;
    private PreparedStatement insertPathExpr;

    private PreparedStatement findPropUri;
    private PreparedStatement findPropIdByUri;
    private PreparedStatement insertPropId;

    private PreparedStatement findPropExpr;
    private PreparedStatement insertPropExpr;

    private PreparedStatement findPropConst;
    private PreparedStatement insertPropConst;

    private PreparedStatement findPropValueRest;
    private PreparedStatement insertPropValueRest;

    private PreparedStatement findRelExpr;
    private PreparedStatement insertRelExpr;

    private PreparedStatement findRelUri;
    private PreparedStatement findRelIdByUri;
    private PreparedStatement insertRelId;

    private PreparedStatement findRelConst;
    private PreparedStatement insertRelConst;

    private PreparedStatement findInstUri;
    private PreparedStatement findInstByUri;
    private PreparedStatement insertInst;

    private PreparedStatement findTransf;
    private PreparedStatement insertTransf;

    private PreparedStatement findLinkkey;
    private PreparedStatement insertLinkkey;
    
    private PreparedStatement findBindings;
    private PreparedStatement insertBinding;

    private PreparedStatement findLiteral;
    private PreparedStatement findLiteralByValue;
    private PreparedStatement findTypedLiteralByTypedValue;
    private PreparedStatement insertLiteral;
    private PreparedStatement insertTypedLiteral;

    private PreparedStatement findDatatypeIdByUri;
    private PreparedStatement findDatatypeUri;
    private PreparedStatement insertDatatype;

    private PreparedStatement findValueExpr;
    private PreparedStatement insertValueExpr;

    private PreparedStatement findApplyExpr;
    private PreparedStatement insertApplyExpr;
    private PreparedStatement insertAggrExpr;
    private PreparedStatement findApplyArgs;
    private PreparedStatement insertApplyArgs;

    public void compileQueries() throws SQLException {
	try {
	    findEDOALJoins = createStatement( "SELECT type,joinid FROM edoalexpr WHERE intid=?" );
	    insertEDOALExpr = createInsertStatement( "INSERT INTO edoalexpr (type,joinid) VALUES (?,?)" );

	    findClassUri = createStatement( "SELECT uri FROM classid WHERE intid=?" );
	    findClassIdByUri = createStatement( "SELECT intid FROM classid WHERE uri=?" );
	    insertClassId = createInsertStatement( "INSERT INTO classid (uri) VALUES (?)" );

	    findClassJoins = createStatement( "SELECT type,joinid FROM classexpr WHERE intid=?" );
	    insertClassExpr = createInsertStatement( "INSERT INTO classexpr (type,joinid) VALUES (?,?)" );

	    findClassConst = createStatement( "SELECT id FROM classlist WHERE intid=? ORDER BY id" );
	    insertClassConst = createInsertStatement( "INSERT INTO classlist (intid,id) VALUES (?,?)" );

	    findClassRestr = createStatement( "SELECT path,type,joinid FROM classrest WHERE intid=?" );
	    insertClassRestr = createInsertStatement( "INSERT INTO classrest (path,type,joinid) VALUES (?,?,?)" );

	    findClassValueRestr = createStatement( "SELECT comp,joinid FROM valuerest WHERE type=? AND intid=?" );
	    insertClassValueRestr = createInsertStatement( "INSERT INTO valuerest (type,comp,joinid) VALUES (?,?,?)" );

	    findPathExpr = createStatement( "SELECT type,joinid FROM pathexpr WHERE intid=?" );
	    insertPathExpr = createInsertStatement( "INSERT INTO pathexpr (type,joinid) VALUES (?,?)" );

	    findPropUri = createStatement( "SELECT uri FROM propid WHERE intid=?" );
	    findPropIdByUri = createStatement( "SELECT intid FROM propid WHERE uri=?" );
	    insertPropId = createInsertStatement( "INSERT INTO propid (uri) VALUES (?)" );

	    findPropExpr = createStatement( "SELECT type,joinid FROM propexpr WHERE intid=?" );
	    insertPropExpr = createInsertStatement( "INSERT INTO propexpr (type,joinid) VALUES (?,?)" );

	    findPropConst = createStatement( "SELECT id FROM proplist WHERE intid=? ORDER BY id" );
	    insertPropConst = createInsertStatement( "INSERT INTO proplist (intid,id) VALUES (?,?)" );

	    findPropValueRest = createStatement( "SELECT comp,joinid FROM valuerest WHERE type=? AND intid=?" );
	    insertPropValueRest = createInsertStatement( "INSERT INTO valuerest (type,comp,joinid) VALUES (?,?,?)" );

	    findRelExpr = createStatement( "SELECT type,joinid FROM relexpr WHERE intid=?" );
	    insertRelExpr = createInsertStatement( "INSERT INTO relexpr (type,joinid) VALUES (?,?)" );

	    findRelUri = createStatement( "SELECT uri FROM relid WHERE intid=?" );
	    findRelIdByUri = createStatement( "SELECT intid FROM relid WHERE uri=?" );
	    insertRelId = createInsertStatement( "INSERT INTO relid (uri) VALUES (?)" );

	    findRelConst = createStatement( "SELECT id FROM rellist WHERE intid=? ORDER BY id" );
	    insertRelConst = createInsertStatement( "INSERT INTO rellist (intid,id) VALUES (?,?)" );

	    findInstUri = createStatement( "SELECT uri FROM instexpr WHERE intid=?" );
	    findInstByUri = createStatement( "SELECT intid FROM instexpr WHERE uri=?" );
	    insertInst = createInsertStatement( "INSERT INTO instexpr (uri) VALUES (?)" );

	    findTransf = createStatement( "SELECT type,joinid1,joinid2 FROM transf WHERE cellid=?" );
	    insertTransf = createInsertStatement( "INSERT INTO transf (cellid,type,joinid1,joinid2) VALUES (?,?,?,?)" );

	    findLinkkey = createStatement( "SELECT intid FROM linkkey WHERE cellid=?" );
	    insertLinkkey = createInsertStatement( "INSERT INTO linkkey (cellid) VALUES (?)" );

	    findBindings = createStatement( "SELECT type,joinid1,joinid2 FROM binding WHERE keyid=?" );
	    insertBinding = createInsertStatement( "INSERT INTO binding (keyid,type,joinid1,joinid2) VALUES (?,?,?,?)" );

	    findLiteral = createStatement( "SELECT type,value FROM literal WHERE intid=?" );
	    findLiteralByValue = createStatement( "SELECT intid FROM literal WHERE value=?" );
	    insertLiteral = createInsertStatement( "INSERT INTO literal (value) VALUES (?)" );
	    findTypedLiteralByTypedValue = createStatement( "SELECT intid FROM literal WHERE value=? AND type=?" );
	    insertTypedLiteral = createInsertStatement( "INSERT INTO literal (value,type) VALUES (?,?)" );

	    findDatatypeIdByUri = createStatement( "SELECT intid FROM typeexpr WHERE uri=?" );
	    findDatatypeUri = createStatement( "SELECT uri FROM typeexpr WHERE intid=?" );
	    insertDatatype = createInsertStatement( "INSERT INTO typeexpr (uri) VALUES (?)" );

	    findValueExpr = createStatement( "SELECT type, joinid FROM valueexpr WHERE intid=?" );
	    insertValueExpr = createInsertStatement( "INSERT INTO valueexpr (type,joinid) VALUES (?,?)" );

	    findApplyExpr = createStatement( "SELECT operation FROM apply WHERE intid=?" );
	    insertApplyExpr = createInsertStatement( "INSERT INTO apply (type,operation) VALUES (0,?)" );
	    insertAggrExpr = createInsertStatement( "INSERT INTO apply (type,operation) VALUES (1,?)" );
	    findApplyArgs = createStatement( "SELECT id FROM arglist WHERE intid=?" );
	    insertApplyArgs = createInsertStatement( "INSERT INTO arglist (intid,id) VALUES (?,?)" );

	} catch ( SQLException sex ) {
	    logger.info( "Cannot initialize queries: Unexpected problem" );
	    throw sex;
	}
    }


    // PS: to disapear
    public Statement createStatement() throws SQLException {
	return service.getConnection().createStatement();
    }

    public PreparedStatement createStatement( String query ) throws SQLException {
	return service.getConnection().prepareStatement( query );
    }

    public PreparedStatement createInsertStatement( String updatePattern ) throws SQLException {
	return service.getConnection().prepareStatement( updatePattern, Statement.RETURN_GENERATED_KEYS );
    }

    public long executeUpdateWithId( PreparedStatement st, String msg ) throws SQLException {
	if ( st.executeUpdate() != 0 ) {
	    try ( ResultSet generatedKeys = st.getGeneratedKeys() ) {
		if ( generatedKeys.next() ) {
		    return generatedKeys.getLong(1);
		} else {
		    throw new SQLException("Creating "+msg+" entry failed, no ID obtained.");
		}
	    }
	} else throw new SQLException("Creating "+msg+" enty failed.");
    }

    public void init() throws AlignmentException {
	// Should be done at the begining, but not necessarily in init
	try {
	    // service must be initialised (from SQLCache likely)
	    service.getConnection().setAutoCommit( false );
	    // visit something
	    compileQueries();
	} catch ( SQLException sqlex ) {
	    throw new AlignmentException( "Cannot connect to database", sqlex );
	} finally {
	    try { service.getConnection().setAutoCommit( true ); } catch ( SQLException sqlex ) {}
	}
    }

    // ***TODO***
    // Deal with variables here...
    // if (isPattern) { renderVariables(e); }
    public void renderVariables( Expression expr ) {
	/*
        if (expr.getVariable() != null) {
            writer.print(" " + SyntaxElement.VAR.print(DEF) + "=\"" + expr.getVariable().name());
	    }*/
    }

    // This is only for the expressions which are in correspondences

    // **TODO**
    public void erase( EDOALCell cell ) {
    }

    public Expression extractExpression( long intid ) throws SQLException, AlignmentException {
	logger.trace( "extractExpression for Id = {} ", intid );
	findEDOALJoins.setLong( 1, intid );
	try ( ResultSet rs = findEDOALJoins.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot retrieve EDOAL expression : "+intid );
	    int type = rs.getInt( "type" );
	    if ( type == RELATION ) return extractRelationExpression( rs.getLong( "joinid" ) );
	    else if ( type == PROPERTY ) return extractPropertyExpression( rs.getLong( "joinid" ) );
	    else if ( type == INSTANCE ) return extractInstanceExpression( rs.getLong( "joinid" ) );
	    else return extractClassExpression( rs.getLong( "joinid" ) );
	}
    }

    public long visit( final Expression e ) throws SQLException, AlignmentException {
	long intid;
	int type;
	if ( e instanceof ClassExpression ) {
	    intid = visit( (ClassExpression)e );
	    type = CLASS;
	} else if ( e instanceof PropertyExpression ) {
	    intid = visit( (PropertyExpression)e );
	    type = PROPERTY;
	} else if ( e instanceof RelationExpression ) {
	    intid = visit( (RelationExpression)e );
	    type = RELATION;
	} else if ( e instanceof InstanceExpression ) {
	    intid = visit( (InstanceExpression)e );
	    type = INSTANCE;
	} else throw new AlignmentException( "Invalid expression type in a correspondence : "+e );
	return registerEDOALExpression( type, intid );
    }

    public ClassExpression extractClassExpression( long intid ) throws SQLException, AlignmentException {
	findClassJoins.setLong( 1, intid );
	try ( ResultSet rs = findClassJoins.executeQuery() ) {
	    if ( rs.next() ) {
		int type = rs.getInt( "type" );
		if ( type == ID ) return extractClassId( rs.getLong( "joinid" ) );
		else if ( type == OR || type == AND || type == NOT ) 
		    return extractClassConstruction( type, rs.getLong( "joinid" ) );
		else if ( type == REST )
		    return extractClassRestriction( rs.getLong( "joinid" ) );
		else throw new AlignmentException( "Invalid class expression type : "+type );
	    } else {
		throw new AlignmentException( "Cannot retrieve class expression "+intid );
	    }
	}
    }

    public long visit( final ClassExpression e ) throws SQLException, AlignmentException {
	if ( e instanceof ClassId ) return visit( (ClassId)e );
	else if ( e instanceof ClassConstruction ) return visit( (ClassConstruction)e );
	else if ( e instanceof ClassRestriction ) return visit( (ClassRestriction)e );
	else throw new AlignmentException( "Invalid ClassExpression type: "+e );
    }

    public ClassId extractClassId( long intid ) throws SQLException, AlignmentException {
	findClassUri.setLong( 1, intid );
	try ( ResultSet rs = findClassUri.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot retrieve class id : "+intid );
	    try {
		logger.trace( "Identified ClassId = {}", rs.getString( "uri" ) );
		return new ClassId( new URI( rs.getString( "uri" ) ) );
		
	    } catch ( URISyntaxException urisex ) {
		throw new AlignmentException( "Invalid URI", urisex );
	    }
	}
    }

    public long visit( final ClassId e ) throws SQLException, AlignmentException {
	long idres = registerClassId( e );
	return registerClassExpression( ID, idres );
    }

    public ClassConstruction extractClassConstruction( int op, long intid ) throws SQLException, AlignmentException {
	Constructor constr = null;
	if ( op == AND ) constr = Constructor.AND;
	else if ( op == OR ) constr = Constructor.OR;
	else if ( op == NOT ) constr = Constructor.NOT;
	else throw new AlignmentException( "Invalid operator "+op );
	List<ClassExpression> expressions = new Vector<ClassExpression>();
	// PS: cannot use PreparedStatement findClassConst because of recursion
	try ( PreparedStatement pst = createStatement( "SELECT id FROM classlist WHERE intid=? ORDER BY id" ) ) {
	    pst.setLong( 1, intid );
	    try ( ResultSet rs = pst.executeQuery() ) {
		while ( rs.next() ) {
		    expressions.add( extractClassExpression( rs.getLong( "id" ) ) );
		}
	    }
	}
	return new ClassConstruction( constr, expressions );
    }

    public long visit( final ClassConstruction e ) throws SQLException, AlignmentException {
	// Get the constructor
	final Constructor op = e.getOperator();
	int type;
	if ( op == Constructor.OR ) type = OR;
	else if ( op == Constructor.AND ) type = AND;
	else if ( op == Constructor.NOT ) type = NOT;
	else throw new AlignmentException( "Invalid constructor "+op );
	if ( (op == Constructor.OR) || (op == Constructor.AND) ) {
	    // Create the relexpr
	    long exprres = registerClassExpression( type, 0 );
	    // Iterate on components
	    for ( final ClassExpression ce : e.getComponents() ) {
		long pres = visit( ce );
		insertClassConst.setLong( 1, exprres );
		insertClassConst.setLong( 2, pres );
		insertClassConst.executeUpdate();
	    }
	    // Return the relexpr
	    return exprres;
	} else { // NOT
	    final ClassExpression ce = e.getComponents().iterator().next(); // OK...
	    // Create the component
	    long pres = visit( ce );
	    // Create the relexpr
	    return registerClassExpression( type, pres );
	}
    }
		
    public ClassRestriction extractClassRestriction( long intid ) throws SQLException, AlignmentException {
	findClassRestr.setLong( 1, intid );
	try ( ResultSet rs = findClassRestr.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot retrieve class restriction "+intid );
	    int type = rs.getInt( "type" );
	    PathExpression pe = extractPathExpression( rs.getLong( "path" ) );
	    if ( type >= OCC_GEQ && type <= OCC_EQ ) {
		int val = rs.getInt( "joinid" ); // HERE COULD BE A PROBLEM
		Comparator comp = null;
		if ( type == OCC_GEQ ) comp = Comparator.GREATER;
		else if ( type == OCC_LEQ ) comp = Comparator.LOWER;
		else if ( type == OCC_EQ ) comp = Comparator.EQUAL;
		return new ClassOccurenceRestriction( pe, comp, val );
	    } else if ( type == DOM ) 
		return new ClassDomainRestriction( pe, extractClassExpression( rs.getLong( "joinid" ) ) );
	    else if ( type == TYP ) 
		return new ClassTypeRestriction( pe, new Datatype( extractDatatype( rs.getLong( "joinid" ) ).toString() ) );
	    else if ( type == VAL )
		return extractClassValueRestriction( pe, rs.getLong( "joinid" ) );
	    else throw new AlignmentException( "Incorect class restriction type "+type );
	}
    }

    public long visit( final ClassRestriction e ) throws SQLException, AlignmentException {
	long idres;
	long pathid = visit( e.getRestrictionPath() );
	if ( e instanceof ClassValueRestriction ) idres = visit( pathid, (ClassValueRestriction)e );
	else if ( e instanceof ClassTypeRestriction ) idres = visit( pathid, (ClassTypeRestriction)e );
	else if ( e instanceof ClassDomainRestriction ) idres = visit( pathid, (ClassDomainRestriction)e );
	else if ( e instanceof ClassOccurenceRestriction ) idres = visit( pathid, (ClassOccurenceRestriction)e );
	else throw new AlignmentException( "Invalid ClassExpression type: "+e );
	return registerClassExpression( REST, idres );
    }

    public ClassValueRestriction extractClassValueRestriction( PathExpression pe, long intid ) throws SQLException, AlignmentException {
	findClassValueRestr.setInt( 1, CLASS );
	findClassValueRestr.setLong( 2, intid );
	try ( ResultSet rs = findClassValueRestr.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot retrieve class value restriction "+intid );
	    Comparator comp;
	    try {
		comp = Comparator.getComparator( new URI( rs.getString( "comp" ) ) );
	    } catch (URISyntaxException urisex) {
		throw new AlignmentException( "Invalid comparator URI : "+rs.getString( "comp" ) );
	    }
	    ValueExpression ve = extractValueExpression( rs.getLong( "joinid" ) );
	    return new ClassValueRestriction( pe, comp, ve );
	}
    }

    public long visit( long pathid, final ClassValueRestriction c ) throws SQLException, AlignmentException {
	// Create the restriction
	long val = visit( c.getValue() );
	String uri = c.getComparator().toString();
	// Register it in value rest
	insertClassValueRestr.setInt( 1, CLASS );
	insertClassValueRestr.setString( 2, uri );
	insertClassValueRestr.setLong( 3, val );
	long res = executeUpdateWithId( insertClassValueRestr, "class value restriction" );
	// Register it finally
	return registerClassRestriction( VAL, pathid, res );
    }

    public long visit( long pathid, final ClassTypeRestriction c ) throws SQLException, AlignmentException {
	// Create the restriction
	long typ = visit( c.getType() );
	// Register it
	return registerClassRestriction( TYP, pathid, typ );
    }

    public long visit( long pathid, final ClassDomainRestriction c ) throws SQLException, AlignmentException {
	// Create the restriction
	long dom = visit( c.getDomain() );
	// Register it
	return registerClassRestriction( c.isUniversal()?ALL:EXIST, pathid, dom );
    }

    public long visit( long pathid, final ClassOccurenceRestriction c ) throws SQLException, AlignmentException {
	// Create the restriction
	int val = c.getOccurence();
	// Register it
	int type;
	switch ( c.getComparator() ) {
	case EQUAL: type = OCC_EQ; break;
	case GREATER: type = OCC_GEQ; break;
	case LOWER: type = OCC_LEQ; break;
	default: throw new AlignmentException( "Cannot deal with cardinality comparator "+c.getComparator() );
	}
	return registerClassRestriction( type, pathid, val );
    }

    public long registerClassRestriction( int type, long path, long joinid ) throws SQLException, AlignmentException {
	insertClassRestr.setLong( 1, path );
	insertClassRestr.setInt( 2, type );
	insertClassRestr.setLong( 3, joinid );
	return executeUpdateWithId( insertClassRestr, "class restriction" );
    }

    public PathExpression extractPathExpression( long intid ) throws SQLException, AlignmentException {
	findPathExpr.setLong( 1, intid );
	try ( ResultSet rs = findPathExpr.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot retrieve path : "+intid );
	    if ( rs.getInt( "type" ) == RELATION ) return extractRelationExpression( rs.getLong( "joinid" ) );
	    else return extractPropertyExpression( rs.getLong( "joinid" ) );
	}
    }

    public long visit( PathExpression e ) throws SQLException, AlignmentException {
	long intres;
	int type;
	if ( e instanceof PropertyExpression ) {
	    intres = visit( (PropertyExpression)e );
	    type = PROPERTY;
	} else if ( e instanceof RelationExpression ) {
	    intres = visit( (RelationExpression)e );
	    type = RELATION;
	} else throw new AlignmentException( "Invalid ClassExpression type: "+e );
	return registerPathExpression( type, intres );
    }

    public PropertyExpression extractPropertyExpression( long intid ) throws SQLException, AlignmentException {
	findPropExpr.setLong( 1, intid );
	try ( ResultSet rs = findPropExpr.executeQuery() ) {
	    if ( rs.next() ) {
		int type = rs.getInt( "type" );
		if ( type == ID ) return extractPropertyId( rs.getLong( "joinid" ) );
		else if ( type == OR || type == AND || type == COMP || type == NOT ) 
		    return extractPropertyConstruction( type, rs.getLong( "joinid" ) );
		else if ( type == DOM ) return extractPropertyDomainRestriction( rs.getLong( "joinid" ) );
		else if ( type == TYP ) return extractPropertyTypeRestriction( rs.getLong( "joinid" ) );
		else if ( type == VAL ) return extractPropertyValueRestriction( rs.getLong( "joinid" ) );
		else throw new AlignmentException( "Invalid property expression type : "+type );
	    } else {
		throw new AlignmentException( "Cannot retrieve property expression "+intid );
	    }
	}
    }

    public long visit( PropertyExpression e ) throws SQLException, AlignmentException {
	if ( e instanceof PropertyValueRestriction ) {
	    return visit( (PropertyValueRestriction)e );
	} else if ( e instanceof PropertyTypeRestriction ) {
	    return visit( (PropertyTypeRestriction)e );
	} else if ( e instanceof PropertyDomainRestriction ) {
	    return visit( (PropertyDomainRestriction)e );
	} else if ( e instanceof PropertyId ) {
	    return visit( (PropertyId)e );
	} else if ( e instanceof PropertyConstruction ) {
	    return visit( (PropertyConstruction)e ); // It does the job...
	} else throw new AlignmentException( "Invalid property expression "+e );
    }

    public PropertyId extractPropertyId( long intid ) throws SQLException, AlignmentException {
	findPropUri.setLong( 1, intid );
	try ( ResultSet rs = findPropUri.executeQuery() ) {
	    if ( rs.next() ) {
		try {
		    return new PropertyId( new URI( rs.getString( "uri" ) ) );
		} catch (URISyntaxException uriex) {
		    throw new AlignmentException( "Badly formatted URI "+rs.getString("uri"), uriex );
		}
	    } else {
		throw new AlignmentException( "Cannot retrieve property "+intid );
	    }
	}
    }

    public long visit( PropertyId e ) throws SQLException {
	long idres = registerPropertyId( e );
	return registerPropertyExpression( ID, idres );
    }

    // *Beware that these are well registered in PathExpression first*
    public PropertyConstruction extractPropertyConstruction( int op, long intid ) throws SQLException, AlignmentException {
	Constructor constr = null;
	if ( op == AND ) constr = Constructor.AND;
	else if ( op == OR ) constr = Constructor.OR;
	else if ( op == NOT ) constr = Constructor.NOT;
	else if ( op == COMP ) constr = Constructor.COMP;
	else throw new AlignmentException( "Invalid operator "+op );
	List<PathExpression> expressions = new Vector<PathExpression>();
	findPropConst.setLong( 1, intid );
	try ( ResultSet rs = findPropConst.executeQuery() ) {
	    while ( rs.next() ) {
		expressions.add( extractPathExpression( rs.getLong( "id" ) ) );
	    }
	    return new PropertyConstruction( constr, expressions );
	}
    }

    public long visit( final PropertyConstruction e ) throws SQLException, AlignmentException {
	// Get the constructor
	final Constructor op = e.getOperator();
	int type;
	if ( op == Constructor.OR ) type = OR;
	else if ( op == Constructor.AND ) type = AND;
	else if ( op == Constructor.COMP ) type = COMP;
	else if ( op == Constructor.NOT ) type = NOT;
	else throw new AlignmentException( "Invalid constructor "+op );
	if ((op == Constructor.OR) || (op == Constructor.AND) || (op == Constructor.COMP)) {
	    // Create the relexpr
	    long exprres = registerPropertyExpression( type, 0 );
	    // Iterate on components
	    for ( final PathExpression re : e.getComponents() ) {
		long pres = visit( re );
		insertPropConst.setLong( 1, exprres );
		insertPropConst.setLong( 2, pres );
		insertPropConst.executeUpdate();
	    }
	    // Return the propexpr
	    return exprres;
	} else { // NOT
	    final PathExpression re = e.getComponents().iterator().next(); // OK...
	    // Create the component
	    long pres = visit( re );
	    // Create the relexpr
	    return registerPropertyExpression( type, pres );
	}
    }

    public PropertyValueRestriction extractPropertyValueRestriction( long intid ) throws SQLException, AlignmentException {
	findPropValueRest.setInt( 1, PROPERTY );
	findPropValueRest.setLong( 2, intid );
	try ( ResultSet rs = findPropValueRest.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot retrieve property value restriction "+intid );
	    Comparator comp;
	    try {
		comp = Comparator.getComparator( new URI( rs.getString( "comp" ) ) );
	    } catch (URISyntaxException urisex) {
		throw new AlignmentException( "Invalid comparator URI : "+rs.getString( "comp" ) );
	    }
	    ValueExpression ve = extractValueExpression( rs.getLong( "joinid" ) );
	    return new PropertyValueRestriction( comp, ve );
	}
    }

    public long visit( final PropertyValueRestriction c ) throws SQLException, AlignmentException {
	// Create the restriction
	long val = visit( c.getValue() );
	String uri = c.getComparator().toString();
	long res = 0;
	// Register it in value rest
	insertPropValueRest.setInt( 1, PROPERTY );
	insertPropValueRest.setString( 2, uri );
	insertPropValueRest.setLong( 3, val );
	res = executeUpdateWithId( insertPropValueRest, "property value restriction" );
	// Register it finally
	return registerPropertyExpression( VAL, res );
    }

    public PropertyDomainRestriction extractPropertyDomainRestriction( long intid ) throws SQLException, AlignmentException {
	return new PropertyDomainRestriction( extractClassExpression( intid ) );
    }

    public long visit( final PropertyDomainRestriction c ) throws SQLException, AlignmentException {
	// Create the domain restriction
	long dom = visit( c.getDomain() );
	// Register it
	return registerPropertyExpression( DOM, dom );
    }

    public PropertyTypeRestriction extractPropertyTypeRestriction( long intid ) throws SQLException, AlignmentException {
	return new PropertyTypeRestriction( new Datatype( extractDatatype( intid ).toString() ) );
    }

    public long visit( final PropertyTypeRestriction c ) throws SQLException, AlignmentException {
	// Create the domain restriction
	long typ = visit( c.getType() );
	// Register it
	return registerPropertyExpression( TYP, typ );
    }

    public RelationExpression extractRelationExpression( long intid ) throws SQLException, AlignmentException {
	findRelExpr.setLong( 1, intid );
	try ( ResultSet rs = findRelExpr.executeQuery() ) {
	    if ( !rs.next() ) throw new AlignmentException( "Cannot find relation expression "+intid );
	    int op = rs.getInt( "type" );
	    if ( op == ID ) {
		return extractRelationId( rs.getLong( "joinid" ) );
	    } else if ( op == DOM ) {
		return extractRelationDomainRestriction( rs.getLong( "joinid" ) );
	    } else if ( op == COD ) {
		return extractRelationCoDomainRestriction( rs.getLong( "joinid" ) );
	    } else {
		return extractRelationConstruction( op, rs.getLong( "joinid" ) );
	    }
	}
    }

    public long visit( RelationExpression e ) throws SQLException, AlignmentException {
	if ( e instanceof RelationRestriction ) {
	    return visit( (RelationRestriction)e );
	} else if ( e instanceof RelationId ) {
	    return visit( (RelationId)e );
	} else if ( e instanceof RelationConstruction ) {
	    return visit( (RelationConstruction)e ); // It does the job...
	} else throw new AlignmentException( "Invalid relation expression "+e );
    }
    

    public RelationId extractRelationId( long intid ) throws SQLException, AlignmentException {
	findRelUri.setLong( 1, intid );
	try ( ResultSet rs = findRelUri.executeQuery() ) {
	    if ( rs.next() ) {
		try {
		    return new RelationId( new URI( rs.getString( "uri" ) ) );
		} catch (URISyntaxException uriex) {
		    throw new AlignmentException( "Badly formatted URI "+rs.getString("uri"), uriex );
		}
	    } else {
		throw new AlignmentException( "Cannot retrieve relation "+intid );
	    }
	}
    }

    public long visit( RelationId e ) throws SQLException {
	long idres = registerRelationId( e );
	return registerRelationExpression( ID, idres );
    }

    public long registerClassId( Id expr ) throws SQLException {
	findClassIdByUri.setString( 1, expr.getURI().toString() );
	try ( ResultSet rs = findClassIdByUri.executeQuery() ) {
	    if ( rs.next() ) {
		return rs.getLong("intid");
	    } else {
		insertClassId.setString( 1, expr.getURI().toString() );
		return executeUpdateWithId( insertClassId, "insert new class id" );
	    }
	}
    }

    public long registerRelationId( Id expr ) throws SQLException {
	findRelIdByUri.setString( 1, expr.getURI().toString() );
	try ( ResultSet rs = findRelIdByUri.executeQuery() ) {
	    if ( rs.next() ) {
		return rs.getLong("intid");
	    } else {
		insertRelId.setString( 1, expr.getURI().toString() );
		return executeUpdateWithId( insertRelId, "insert new relation id" );
	    }
	}
    }

    public long registerPropertyId( Id expr ) throws SQLException {
	findPropIdByUri.setString( 1, expr.getURI().toString() );
	try ( ResultSet rs = findPropIdByUri.executeQuery(); ) {
	    if ( rs.next() ) {
		return rs.getLong("intid");
	    } else {
		insertPropId.setString( 1, expr.getURI().toString() );
		return executeUpdateWithId( insertPropId, "insert new property id" );
	    }
	}
    }

    public long registerInstanceId( Id expr ) throws SQLException {
	findInstByUri.setString( 1, expr.getURI().toString() );
	try ( ResultSet rs = findInstByUri.executeQuery() ) {
	    if ( rs.next() ) {
		return rs.getLong("intid");
	    } else {
		insertInst.setString( 1, expr.getURI().toString() );
		return executeUpdateWithId( insertInst, "insert new instance id" );
	    }
	}
    }

    public long registerValueExpression( int type, long join ) throws SQLException {
	insertValueExpr.setInt( 1, type );
	insertValueExpr.setLong( 2, join );
	return executeUpdateWithId( insertValueExpr, "valueexpr" );
    }

    public long registerClassExpression( int type, long join ) throws SQLException {
	insertClassExpr.setInt( 1, type );
	insertClassExpr.setLong( 2, join );
	return executeUpdateWithId( insertClassExpr, "classexpr" );
    }

    public long registerRelationExpression( int type, long join ) throws SQLException {
	insertRelExpr.setInt( 1, type );
	insertRelExpr.setLong( 2, join );
	return executeUpdateWithId( insertRelExpr, "relexpr" );
    }

    public long registerPropertyExpression( int type, long join ) throws SQLException {
	insertPropExpr.setInt( 1, type );
	insertPropExpr.setLong( 2, join );
	return executeUpdateWithId( insertPropExpr, "propexpr" );
    }

    public long registerPathExpression( int type, long join ) throws SQLException {
	insertPathExpr.setInt( 1, type );
	insertPathExpr.setLong( 2, join );
	return executeUpdateWithId( insertPathExpr, "pathexpr" );
    }

    public long registerEDOALExpression( int type, long join ) throws SQLException {
	insertEDOALExpr.setInt( 1, type );
	insertEDOALExpr.setLong( 2, join );
	return executeUpdateWithId( insertEDOALExpr, "edoalexpr" );
    }

    public RelationConstruction extractRelationConstruction( int op, long intid ) throws SQLException, AlignmentException {
	Constructor constr = null;
	if ( op == AND ) constr = Constructor.AND;
	else if ( op == OR ) constr = Constructor.OR;
	else if ( op == NOT ) constr = Constructor.NOT;
	else if ( op == COMP ) constr = Constructor.COMP;
	else if ( op == INV ) constr = Constructor.INVERSE;
	else if ( op == SYM ) constr = Constructor.SYMMETRIC;
	else if ( op == TRANS ) constr = Constructor.TRANSITIVE;
	else if ( op == REFL ) constr = Constructor.REFLEXIVE;
	else throw new AlignmentException( "Invalid operator "+op );
	List<RelationExpression> expressions = new Vector<RelationExpression>();
	// PS: cannot use PreparedStatement findRelCons because of recursion
	try ( PreparedStatement pst = createStatement( "SELECT id FROM rellist WHERE intid=? ORDER BY id" ) ) {
	    pst.setLong( 1, intid );
	    try ( ResultSet rs = pst.executeQuery() ) {
		while ( rs.next() ) {
		    expressions.add( extractRelationExpression( rs.getLong( "id" ) ) );
		}
	    }
	}
	return new RelationConstruction( constr, expressions );
    }

    public long visit( final RelationConstruction e ) throws SQLException, AlignmentException {
	// Get the constructor
	final Constructor op = e.getOperator();
	int type;
	if ( op == Constructor.OR ) type = OR;
	else if ( op == Constructor.AND ) type = AND;
	else if ( op == Constructor.COMP ) type = COMP;
	else if ( op == Constructor.NOT ) type = NOT;
	else if ( op == Constructor.INVERSE ) type = INV;
	else if ( op == Constructor.REFLEXIVE ) type = REFL;
	else if ( op == Constructor.TRANSITIVE ) type = TRANS;
	else if ( op == Constructor.SYMMETRIC ) type = SYM;
	else throw new AlignmentException( "Invalid constructor "+op );
	if ((op == Constructor.OR) || (op == Constructor.AND) || (op == Constructor.COMP)) {
	    // Create the relexpr
	    long exprres = registerRelationExpression( type, 0 );
	    // Iterate on components
	    for ( final PathExpression re : e.getComponents() ) {
		long pres = visit( re );
		insertRelConst.setLong( 1, exprres );
		insertRelConst.setLong( 2, pres );
		insertRelConst.executeUpdate();
	    }
	    // Return the relexpr
	    return exprres;
	} else { // NOT
	    final PathExpression re = e.getComponents().iterator().next(); // OK...
	    // Create the component
	    long pres = visit( re );
	    // Create the relexpr
	    return registerRelationExpression( type, pres );
	}
    }

    public RelationCoDomainRestriction extractRelationCoDomainRestriction( long intid ) throws SQLException, AlignmentException {
	ClassExpression codom = extractClassExpression( intid );
	return new RelationCoDomainRestriction( codom );
    }

    public long visit( final RelationRestriction c ) throws SQLException, AlignmentException {
	if ( c instanceof RelationCoDomainRestriction ) return visit( (RelationCoDomainRestriction)c );
	else if ( c instanceof RelationDomainRestriction ) return visit( (RelationDomainRestriction)c );
	else throw new AlignmentException("Creating relation restriction entry failed.");
    }

    public long visit( final RelationCoDomainRestriction c ) throws SQLException, AlignmentException {
	// Create the codomain restriction
	long codom = visit( c.getCoDomain() );
	// Register it
	return registerRelationExpression( COD, codom );
    }

    public RelationDomainRestriction extractRelationDomainRestriction( long intid ) throws SQLException, AlignmentException {
	ClassExpression dom = extractClassExpression( intid );
	return new RelationDomainRestriction( dom );
    }

    public long visit( final RelationDomainRestriction c ) throws SQLException, AlignmentException {
	// Create the domain restriction
	long dom = visit( c.getDomain() );
	// Register it
	return registerRelationExpression( DOM, dom );
    }

    public ValueExpression extractValueExpression( long intid ) throws SQLException, AlignmentException {
	findValueExpr.setLong( 1, intid );
	try ( ResultSet rs = findValueExpr.executeQuery() ) {
	    if ( rs.next() ) {
		int type = rs.getInt( "type" );
		if ( type == INSTANCE ) return extractInstanceExpression( rs.getLong( "joinid" ) );
		else if ( type == VALUE ) return extractValue( rs.getLong( "joinid" ) );
		else if ( type == PATH ) return extractPathExpression( rs.getLong( "joinid" ) );
		else if ( type == APPLY ) return extractApply( rs.getLong( "joinid" ), 0 );
		else if ( type == AGGREGATE ) return extractApply( rs.getLong( "joinid" ), 1 );
		else throw new AlignmentException( "Unknown ValueExpression type "+type );
	    } else {
		throw new AlignmentException( "Cannot retrieve value expression "+intid );
	    }
	}
    }

    public long visit( ValueExpression e ) throws SQLException, AlignmentException {
	long idres;
	int type;
	if ( e instanceof InstanceExpression ) {
	    idres = visit( (InstanceExpression)e );
	    type = INSTANCE;
	} else if ( e instanceof Value ) {
	    idres = visit( (Value)e );
	    type = VALUE;
	} else if ( e instanceof PathExpression ) {
	    idres = visit( (PathExpression)e );
	    type = PATH;
	} else if ( e instanceof Apply ) {
	    idres = visit( (Apply)e );
	    type = APPLY;
	} else if ( e instanceof Aggregate ) {
	    idres = visit( (Aggregate)e );
	    type = AGGREGATE;
	} else throw new AlignmentException( "Unknown ValueExpression "+e );
	return registerValueExpression( type, idres );
    }

    // (no instance expression table)
    public InstanceExpression extractInstanceExpression( long intid ) throws SQLException, AlignmentException {
	return extractInstanceId( intid );
    }

    // (no instance expression table: it is dealt with in instanceid)
    public long visit( InstanceExpression e ) throws SQLException, AlignmentException {
	if ( e instanceof InstanceId ) return visit( (InstanceId)e );
	else throw new AlignmentException( "Unknown InstanceExpression "+e );
    }

    public InstanceId extractInstanceId( long intid ) throws SQLException, AlignmentException {
	findInstUri.setLong( 1, intid );
	try ( ResultSet rs = findInstUri.executeQuery() ) {
	    if ( rs.next() ) {
		try {
		    return new InstanceId( new URI( rs.getString( "uri" ) ) );
		} catch (URISyntaxException uriex) {
		    throw new AlignmentException( "Badly formatted URI "+rs.getString("uri"), uriex );
		}
	    } else throw new AlignmentException( "Cannot retrieve instance "+intid );
	}
    }

    public long visit( InstanceId e ) throws SQLException {
	return registerInstanceId( e );
    }

    // (play with NULL)
    public Value extractValue( long intid ) throws SQLException, AlignmentException {
	findLiteral.setLong( 1, intid );
	try ( ResultSet rs = findLiteral.executeQuery() ) {
	    if ( rs.next() ) {
		Long typeid = rs.getLong( "type" );
		if ( typeid != 0 ) {
		    return new Value( rs.getString( "value" ), extractDatatype( typeid ) );
		} else {
		    return new Value( rs.getString( "value" ) );
		}
	    } else throw new AlignmentException( "Cannot retrieve value "+intid );
	}
    }

    /**
     * Retrieves Value, if it does not exists adds it
     *
     * @param e the Value
     * @returns the id associated with the value
     */
    public long visit( final Value e ) throws SQLException, AlignmentException {
        if ( e.getType() != null ) {
	    long typid = visitDatatype( e.getType().toString() );
	    findTypedLiteralByTypedValue.setString( 1, e.getValue() );
	    findTypedLiteralByTypedValue.setLong( 2, typid );
	    try ( ResultSet rs = findTypedLiteralByTypedValue.executeQuery() ) {
		if ( rs.next() ) {
		    return rs.getLong("intid");
		} else {
		    insertTypedLiteral.setString( 1, e.getValue() );
		    insertTypedLiteral.setLong( 2, typid );
		    return executeUpdateWithId( insertTypedLiteral, "value" );
		}
	    }
        } else {
	    findLiteralByValue.setString( 1, e.getValue() );
	    try ( ResultSet rs = findLiteralByValue.executeQuery() ) {
		if ( rs.next() ) {
		    return rs.getLong("intid");
		} else {
		    insertLiteral.setString( 1, e.getValue() );
		    return executeUpdateWithId( insertLiteral, "value" );
		}
	    }
	}
    }

    // (play with NULL)
    public ValueExpression extractApply( long intid, int type ) throws SQLException, AlignmentException {
	findApplyExpr.setLong( 1, intid );
	try ( ResultSet rs = findApplyExpr.executeQuery() ) {
	    if ( rs.next() ) {
		try {
		    URI opuri = new URI( rs.getString( "operation" ) );
		    findApplyArgs.setLong( 1, intid );
		    try ( ResultSet rs2 = findApplyArgs.executeQuery() ) {
			List<ValueExpression> args = new Vector<ValueExpression>();
			while ( rs2.next() ) {
			    args.add( extractValueExpression( rs2.getLong( "id" ) ) );
			}
			if ( type == 0 ) {
			    return new Apply( opuri, args );
			} else {
			    return new Aggregate( opuri, args );
			}
		    }
		} catch ( URISyntaxException urisex ) {
		    throw new AlignmentException( "Invalid operation URI", urisex );
		}
	    } else throw new AlignmentException( "Cannot retrieve apply "+intid );
	}
    }

    public long visit( final Apply e ) throws SQLException, AlignmentException {
	// Get the constructor
	final URI op = e.getOperation();
	// Create the relexpr
	insertApplyExpr.setString( 1, op.toString() );
	long exprres = executeUpdateWithId( insertApplyExpr, "apply" );
	// Iterate on arguments
	for ( final ValueExpression ve : e.getArguments() ) {
	    long pres = visit( ve );
	    insertApplyArgs.setLong( 1, exprres );
	    insertApplyArgs.setLong( 2, pres );
	    insertApplyArgs.executeUpdate();
	}
	// Return the expr
	return exprres;
    }

    public long visit( final Aggregate e ) throws SQLException, AlignmentException {
	// Get the constructor
	final URI op = e.getOperation();
	// Create the relexpr
	insertAggrExpr.setString( 1, op.toString() );
	long exprres = executeUpdateWithId( insertApplyExpr, "aggr" );
	// Iterate on arguments
	for ( final ValueExpression ve : e.getArguments() ) {
	    long pres = visit( ve );
	    insertApplyArgs.setLong( 1, exprres );
	    insertApplyArgs.setLong( 2, pres );
	    insertApplyArgs.executeUpdate();
	}
	// Return the expr
	return exprres;
    }

    public URI extractDatatype( long intid ) throws SQLException, AlignmentException {
	findDatatypeUri.setLong( 1, intid );
	try ( ResultSet rs = findDatatypeUri.executeQuery() ) {
	    if ( rs.next() ) {
		try {
		    return new URI( rs.getString("uri") );
		} catch (URISyntaxException uriex) {
		    throw new AlignmentException( "Badly formatted URI "+rs.getString("uri"), uriex );
		}
	    } else throw new AlignmentException( "Cannot retrieve datatype "+intid );
	}
    }

    // Note: in EDOAL, values have datatypes which simply are URIs
    public long visitDatatype( String uri ) throws SQLException {
	// If it exists do not add:
	findDatatypeIdByUri.setString( 1, uri );
	try ( ResultSet rs = findDatatypeIdByUri.executeQuery() ) {
	    if ( rs.next() ) {
		return rs.getLong("intid"); // try getInt on tables
	    } else {
		insertDatatype.setString( 1, uri );
		return executeUpdateWithId( insertDatatype, "typeexpr" );
	    }
	}
    }

    public long visit( Datatype e ) throws SQLException {
	return visitDatatype( e.getType() );
    }

    // Called for each cell in an edoal alignment
    public void extractTransformations( String cellid, EDOALCell cell ) throws SQLException, AlignmentException {
	// For all the transformations with cellid as cellid
	findTransf.setString( 1, cellid );
	try ( ResultSet rs = findTransf.executeQuery() ) {
	    // Store the result
	    while ( rs.next() ) { 
		int type = rs.getInt( "type" );
		String tp; 
		if ( type == OO ) tp = "oo";
		else if ( type == O_ ) tp = "o-";
		else if ( type == _O ) tp = "-o";
		else throw new AlignmentException( "Invalid transformation type : "+type );
		cell.addTransformation( new Transformation( tp, 
							    extractValueExpression( rs.getLong( "joinid1" ) ),
							    extractValueExpression( rs.getLong( "joinid2" ) ) ) );
	    }
	}
    }

    // Called for each cell in an edoal alignment
    public long visit( final Transformation transf, String cellid ) throws SQLException, AlignmentException {
	String tp = transf.getType();
	long ob1 = visit( transf.getObject1() );
	long ob2 = visit( transf.getObject2() );
	int type;
	if ( tp.equals("oo") ) type = OO;
	else if ( tp.equals( "o-" ) ) type = O_;
	else if ( tp.equals( "-o" ) ) type = _O;
	else throw new AlignmentException( "Invalid transformation type : "+tp );
	insertTransf.setString( 1, cellid );
	insertTransf.setInt( 2, type );
	insertTransf.setLong( 3, ob1 );
	insertTransf.setLong( 4, ob2 );
	return executeUpdateWithId( insertTransf, "transformation" );
    }

    // Called for each cell in an edoal alignment
    public void extractLinkkeys( String cellid, EDOALCell cell ) throws SQLException, AlignmentException {
	// For all the Linkkeys with cellid as cellid
	findLinkkey.setString( 1, cellid );
	try ( ResultSet rs = findLinkkey.executeQuery() ) {
	    // Store the result
	    while ( rs.next() ) { 
		Linkkey linkkey = new Linkkey();
		extractBindings( rs.getLong( "intid" ), linkkey );
		cell.addLinkkey( linkkey );
	    }
	}
    }

    public void visit( final Linkkey linkkey, String cellid ) throws SQLException, AlignmentException {
	// Add the linkkey to the table
	insertLinkkey.setString( 1, cellid );
	long keyid = executeUpdateWithId( insertLinkkey, "linkkey" );
	// For each dinding add the binding
        for ( LinkkeyBinding linkkeyBinding : linkkey.bindings() ) {
	    visit( linkkeyBinding, keyid );
        }
    }

    public void extractBindings( long keyid, Linkkey key ) throws SQLException, AlignmentException {
	// For all the Linkkeys with cellid as cellid
	findBindings.setLong( 1, keyid );
	try ( ResultSet rs = findBindings.executeQuery() ) {
	    // Store the result
	    while ( rs.next() ) {
		PathExpression p1 = extractPathExpression( rs.getLong( "joinid1" ) );
		PathExpression p2 = extractPathExpression( rs.getLong( "joinid2" ) );
		if ( rs.getInt( "type" ) == EQUAL_KEY ) {
		    key.addBinding( new LinkkeyEquals( p1, p2 ) );
		} else {
		    key.addBinding( new LinkkeyIntersects( p1, p2 ) );
		}
	    }
	}
    }

    private void visit( LinkkeyBinding linkkeyBinding, long keyid ) throws SQLException, AlignmentException {
	// Store the two paths
	long p1 = visit( linkkeyBinding.getExpression1() );
	long p2 = visit( linkkeyBinding.getExpression2() );
	int type = ( linkkeyBinding instanceof LinkkeyEquals )?EQUAL_KEY:INTER_KEY;
	insertBinding.setLong( 1, keyid );
	insertBinding.setInt( 2, type );
	insertBinding.setLong( 3, p1 );
	insertBinding.setLong( 4, p2 );
	insertBinding.executeUpdate();
    }
}

