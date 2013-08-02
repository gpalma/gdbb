#Graphium
**GDBB** | Graph DataBase Benchmark

Welcome to *Graphium*, a suite of performance benchmarks for graph databases developed at Universidad Simón Bolívar, Caracas, Venezuela.

[www.graphium.ldc.usb.ve](http://graphium.ldc.usb.ve)

##Getting

You can download this benchmark form GitHub by runing:

    git clone git://github.com/gpalma/gdbb.git

##Compiling

First of all, place your self into the *Graphium* directory and compile the entire package:

    make all

In order to compile just the core of the benchmark, run `make compile`. You can compile only the test runners for Neo4j, Neo4j with Cypher, DEX or HyperGraphDB by runing `make Neo4j`, `make Neo4jCypher`, `make DEX` or `make HyperGraphDB` respectively. To delete the _.class_ files use `make clean`.

##Running

First of all you need to uncompress the _.bz2_ files of each graph, located in `graphs/files`.

For running a test in the *Graphium* benchmark you must run:

    ./run.sh <GDBM> <TEST> <GRAPH_INDEX> [ <TEST_INDEX> ]

Where `<GDBM>` must be:

- `Neo4j`
- `Neo4jCypher` ( _Neo4j_ using _Cypher Query Language_ )
- `DEX`
- `HyperGraphDB`

And `<TEST>` should be:

- `Create`
- `Load`
- `DensestGraph`
- `GraphSumm`
- `Reachability`
- `ShortPath`
- `PatternMatching`

Also, `<GRAPH_INDEX>` is one of:

- `1` _for_ DSJC1000.1
- `2` _for_ DSJC1000.5
- `3` _for_ DSJC1000.9
- `4` _for_ USA-road-d.NY
- `5` _for_ USA-road-d.FLA
- `6` _for_ SSCA2-17
- `7` _for_ RANDOM-1M
- `8` _for_ R-MAT-1M
- `9` _for_ Random-0.3-1
- `10` _for_ Random-0.3-5
- `11` _for_ Random-0.3-9
- `12` _for_ fixed-number-arcs-0.1
- `13` _for_ fixed-number-arcs-0.5
- `14` _for_ fixed-number-arcs-0.9
- `15` _for_ Berlin10M
- `16` _for_ Berlin5M

Additionally, if you want to run a `Reachability` or `PatternMatching` test, you need a fourth parameter:
Options for running `PatternMatching`, use the next as `<TEST_INDEX>` (a number in [0..8]):

- `0` _for_ **AdjacentX**
- `1` _for_ **AdjacentXP**
- `2` _for_ **EdgeBetween**
- `3` _for_ **External 2-Hop**
- `4` _for_ **External 3-Hop**
- `5` _for_ **External 4-Hop**
- `6` _for_ **Internal 2-Hop**
- `7` _for_ **Internal 3-Hop**
- `8` _for_ **Internal 4-Hop**

Options for running `Reachability`, use the next as `<TEST_INDEX>` (a number in [0..3]):

- `0` _for_ **External BFS**
- `1` _for_ **Internal BFS**
- `2` _for_ **External DFS**
- `3` _for_ **Internal DFS**
