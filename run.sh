#!/bin/bash
HG="lib/hypergraphdb/je-5.0.34.jar:lib/hypergraphdb/hgdb-1.2.jar:lib/hypergraphdb/db-5.3.15.jar:lib/hypergraphdb/hgbdbje-1.2.jar:lib/hypergraphdb/hgbdbnative-1.2.jar:lib/hypergraphdb/hgdbp2p-1.2.jar"
DEX="lib/dex/dexjava.jar"
NEO="lib/neo4j/concurrentlinkedhashmap-lru-1.3.1.jar:lib/neo4j/neo4j-lucene-index-1.9.jar:lib/neo4j/geronimo-jta_1.1_spec-1.1.1.jar:lib/neo4j/neo4j-shell-1.9.jar:lib/neo4j/lucene-core-3.6.2.jar:lib/neo4j/neo4j-udc-1.9.jar:lib/neo4j/neo4j-cypher-1.9.jar:lib/neo4j/neo4j-graph-algo-1.9.jar:lib/neo4j/org.apache.servicemix.bundles.jline-0.9.94_1.jar:lib/neo4j/neo4j-graph-matching-1.9.jar:lib/neo4j/scala-library-2.10.0.jar:lib/neo4j/neo4j-jmx-1.9.jar:lib/neo4j/server-api-1.9.jar:lib/neo4j/neo4j-kernel-1.9.jar"
LIBS="./:$HG:$DEX:$NEO"
FLAGS="-Xms16240m -Xmx16240m -XX:PermSize=14240m -XX:MaxPermSize=14240m -XX:-UseGCOverheadLimit "

case $2 in
  "Create") 
  java $FLAGS -classpath $LIBS experiment.$1.creategraphs.Create $3;;
  "Load")
  java $FLAGS -classpath $LIBS experiment.$1.creategraphs.Load $3;;
  "DensestGraph")
  java $FLAGS -classpath $LIBS experiment.$1.algorithms.densestgraph $3;;
  "GraphSumm")
  java $FLAGS -classpath $LIBS experiment.$1.algorithms.graphsumm $3;;
  "Reachability")
  java $FLAGS -classpath $LIBS experiment.$1.algorithms.reachability $3 $4;;
  "ShortPath")
  java $FLAGS -classpath $LIBS experiment.$1.algorithms.shortpath $3;;
  "PatternMatching")
  java $FLAGS -classpath $LIBS experiment.$1.algorithms.patternmatching $3 $4;;
  "dropcache")
  sudo /experiment2/drop_cache.sh;;
esac
