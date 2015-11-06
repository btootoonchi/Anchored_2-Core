package ca.uvic.css.graph;

import java.util.Iterator;
import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;

public class GraphWebgraph implements Graph {
	ImmutableGraph immutableGraph; //graph
	Integer maxDegree = null;
	
	public GraphWebgraph(String edgesfilename, String mode) throws Exception {
		final ProgressLogger pl = new ProgressLogger();
		pl.logInterval = 1000; //millisec
		
		if(mode.equals("memory")) {
			System.out.println("3mode: " + edgesfilename);
			immutableGraph = ImmutableGraph.load(edgesfilename, pl);
			System.out.println("Memory loaded graph!");
		} else if (mode.equals("memory-mapped")) {
			immutableGraph = ImmutableGraph.loadMapped(edgesfilename, pl); //We need random access
			System.out.println("Memory-mapped graph!");
		} else {
			immutableGraph = ImmutableGraph.loadOffline(edgesfilename, pl); //We need random access
			System.out.println("Loading offline the graph!");
		}
	}
	
	public int maxDegree() {
		if(maxDegree != null)
			return maxDegree;
		
		Iterator<Integer> degIter = immutableGraph.outdegrees();
		maxDegree = -1;
		while(degIter.hasNext()) {
			Integer deg = degIter.next(); 
			if(deg > maxDegree)
				maxDegree = deg;
		}
		return maxDegree;
	}
	
	public int size() {
		return immutableGraph.numNodes();
	}
	
	public int[] getNeighbors(int u) {
		return immutableGraph.successorArray(u);
	}
	
	public int outdegree(int u) {
		return immutableGraph.outdegree(u);
	}
	
	public Iterator<Integer> vertexIterator(){
		return immutableGraph.nodeIterator();
	}	
}
