/**
 * K-core decomposition algorithm
 *
 * Outputs: array "int[] res" containing the core values for each vertex.  
 *
 * This is an implementation of the algorithm given in: 
 * V. Batagelj and M. Zaversnik. An o (m) algorithm for cores decomposition of networks. CoRR, 2003.
 * 
 * The graph is stored using Webgraph (see P. Boldi and S. Vigna. The webgraph framework I: compression techniques. WWW 04.)
 *
 * @author Alex Thomo, thomo@uvic.ca, 2015
 */
package kcore;

import java.io.*;
import java.util.*;

import ca.uvic.css.graph.Graph;
import ca.uvic.css.graph.GraphWebgraph;

public class KCoreWG_BZ {
	public Graph graph;
	private boolean printprogress = false; 
	private long E;
    private int[] vertex;
    private int[] position;
    private int[] degree;
	private int[] bin;
    private int[] degreeGraph;

	public KCoreWG_BZ(String edgesfilename, String storageType) throws Exception {
        
		if (storageType.equals("webgraph")) {
			graph = new GraphWebgraph(edgesfilename, "memory");
            
            vertex = new int[graph.size()];
            position = new int[graph.size()];
            degree = new int[graph.size()];
            bin = new int[graph.maxDegree() + 1];
            degreeGraph = new int[graph.size()];
            this.E = 0;
        }
	}
	
    public long getE() {
        return E;
    }

    public int[] getVertex() {
        return vertex;
    }

    public int[] getPosition() {
        return position;
    }

    public int[] getDegree() {
        return degreeGraph;
    }

    public int[] getBin() {
        return bin;
    }

    public void KCoreCompute () {
    	int n = graph.size(); 
        int md = graph.maxDegree(); 
    	/*int[] vertex = new int[n];
    	int[] position = new int[n];
    	int[] degree = new int[n];
    	int[] bin = new int[md + 1];*/ //md+1 because we can zero degree 

    	for(int d = 0; d <= md; d++) 
    		bin[d] = 0;
    	for(int v = 0; v < n; v++) { 
    		if (printprogress && v % 1000000 == 0) 
    			System.out.println(v);
    		degree[v] = graph.outdegree(v); 
            E += degree[v];
    		bin[ degree[v] ]++;
    	}
        
        System.arraycopy(degree, 0, degreeGraph, 0, degree.length);
        /*for(int j = 0; j < degree.length; ++j) 
            System.out.println("degree["+j+"]="+degree[j]);*/

    	int start = 0; //start=1 in original, but no problem
    	for(int d = 0; d <= md; d++) {
    		int num = bin[d];
    		bin[d] = start;
    		start += num;
    	}

    	//bin-sort vertices by degree
    	for(int v = 0; v < n; v++) {
    		position[v] = bin[ degree[v] ];
    		vertex[ position[v] ] = v;
    		bin[ degree[v] ]++;
    	}

    	//recover bin[]
    	for(int d = md; d >= 1; d--) 
    		bin[d] = bin[d-1];
    	bin[0] = 0; //1 in original

    	//main algorithm
    	for(int i = 0; i < n; i++) {
    		if (printprogress && i % 1000000 == 0) 
    			System.out.println(i);
    		int v = vertex[i]; //smallest degree vertex
    		int[] N_v = graph.getNeighbors(v);
    		for(Integer u : N_v) {
    			if(degree[u] > degree[v]) {
    				int du = degree[u]; 
                    int pu = position[u];
    				int pw = bin[du]; 
                    int w = vertex[pw];
    				if(u != w) {
    					position[u] = pw; 
                        vertex[pu] = w;
    					position[w] = pu; 
                        vertex[pw] = u;
    				}
    				bin[du]++;
    				degree[u]--;
    			}
    		}
    	}
            
    	return;
    }
    
	/*public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		
		//args = new String[] {"simplegraph"};
		
		System.out.println("Starting " + args[0]);
		KCoreWG_BZ kc3 = new KCoreWG_BZ(args[0], "webgraph");
		
		int[] res = kc3.KCoreCompute();
		int kmax = -1;
		double sum = 0;
		int cnt = 0;
		for(int i=0; i<res.length; i++) {
			//System.out.print(i+":" + res[i] + " ");
			if(res[i] > kmax) 
				kmax = res[i];
			sum += res[i];
			if(res[i] > 0) cnt++;
		}
		System.out.println("|V|	|E|	dmax	kmax	kavg");
		System.out.println(cnt + "\t" + (kc3.E/2) + "\t" + kc3.graph.maxDegree() + "\t" + kmax + "\t" + (sum/cnt) );
		
		long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(args[0] + ": Time elapsed = " + estimatedTime/1000.0);
	}*/
}
