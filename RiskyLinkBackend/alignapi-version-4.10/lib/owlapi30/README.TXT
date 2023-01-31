--------------------------------------------------------------------------
2020-10-22

* What is the status?

Currently supported version is 3.5.6

owlapi-bin (old terminology) corresponds in fact to owl-distribution (new terminology)

What could be considered is to create an ontowrap.owlapi5
- supporting OWL API 5 and eventually 4
- if there is a corresponding HermiT implementation to use (currently there are compatibility packages of HermiT developed by OWL API developpers)
(+ see below about package order)

If you have implemented ontosim for OWL API 4.x or if you need it, please do not hesitate to let us know.

* OWL API 3.5.6 (2017)

Ships with guava 18 and trove4j 3.0.3.
And seems to work.

* OWL API 4.5.14

Use the same guava 18 and trove4j 3.0.3.

22 errors in ontowrap that would have to be rewritten
4 warnings (deprecation) in SemPRecEvaluator

* OWL API 5.1.12 (2019)

Ships with guava 22 and not trove4j anymore

22 errors in ontowrap that would be to be rewritten
9 warning in ontowrap
1 error (about needed IRI but quite strange) in SemPRecEvaluator
4 warnings (deprecation) in SemPRecEvaluator

--------------------------------------------------------------------------
2017-07-07

* Why do we stick to OWL API 3.5.0?

Version 3.5.1 and above (up to 3.5.4) do not work, they have the issue:
ClassNotFoundException: com.google.common.cache.CacheLoader
when running: OWLManager.createOWLOntologyManager()
This then causes NoClassDefFoundError Could not initialize class uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl

This is due to relying on guava without providing it.

If you really need it, you may add a guava library in the build.xml wherever the owlapi-bin.jar is, replace the owlapi-bin.jar by a newer 3.x and it should work (not tested).

* What about 4.x?

Version 4.x has a drastically modified API
see: https://github.com/owlcs/owlapi/wiki/Migrate-from-version-3.4-and-3.5-to-4.0
and so far not supported by HermiT

Integrating it would require a new OWLAPI40 implementation in ontosim, like that was done for 3.x
Like that was done for 2.x, the OWL API developer did not change the package names with the new API.
But it seems possible to have 3.x and 4.x to cohabits, contrary to 2.x and 3.x.
It is sufficient to change the order of the jars in the build.xml (tested with 3.5.0 and 4.1.4)

We however decided to wait for wider adoption of 4.x
