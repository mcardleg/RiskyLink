
# Ontology alignment API and implementation
# [https://moex.gitlabpages.inria.fr/alignapi/](https://moex.gitlabpages.inria.fr/alignapi/)
# 21/11/2020, version 4.10

<pre>
Copyright (C) 2003-2021 INRIA.
Copyright (C) 2004-2005 Université de Montréal.
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

Read the [LICENSE.TXT](LICENSE.TXT) file for the terms of the LGPL license.
</pre>

## WHAT IS AN ONTOLOGY ALIGNMENT?

See [https://moex.gitlabpages.inria.fr/alignapi/](https://moex.gitlabpages.inria.fr/alignapi/).

See also http://www.ontologymatching.org

## USING THE ONTOLOGY ALIGNMENT API IMPLEMENTATION

### Building:

~~~~
$ ant jar
~~~~
(or ant `compileall`)

### Testing

~~~~
$ ant test
~~~~

### Running

For a better introduction, see the tutorial at https://moex.gitlabpages.inria.fr/alignapi/tutorial
or find it as a script in `html/tutorial/script.sh`

~~~~
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
~~~~

## Using with JWNL (Wordnet)

Requirements:
* Wordnet should be installed its directory to be put in `$WNDIR` (3.0 and 3.1 should work)
* `jwnl.jar` `commons-logging.jar` must be in `lib` (`file_properties.xml` need not anymore to be in current directory)

~~~~
$ export WNDIR=../WordNet-3.1/dict

$ java -jar lib/procalign.jar -Dwndict=$WNDIR file://$CWD/examples/rdf/edu.umbc.ebiquity.publication.owl file://$CWD/examples/rdf/edu.mit.visus.bibtex.owl -i fr.inrialpes.exmo.align.ling.JWNLAlignment -o examples/rdf/JWNL.rdf
~~~~

## LAST RELEASE

The last release is available from: https://gitlab.inria.fr/moex/alignapi/-/releases
It may actually be easier to clone the git repository from there.

## DOCUMENTATION

The documentation can be found online at https://moex.gitlabpages.inria.fr/alignapi/.

## SOURCE REPOSITORY

See https://gitlab.inria.fr/moex/alignapi

## FILES

<ul>
<li><tt>README.md</tt>: This file</li>
<li><a href="LICENSE.TXT"><tt>LICENSE.TXT</tt></a>:	the terms under which the software is licensed to you.</li>
<li><tt>build.xml</tt>:	the Ant build file for compiling and testing</li>
<li><tt>classes/</tt>:	directory for compiling the sources</li>
<li><tt>distrib/</tt>:	some files for generating new jarfiles</li>
<li><tt>dtd/</tt>: contains the ontoalign DTDs (and schemas)</li>
<li><tt>examples/</tt>:	examples of use of the API and server</li>
<li><tt>html/</tt>: contains some documentation in HTML format (includes `html/relnotes.html`)</li>
<li><tt>html/tutorial/</tt>:  tutorial</li>
<li><tt>javadoc/</tt>:	javadoc API documentation in HTML (not very useful)</li>
<li><tt>lib/</tt>: contains align.jar, procalign.jar, alignsvc.jar
</tt>: 	 and other necessary jarfiles</li>
<li><tt>plugins/neon/</tt>:	Plug-in for the NeOn toolkit</li>
<li><tt>src/</tt>: Java sources of ontoalign<br />
<tt>org.semanticweb.owl.align</tt>: the API<br />
<tt>fr.inrialpes.exmo.align.impl</tt>: basic implementation<br />
<tt>fr.inrialpes.exmo.align.util</tt>: utility functions<br />
<tt>fr.inrialpes.exmo.align.cli</tt>: command line interface<br />
<tt>fr.inrialpes.exmo.align.ling</tt>: WordNet-based implementation<br />
<tt>fr.inrialpes.exmo.align.service</tt>: Alignment Service<br />
<tt>fr.inrialpes.exmo.align.parser</tt>: Alignment format parsers<br />
<tt>fr.inrialpes.exmo.align.gen</tt>: test generators<br />
<tt>fr.inrialpes.exmo.ontowrap</tt>: Abstract ontology layer</li>
<li><tt>test/</tt>: Unit tests for testng</li>
<li><tt>tools/</tt>: Compile-time tools (testng, etc.)</li>
</ul>

## RUNNING THE ALIGNMENT SERVER

For information on how to setup an alignment setver, please consult the
more elaborate documentation at: [`html/aserv.html`](html/aserv.html)

~~~~
$ java -jar lib/alignsvc.jar -H
$ java -jar lib/alignsvc.jar -Dwndict=../WordNet-3.1/dict -H
~~~~

The alignment server is then available through HTTP with: http://localhost:8089/html/

For debugging, using logback, do:
~~~~
$ java -cp lib/slf4j/logback-core-1.2.10.jar:lib/slf4j/logback-classic-1.2.10.jar:lib/alignsvc.jar -Dlogback.configurationFile=logback.xml fr.inrialpes.exmo.align.service.AlignmentService -Dwndict=../WordNet-3.1/dict -H
~~~~
