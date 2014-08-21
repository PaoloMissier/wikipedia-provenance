wikipedia-provenance
====================

Updated version of the wikipedia-provenance tool to generate provenance of wikipedia history pages.

The tool supports two forms of usage:

  - via the command line to generate provenance as RDF, or store in a neo4j backend
  - via a GUI to store and query a neo4j backend


###Buiding and Using###
Build the jar using maven `mvn package`. You can then use the tool as `java -jar wikipedia-provenance-<version>-jar-with-dependencies.jar <arguments>`.

### Usage ###
```
usage: wiki2prov
 -d <arg>           depth
 -diff              diff: Evalaute diff between revisions (requires GNU
                    wdiff)
 -f <arg>           file listing URLs
                    (http://en.wikipedia.org/wiki/{title}) or titles of
                    wikipages to proccess (one per line or csv)
 -h                 Help: display this usage info
 -neo4j             neo4j: Use a neo4j store (default assumes
                    localhost:7474
 -o <arg>           directory to output to (default is cwd)
 -p <arg>           URL or title of a wiki-page for proccessing
 -r <arg>           number of revisions
 -startdate <arg>   rvstart: the timestamp to start at
 -startid <arg>     rvstartid: the numerical wikipedia revision id to
                    start at
 -t <arg>           file type of the input file, text, csv
                    (title,startid,startdate), jena (result set where URL
                    is a result var 'page'
 -u <arg>           number of user contributions
```


###Dependencies###

GNU wdiff: https://www.gnu.org/software/wdiff/ is required if you want to be generate quantitative provenance information about diffs between revisions (--diff).



###Examples###

An example generating provenance for the page Manchester, for the previous 4 revisions of the page:
```
java -jar target/wikipedia-provenance-0.0.6-jar-with-dependencies.jar -p Manchester -r 4
```


An example using Neo4j as the data store (at the default address of http://localhost:7474)
```
java -jar target/wikipedia-provenance-0.0.6-jar-with-dependencies.jar -p Amsterdam -neo4j -r 4
```
