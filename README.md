# Anchored 2-core
=======================================

This is a Java implementation of "Algorithm 2.1. An efficient, exact algorithm for anchored 2-core", according to the description given in the following paper: Bhawalkar, Kshipra, et al. "Preventing unraveling in social networks: the anchored k-core problem." Automata, Languages, and Programming. Springer Berlin Heidelberg, 2012. 440-451. URL: http://link.springer.com/chapter/10.1007/978-3-642-31585-5_40

MINIMUM REQUIREMENTS:
---------------------
     o Linux Compatible PC with jdk 7 or later installed
     o The project packages

RUN THE PROGRAM:
----------------
To run the program, first compile all the classes by executing the following command 
./compile.sh which contains all classpths are necessary for the project 
then you can run the application by ./run.sh

The application uses WebGraph which is a framework for graph compression. It exploits modern compression techniques to manage very large graphs. http://webgraph.di.unimi.it

If your input graph is directed, you need to convert it to an undirected graph by complementing each edge into both directions.

If you want to test your own graph, there are some class and jar files to create graph, offsets, and properties files. All files and the description to create files based on WebGraph are available in the Tools folder.

The sample input graph is provided in file example.txt. This file contains an undirected graph, where each line represents an edge as a space-delimited pair of node IDs.

