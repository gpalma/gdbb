# classpath
HG = lib/hypergraphdb/je-5.0.34.jar:lib/hypergraphdb/hgdb-1.2.jar:lib/hypergraphdb/db-5.3.15.jar:lib/hypergraphdb/hgbdbje-1.2.jar:lib/hypergraphdb/hgbdbnative-1.2.jar:lib/hypergraphdb/hgdbp2p-1.2.jar
DEX = lib/dex/dexjava.jar
NEO = lib/neo4j/concurrentlinkedhashmap-lru-1.3.1.jar:lib/neo4j/neo4j-lucene-index-1.9.jar:lib/neo4j/geronimo-jta_1.1_spec-1.1.1.jar:lib/neo4j/neo4j-shell-1.9.jar:lib/neo4j/lucene-core-3.6.2.jar:lib/neo4j/neo4j-udc-1.9.jar:lib/neo4j/neo4j-cypher-1.9.jar:lib/neo4j/neo4j-graph-algo-1.9.jar:lib/neo4j/org.apache.servicemix.bundles.jline-0.9.94_1.jar:lib/neo4j/neo4j-graph-matching-1.9.jar:lib/neo4j/scala-library-2.10.0.jar:lib/neo4j/neo4j-jmx-1.9.jar:lib/neo4j/server-api-1.9.jar:lib/neo4j/neo4j-kernel-1.9.jar

LIBS = "./:$(HG):$(DEX):$(NEO)"
FLAGS = -source 6 -nowarn -cp

all: compile Neo4j DEX HyperGraphDB

compile:
	javac $(FLAGS) $(LIBS) ve/usb/gdbb/*.java
	javac $(FLAGS) $(LIBS) testgdbb/*.java

Neo4j:
	javac $(FLAGS) $(LIBS) experiment/Neo4j/creategraphs/*.java
	javac $(FLAGS) $(LIBS) experiment/Neo4j/algorithms/*.java

DEX:
	javac $(FLAGS) $(LIBS) experiment/DEX/creategraphs/*.java
	javac $(FLAGS) $(LIBS) experiment/DEX/algorithms/*.java

HyperGraphDB:
	javac $(FLAGS) $(LIBS) experiment/HyperGraphDB/creategraphs/*.java
	javac $(FLAGS) $(LIBS) experiment/HyperGraphDB/algorithms/*.java

clean:
	rm ve/usb/gdbb/*.class
	rm testgdbb/*.class
	rm experiment/Neo4j/creategraphs/*.class
	rm experiment/Neo4j/algorithms/*.class
	rm experiment/DEX/creategraphs/*.class
	rm experiment/DEX/algorithms/*.class
	rm experiment/HyperGraphDB/creategraphs/*.class
	rm experiment/HyperGraphDB/algorithms/*.class
