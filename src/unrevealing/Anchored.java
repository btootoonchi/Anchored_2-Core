package unrevealing;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.NodeIterator;
// import it.unimi.dsi.webgraph.ArrayListMutableGraph;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import ca.uvic.css.graph.Graph;
import ca.uvic.css.graph.GraphWebgraph;
import kcore.KCoreWG_BZ;
import kcore.KCoreWG_M;
import ca.uvic.css.util.*;

public class Anchored {
	private static Anchored uniqueInstance;
	private static Graph graph; 

	private Anchored() {}

	public static Anchored getInstance() {
		if (uniqueInstance == null) 
			uniqueInstance = new Anchored();
		return uniqueInstance;
	}

	public static Graph getGraphInstance(String path_input) {
		if (graph == null) {
			try {
				graph = new GraphWebgraph(path_input, "memory");
			} catch(Exception e){
        		return null;            // Always must return something
    		}
    	}
		return graph;
	}

	public static void process(String path_input, String path_output) throws  ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, IOException {
    	// final ImmutableGraph graph = it.unimi.dsi.webgraph.ImmutableGraph.loadOffline( path_input );
        final ImmutableGraph graph = it.unimi.dsi.webgraph.ImmutableGraph.load( path_input );

    	long startTime = System.currentTimeMillis();

    	NodeIterator nodeIterator = graph.nodeIterator();
    	int currentNode, outDegree;
    	int[] neighbors;
    	int[] vertex = new int[graph.numNodes()];
    	Map<Integer, Integer> rooted = new HashMap<Integer, Integer>();
        Map<Integer, Integer> graphMap = new HashMap<Integer, Integer>();

    	BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path_output)));
    	System.out.println(graph.numNodes() + " " + graph.numArcs() + " " + graph.basename());
    	long cnt = 0;

    	while( nodeIterator.hasNext() ) {
    		currentNode = nodeIterator.nextInt();
    		outDegree = nodeIterator.outdegree();
    		neighbors = nodeIterator.successorArray();

            /*Integer node = graphMap.get(currentNode);
            if (node == null) {
                    
            }
    		if (outDegree == 1) {
    			Integer successor = rooted.get(neighbors[0]);
    			
    			if (successor != null) {
    				if (successor != currentNode)
    					rooted.remove(neighbors[0]);
    			}
    			else {
    				if (!rooted.containsKey(neighbors[0])) {
    					if (currentNode != neighbors[0]) {
    						rooted.put(neighbors[0], currentNode);
    					}
    				}
    			}
    		}*/
            if (currentNode == 13) 
                System.out.println("outDegree:"+outDegree);

    		for( int j = 0; j < outDegree; j++ ) {
    			bout.write( currentNode + "," + neighbors[j] + "," + outDegree + "\n" );
    			++cnt;
    			if( cnt % 10000 == 0 )
    				System.out.println(cnt);
    		}
    		//outStream.println();
    	}
    	
    	/*Iterator<Entry<Integer, Integer>> iterator = rooted.entrySet().iterator();
    	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./dataset/rooted.txt")));
    	while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) iterator.next();
    		out.write("Key : " + entry.getKey() + " Value :" + entry.getValue() + "\n");
    	}
    	out.close();*/
    	bout.close();
    	long endTime = System.currentTimeMillis();
    	System.out.println ("Total time for processing "+(endTime - startTime) +" ms.");
    	System.out.println( "finish" );
    }

    public static void compute(String path_input, String path_output) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, Exception, IOException {
        long startTime = System.currentTimeMillis();
        
        //args = new String[] {"simplegraph"};
        
        System.out.println("BZ Starting " + path_input);
        KCoreWG_BZ kc3 = new KCoreWG_BZ(path_input, "webgraph");
        
        kc3.KCoreCompute();
        int kmax = -1;
        double sum = 0;
        int cnt = 0;
        int deg[] = kc3.getDegree();
        int ver[] = kc3.getVertex();
        int pos[] = kc3.getPosition(); 
        
        // Arrays.sort(deg);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./dataset/out.txt")));
        for(int i = 0; i< deg.length; i++) {
            //System.out.print(i+":" + res[i] + " ");
            out.write("degree[" + i + "] = " + deg[i] + " vertex[" + i + "] = " + ver[i] + " position[" + i + "] = " + pos[i] + "\n");
            if(deg[i] > kmax) 
                kmax = deg[i];
            sum += deg[i];
            if(deg[i] > 0) cnt++;
        }

        out.close();
        System.out.println("|V| " + "\t" +" |E| " + "\t" + " dmax " + "\t" +" kmax " + "\t" + " kavg");
        System.out.println(cnt + "\t" + (kc3.getE()/2) + "\t" + kc3.graph.maxDegree() + "\t" + kmax + "\t" + (sum/cnt) );

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(path_input + ": Time elapsed = " + estimatedTime/1000.0);
            
        
        System.out.println("res[0] = " + deg[0] + " res[" + deg.length + "] = " + deg[deg.length-1]);
    }

    public static void compute_M(String path_input, String path_output) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, Exception, IOException {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting " + path_input);
        KCoreWG_M kc4 = new KCoreWG_M(path_input, "webgraph");
        kc4.KCoreCompute();
        System.out.println("Number of iterations="+kc4.iteration);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./dataset/M.txt")));
        for(int i = 0; i < kc4.core.length; i++)
            out.write(i +":" + kc4.core[i] + " \n");
        out.close();
        
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(path_input + ": Time elapsed = " + estimatedTime/1000.0);
    }

    public static void createMmutableGraph(String path_input, String path_output) throws  ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, IOException {
        final ImmutableGraph graph = it.unimi.dsi.webgraph.ImmutableGraph.load( path_input );

        long startTime = System.currentTimeMillis();
        it.unimi.dsi.webgraph.ArrayListMutableGraph mutableGraph = new it.unimi.dsi.webgraph.ArrayListMutableGraph(graph);

        mutableGraph.removeNode(12);
        ImmutableGraph gView = mutableGraph.immutableView();

        NodeIterator nodeIterator = gView.nodeIterator();
        int currentNode, outDegree;
        int[] neighbors;
        int[] vertex = new int[gView.numNodes()];

        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./dataset/gview.txt")));
        System.out.println(mutableGraph.numNodes() + " " + mutableGraph.numArcs() + " ") ;
        long cnt = 0;

        while( nodeIterator.hasNext() ) {
            currentNode = nodeIterator.nextInt();
            outDegree = nodeIterator.outdegree();
            neighbors = nodeIterator.successorArray();

            if (currentNode == 13) 
                System.out.println("outDegree:"+outDegree);
            
            for( int j = 0; j < outDegree; j++ ) {
                bout.write( currentNode + "," + neighbors[j] + "," + outDegree + "\n" );
                ++cnt;
                if( cnt % 10000 == 0 )
                    System.out.println(cnt);
            }
            //outStream.println();
        }
        bout.close();
        long endTime = System.currentTimeMillis();
        System.out.println ("Total time for processing "+(endTime - startTime) +" ms.");
        System.out.println( "finish" );
    }

    public static void createKCore(String path_input, String path_output, int k) throws  ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, Exception, IOException {
        long startTime = System.currentTimeMillis();
        boolean done = false;

        KCoreWG_BZ kc3 = new KCoreWG_BZ(path_input, "webgraph");
        ImmutableGraph graph = it.unimi.dsi.webgraph.ImmutableGraph.load( path_input );
        it.unimi.dsi.webgraph.ArrayListMutableGraph mutableGraph = new it.unimi.dsi.webgraph.ArrayListMutableGraph(graph);

        int j = 1;
        // do {
            done = false;     
            kc3.KCoreCompute();
            int deg[] = kc3.getDegree();
            int ver[] = kc3.getVertex();
            int pos[] = kc3.getPosition();
            int bin[] = kc3.getBin();

            for(int i = 0; i< deg.length; ++i) {
                System.out.println("degree[" + i + "] = " + deg[i] + " vertex[" + i + "] = " + ver[i] + " position[" + i + "] = " + pos[i] + "\n");
            }

            for(int b = 0; b < bin.length - 1; ++b)
                System.out.println("bin["+ b +"]="+bin[b]);

            for(int v = 1; v < bin[k]; ++v) {
                if (deg[v] < k) {
                    System.out.println("vertex: "+v+" deg: "+deg[v]);
                    mutableGraph.removeNode(v);
                }
            }

            if (bin[k] == 1) 
                done = true;
            /*for(int v = 1; v <= ver.length - 1; ++v) {
                System.out.println("vertex: "+v+" deg: "+deg[v]);
                if (deg[v] < k) {
                    System.out.println("removing a ver "+v+"deg: "+deg[v]);
                    mutableGraph.removeNode(v);
                } else {
                    done = true;
                    break;
                }
            }*/

            graph = mutableGraph.immutableView();
            mutableGraph = new it.unimi.dsi.webgraph.ArrayListMutableGraph(graph);
            j++;
        // } while(j < 3); //!done);

        long endTime = System.currentTimeMillis();
        System.out.println ("Total time for processing "+(endTime - startTime) +" ms.");
        System.out.println( "finish" );
    }

    public static void createKCore_BZ(String path_input, String path_output, int k) throws  ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, Exception, IOException {
        long startTime = System.currentTimeMillis();

        KCoreWG_BZ kc3 = new KCoreWG_BZ(path_input, "webgraph");
        kc3.KCoreCompute();
        int deg[] = kc3.getDegree();
        int ver[] = kc3.getVertex();
        int pos[] = kc3.getPosition();
        int bin[] = kc3.getBin();

        boolean verMarked[] = new boolean[ver.length];
        int degKCore[] = new int[deg.length];

        Arrays.fill(verMarked, true);
        System.arraycopy(deg, 0, degKCore, 0, deg.length);
/*
        for(int i = 0; i< deg.length; ++i) {
            System.out.println("degree[" + i + "] = " + deg[i] + " vertex[" + i + "] = " + ver[i] + " position[" + i + "] = " + pos[i] + " degKCore["+ i +"] = "+ degKCore[i] + " verMarked["+ i +"] = "+ verMarked[i]);
        }

        for(int j = 0; j <= ver.length-1; ++j) {
            System.out.println("ver["+ver[j]+"]"+" deg: "+deg[ver[j]]);
            int[] neighbors = kc3.graph.getNeighbors(ver[j]);
            for (int x = 0; x <= neighbors.length-1; ++x)
                System.out.print(" neighbors["+x+"]="+neighbors[x]);
            System.out.println("");
        }*/
        int countRemoveCore = 0;
        boolean isIterate = false;
        int v;
        do {
            v = 0;
            isIterate = false;
            while(/*!isIterate ||*/ (degKCore[ver[v]] < k && deg[v] > 0 && v < ver.length-1)) {
                if (verMarked[v]) {
                    System.out.println("test: "+ver[v]+" d:"+degKCore[ver[v]]);
                    degKCore[ver[v]] = 0;
                    ++countRemoveCore;
                    int[] neighbors = kc3.graph.getNeighbors(ver[v]);
                    for(int i = 0; i <= neighbors.length-1; ++i) {
                        int vertex = neighbors[i];
                        System.out.println("neighbors["+ i + "] = "+ neighbors[i]);
                        if (degKCore[vertex] > 0)
                            degKCore[vertex]--; 
                        if (degKCore[vertex] < k && degKCore[vertex] > 0)
                            isIterate = true;
                        System.out.println("degKCore["+vertex+"]="+degKCore[vertex]);
                    }
                    verMarked[v] = false;
                }
                v++;
            }
            // System.out.println(isIterate + " v:"+v + " length:"+ver.length);
        } while(isIterate && v < ver.length-1); 

        for(int i = 0; i < deg.length; ++i) {
            System.out.println("degree[" + i + "] = " + deg[i] + " vertex[" + i + "] = " + ver[i] + " position[" + i + "] = " + pos[i] + " degKCore["+ i +"] = "+ degKCore[i] + " verMarked["+ i +"] = "+ verMarked[i] + "\n");
        }

        if (countRemoveCore > 0) {
            boolean isRooted = false;
            SeparateChainingHash<Integer, Integer> sth = new SeparateChainingHash<Integer, Integer>(countRemoveCore);
            for (int ve = 0; ve <= ver.length-1; ++ve) {
                if (degKCore[ver[ve]] == 0) {
                    isRooted = checkRootedTree(degKCore, ver, ve, kc3, sth);
                    System.out.println("vertex["+ve+"]="+ver[ve]+" "+isRooted);
                }
            }

            for (int i = 0; i < countRemoveCore; ++i) {
                for (Integer key : sth.st[i].keys()) {
                    System.out.println("i: " + i + ", " + key + ", " + sth.st[i].get(key));
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println ("Total time for processing "+(endTime - startTime) +" ms.");
        System.out.println( "finish" );
    }

    public static boolean checkRootedTree(int[] degreeKCore, int[] vertex, int v, KCoreWG_BZ kc3, SeparateChainingHash<Integer, Integer> sth) {
        boolean isRooted = false;

        if (degreeKCore[vertex[v]] == 0) {
            int[] neighbors = kc3.graph.getNeighbors(vertex[v]);
            for (int i = 0; i <= neighbors.length-1; ++i) {
                sth.st[v].put(neighbors[i], 1);
                if (degreeKCore[neighbors[i]] == 0)
                    isRooted = checkRootedTree(degreeKCore, vertex, neighbors[i], kc3, sth);
                else
                    isRooted = true;
            }
        }
        return isRooted;
    }
}

