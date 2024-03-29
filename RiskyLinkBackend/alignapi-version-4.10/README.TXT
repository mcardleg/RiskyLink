#######################################################################
#              Ontology alignment API and implementation              #
#                      21/11/2020, version 4.10                        #
#                  http://alignapi.gforge.inria.fr                    #
#######################################################################

Copyright (C) 2003-2020 INRIA.
Copyright (C) 2004-2005 Universit� de Montr�al.
Copyright (C) 2005 CNR Pisa.
Copyright (C) 2005 Konstantinos A. Nedas.
Copyright (C) 2006 CERT.
Copyright (C) 2006 Seungkeun Lee.
Copyright (C) 2006-2007 Orange R&D.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

Read the LICENSE.TXT file for the terms of the LGPL license.

WHAT IS ONTOLOGY ALIGNMENT?
---------------------------

See http://alignapi.gforge.inria.fr.
See also http://www.ontologymatching.org

USING THE ONTOLOGY ALIGNMENT API IMPLEMENTATION
-----------------------------------------------

Building:
---------

$ ant jar
(or ant compileall)

Testing
-------

$ ant test

Running:
--------

For a better introduction, see the tutorial at 
      	     		   http://alignapi.gforge.inria.fr/tutorial
or find it as a script in html/tutorial/script.sh

$ export CWD=`pwd`

$ java -jar lib/procalign.jar --help

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/onto1.owl file://$CWD/examples/rdf/onto2.owl

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/onto1.owl file://$CWD/examples/rdf/onto2.owl -i fr.inrialpes.exmo.align.impl.method.StringDistAlignment -DstringFunction=levenshteinDistance -r fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/onto1.owl file://$CWD/examples/rdf/onto2.owl -i fr.inrialpes.exmo.align.impl.method.StringDistAlignment -DstringFunction=levenshteinDistance -t 0.4 -o examples/rdf/sample.rdf

$ java -cp lib/procalign.jar fr.inrialpes.exmo.align.cli.ParserPrinter file:examples/rdf/newsample.rdf

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/onto1.owl file://$CWD/examples/rdf/onto2.owl -a examples/rdf/sample.rdf

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/edu.umbc.ebiquity.publication.owl file://$CWD/examples/rdf/edu.mit.visus.bibtex.owl

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/edu.umbc.ebiquity.publication.owl file://$CWD/examples/rdf/edu.mit.visus.bibtex.owl -i fr.inrialpes.exmo.align.impl.method.StringDistAlignment -DstringFunction=levenshteinDistance -o examples/rdf/bibref.rdf

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/edu.umbc.ebiquity.publication.owl file://$CWD/examples/rdf/edu.mit.visus.bibtex.owl -i fr.inrialpes.exmo.align.impl.method.StringDistAlignment -DstringFunction=subStringDistance -t .4 -o examples/rdf/bibref2.rdf

$ java -cp lib/procalign.jar fr.inrialpes.exmo.align.cli.EvalAlign -i fr.inrialpes.exmo.align.impl.eval.PRecEvaluator file://$CWD/examples/rdf/bibref2.rdf file://$CWD/examples/rdf/bibref.rdf

$ java -jar lib/procalign.jar file://$CWD/examples/rdf/edu.umbc.ebiquity.publication.owl file://$CWD/examples/rdf/edu.mit.visus.bibtex.owl -i fr.inrialpes.exmo.align.impl.method.StringDistAlignment -DstringFunction=levenshteinDistance -DprintMatrix=1 -o /dev/null > examples/rdf/matrix.tex

Using with JWNL (Wordnet)
-------------------------

- Wordnet should be installed its directory to be put in $WNDIR (3.0 and 3.1 should work)
- jwnl.jar commons-logging.jar must be in lib (file_properties.xml need not anymore to be in current directory)

$ export WNDIR=../WordNet-3.1/dict

$ java -jar lib/procalign.jar -Dwndict=$WNDIR file://$CWD/examples/rdf/edu.umbc.ebiquity.publication.owl file://$CWD/examples/rdf/edu.mit.visus.bibtex.owl -i fr.inrialpes.exmo.align.ling.JWNLAlignment -o examples/rdf/JWNL.rdf

LAST RELEASE
------------

The last release is available from: http://gforge.inria.fr/frs/?group_id=117

DOCUMENTATION
-------------

The documentation can be found online at http://alignapi.gforge.inria.fr

SOURCE REPOSITORY
-----------------

See http://gforge.inria.fr/scm/?group_id=117 for SVN Access.

FILES
-----

README.TXT	this file
LICENSE.TXT	the terms under which the software is licensed to you.
build.xml	the Ant build file for compiling and testing
classes/	directory for compiling the sources
distrib/	some files for generating new jarfiles
dtd/		contains the ontoalign DTDs (and schemas)
examples/	examples of use of the API and server
html/		contains some documentation in HTML format
			 (includes relnotes.html)
html/tutorial/  tutorial
javadoc/	javadoc API documentation in HTML (not very useful)
lib/		contains align.jar, procalign.jar, alignsvc.jar
			 and other necessary jarfiles
plugins/neon/	Plug-in for the NeOn toolkit
src/		Java sources of ontoalign
		org.semanticweb.owl.align: the API
		fr.inrialpes.exmo.align.impl: basic implementation
		fr.inrialpes.exmo.align.util: utility functions
		fr.inrialpes.exmo.align.cli: command line interface
		fr.inrialpes.exmo.align.ling: WordNet-based implementation
		fr.inrialpes.exmo.align.service: Alignment Service
                fr.inrialpes.exmo.align.parser: Alignment format parsers
                fr.inrialpes.exmo.align.gen: test generators
		fr.inrialpes.exmo.ontowrap: Abstract ontology layer
test/		Unit tests for testng
tools/		Compile-time tools (testng, etc.)

RUNNING THE ALIGNMENT SERVER
----------------------------

For information on how to setup an alignment setver, please consult the
more elaborate documentation at: html/aserv.html

$ java -jar lib/alignsvc.jar -H
$ java -jar lib/alignsvc.jar -Dwndict=../WordNet-3.1/dict -H

The alignment server is then available through HTTP with:

    	      http://localhost:8089/html/

For debugging, using logback, do:
$ java -cp lib/slf4j/logback-core-1.2.3.jar:lib/slf4j/logback-classic-1.2.3.jar:lib/alignsvc.jar -Dlogback.configurationFile=logback.xml fr.inrialpes.exmo.align.service.AlignmentService -Dwndict=../WordNet-3.1/dict -H
