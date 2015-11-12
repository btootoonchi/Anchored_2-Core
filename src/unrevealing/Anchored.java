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
    private static SeparateChainingHash<Integer, Integer> sth;
    private static int vertexIndex; 
    private static KCoreWG_BZ kc3;
    private static int degree[];
    private static int vertices[];
    private static int degreeKCore[];

    private static char vertexTreeStatus[];

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

    public static void findAnchors(String path_input, String path_output, int budget) throws  ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, Exception, IOException {
        long startTime = System.currentTimeMillis();

        kc3 = new KCoreWG_BZ(path_input, "webgraph");
        kc3.KCoreCompute();

        degree = kc3.getDegree();
        vertices = kc3.getVertex();

        degreeKCore = new int[degree.length];
        vertexTreeStatus = new char[vertices.length];
        boolean verMarked[] = new boolean[vertices.length];
        boolean verVisited[] = new boolean[verMarked.length];
        System.arraycopy(degree, 0, degreeKCore, 0, degree.length);

        while (budget > 0) {
            Arrays.fill(verMarked, true);
            Arrays.fill(verVisited, false);
            Arrays.fill(vertexTreeStatus, 'v'); 

            int countRemoveCore = createKCore_BZ(2);
            if (countRemoveCore > 0) {
                boolean isRooted = false;
                Arrays.fill(verMarked, false);
                // SeparateChainingHash<Integer, Integer> sth = new SeparateChainingHash<Integer, Integer>(countRemoveCore);
                sth = new SeparateChainingHash<Integer, Integer>(countRemoveCore);
                for (vertexIndex = 0; vertexIndex <= vertices.length-1; ++vertexIndex) {
                // vertexIndex = 1;
                    int distance = 0;
                    if (degreeKCore[vertices[vertexIndex]] == 0) {
                        isRooted = checkRootedTree(degreeKCore, verVisited, vertices[vertexIndex], verMarked, distance, kc3);
                        if (!isRooted && !verVisited[vertices[vertexIndex]])
                            vertexTreeStatus[vertices[vertexIndex]] = 'n';
                        // System.out.println("vertex["+vertexIndex+"]="+vertices[vertexIndex]+" isRooted: "+isRooted + " verMarked: " + verMarked[vertices[vertexIndex]] + " verVisited: " + verVisited[ver[vertexIndex]]);
                    }
                }

                /*for (int i = 0; i < vertexTreeStatus.length; ++i) 
                    System.out.println("i: "+i+" vertexTreeStatus: "+vertexTreeStatus[i]);
                for (int i = 0; i < countRemoveCore; ++i) {
                    for (Integer key : sth.st[i].keys()) {
                        System.out.println("i: " + i + ", " + key + ", " + sth.st[i].get(key)+", "+sth.st[i].size());
                    }
                }*/

                int rootedNumberVerticesSaved = 0, nonRootedNumberVerticesSaved = 0;
                Map<Integer,Integer> vertexMap = new HashMap<Integer,Integer>();
                List<Map.Entry<Integer,Integer>> anchorVertices = new ArrayList<Map.Entry<Integer,Integer>>(vertexMap.entrySet());
                anchorVertices = findFurthestLongestVertices(countRemoveCore, vertices);
                for (int l = 0; l < anchorVertices.size(); ++l) {
                    if (l < 2)
                        rootedNumberVerticesSaved += anchorVertices.get(l).getValue();
                    else
                        nonRootedNumberVerticesSaved += anchorVertices.get(l).getValue();
                    System.out.println("key: " + anchorVertices.get(l).getKey() + " value: " + anchorVertices.get(l).getValue());
                }

                /*Iterator<Map.Entry<Integer,Integer>> maxListIterator = maxList.iterator();
                while (maxListIterator.hasNext()) {
                    System.out.println(maxListIterator.next());
                }*/
                /*maxList = findLongestNonRootedTree(countRemoveCore, ver);
                for (int l = 0; l < maxList.size(); ++l)
                    nonRootedCost += maxList.get(l).getValue();*/
                // System.out.println("key: " + maxList.get(l).getKey() + " value: " + maxList.get(l).getValue());
            
                if (rootedNumberVerticesSaved > nonRootedNumberVerticesSaved || budget == 1) {
                    updateVertex(anchorVertices, countRemoveCore, 'r');
                    --budget;
                } else {
                    updateVertex(anchorVertices, countRemoveCore, 'n');
                    budget -= 2;
                }
                System.out.println("budget: "+budget);
            
                for(int i = 0; i < degree.length; ++i) {
                    System.out.println("degree[" + i + "] = " + degree[i] + " degreeKCore["+ i +"] = "+ degreeKCore[i]);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println ("Total time for processing "+(endTime - startTime) +" ms.");
        System.out.println( "finish" );
    }

    private static int createKCore_BZ(int k) {
        boolean verMarked[] = new boolean[vertices.length];
        Arrays.fill(verMarked, true);
        /*int pos[] = kc3.getPosition();
        int bin[] = kc3.getBin();*/
/*
        for(int i = 0; i< deg.length; ++i) {
            System.out.println("degree[" + i + "] = " + deg[i] + " vertex[" + i + "] = " + vertices[i] + " position[" + i + "] = " + pos[i] + " degreeKCore["+ i +"] = "+ degreeKCore[i] + " verMarked["+ i +"] = "+ verMarked[i]);
        }

        for(int j = 0; j <= ver.length-1; ++j) {
            System.out.println("vertices["+vertices[j]+"]"+" deg: "+deg[vertices[j]]);
            int[] neighbors = kc3.graph.getNeighbors(vertices[j]);
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
            while(/*!isIterate ||*/ (degreeKCore[vertices[v]] < k && degree[v] > 0 && v < vertices.length-1)) {
                if (verMarked[v]) {
                    // System.out.println("test: "+vertices[v]+" d:"+degreeKCore[vertices[v]]);
                    degreeKCore[vertices[v]] = 0;
                    ++countRemoveCore;
                    int[] neighbors = kc3.graph.getNeighbors(vertices[v]);
                    for(int i = 0; i <= neighbors.length-1; ++i) {
                        int vertex = neighbors[i];
                        // System.out.println("neighbors["+ i + "] = "+ neighbors[i]);
                        if (degreeKCore[vertex] > 0)
                            degreeKCore[vertex]--; 
                        if (degreeKCore[vertex] < k && degreeKCore[vertex] > 0)
                            isIterate = true;
                        // System.out.println("degreeKCore["+vertex+"]="+degreeKCore[vertex]);
                    }
                    verMarked[v] = false;
                }
                v++;
            }
            // System.out.println(isIterate + " v:"+v + " length:"+ver.length);
        } while(isIterate && v < vertices.length-1); 

        for(int i = 0; i < degree.length; ++i) {
            System.out.println("degree[" + i + "] = " + degree[i] + " vertices[" + i + "] = " + vertices[i] /*+ " position[" + i + "] = " + pos[i]*/ + " degreeKCore["+ i +"] = "+ degreeKCore[i] + " verMarked["+ i +"] = "+ verMarked[i] + "\n");
        }
        return countRemoveCore;
    }

    private static boolean checkRootedTree(int[] degKCore, boolean[] verVisited, int v, boolean[] verMarked, int distance, KCoreWG_BZ kc3) {
        boolean isRooted = false;

        // System.out.println("v: "+v+" neighbors(): "+degKCore[v]);
        if (degKCore[v] == 0 && !verMarked[v]) {
            verMarked[v] = true;
            int[] neighbors = kc3.graph.getNeighbors(v);
            // System.out.println("length: "+neighbors.length);            
            ++distance;
            for (int i = 0; i <= neighbors.length-1; ++i) {
                if (!verMarked[neighbors[i]] && !sth.st[vertexIndex].contains(neighbors[i])) {
                    sth.st[vertexIndex].put(neighbors[i], distance);
                    vertexTreeStatus[neighbors[i]] = 'n';
                    // System.out.println("checkRootedTree neighbors["+i+"]: "+neighbors[i]);
                }
                // System.out.println("vertexIndex: "+vertexIndex+" i: "+i+" neighbors("+ neighbors[i] +"): "+degKCore[neighbors[i]]+ " "+verMarked[neighbors[i]]+ " v " + v + " Marked " + verMarked[v]);
                if (degKCore[neighbors[i]] == 0) {
                    // System.out.println("neighbors[i]: "+neighbors[i]);
                    isRooted = checkRootedTree(degKCore, verVisited, neighbors[i], verMarked, distance, kc3); 
                }
                else {
                    isRooted = true;
                    distance = 0;
                    /*boolean verVisited[] = new boolean[verMarked.length];
                    Arrays.fill(verVisited, false);*/ // TODO: maybe we can use verMarked instead of verVisited. Check it out!
                    sth.st[vertexIndex].put(neighbors[i], 0);
                    vertexTreeStatus[neighbors[i]] = 'r';
                    computeDistance(degKCore, neighbors[i], verVisited, distance, kc3);
                }
                // System.out.println("i: "+ i+ " v: "+v);
            }
        }
        return isRooted;
    }

    private static int computeDistance(int[] degKCore, int v, boolean[] verVisited, int distance,KCoreWG_BZ kc3) {
        int[] neighbors = kc3.graph.getNeighbors(v);
        // System.out.println("distance: "+ distance+ " v: "+v + " length: "+neighbors.length);
        verVisited[v] = true;
        ++distance;
        for (int i = 0; i <= neighbors.length-1; ++i) {
            // if (sth.st[vertexIndex].contains(neighbors[i]))
            // System.out.println("contains "+sth.st[vertexIndex].contains(neighbors[i])+ " neighbors[i]: " + neighbors[i]);
            // System.out.println("vertexIndex: "+vertexIndex+" i: "+i+" neighbors("+ neighbors[i] +"): "+degKCore[neighbors[i]]+ " "+verVisited[neighbors[i]]+ " v " + v + " Marked " + verVisited[v]);
            if (degKCore[neighbors[i]] == 0 && !verVisited[neighbors[i]]) {
                verVisited[neighbors[i]] = true;
                // System.out.println("computeDistance neighbors["+i+"]: "+neighbors[i]+" distance: "+ distance);
                sth.st[vertexIndex].put(neighbors[i], distance);
                vertexTreeStatus[neighbors[i]] = 'r';
                computeDistance(degKCore, neighbors[i], verVisited, distance, kc3);
            }
        }
        // System.out.println("\n");
        return 0; 
    }

    private static List<Map.Entry<Integer,Integer>> findFurthestLongestVertices(int countRemoveCore, int[] vertex) {
        char isRootedTree = 'r', isNonRootedTree = 'n';
        int furthestValue1 = 0, furthestValue2 = 0;
        int furthestKey1 = 0, furthestKey2 = 0;
        int endPointValue1 = 0, endPointValue2 = 0;
        int endpointKey1 = 0, endpointKey2 = 0;
        
        for (int i = 0; i < countRemoveCore; ++i) {
            for (Integer key : sth.st[i].keys()) {
                if ((furthestValue1 == 0 || furthestValue1 < sth.st[i].get(key)) && (sth.st[i].get(key) != 0) && (isRootedTree == vertexTreeStatus[key])) {
                    furthestValue1 = sth.st[i].get(key);
                    furthestKey1 = key;
                }

                if ((furthestValue2 == 0 || furthestValue2 < sth.st[i].get(key)) && (sth.st[i].get(key) <= furthestValue1) && (furthestKey1 != key) && (sth.st[i].get(key) != 0) && (isRootedTree == vertexTreeStatus[key])) {
                    furthestValue2 = sth.st[i].get(key);
                    furthestKey2 = key;
                }

                if ((endPointValue2 == 0 || endPointValue2 < sth.st[i].get(key)) && (sth.st[i].get(key) != 0) && (isNonRootedTree == vertexTreeStatus[key])) {
                    endPointValue2 = sth.st[i].get(key);
                    endpointKey2 = key;

                    endpointKey1 = vertex[i];
                    endPointValue1 = 0;
                }
            }
        }

        /*Map<Integer,Integer> tmpMap = new HashMap<Integer,Integer>();
        tmpMap.put(endpointKey1, endPointValue1);
        tmpMap.put(furthestKey1, furthestValue1);
        tmpMap.put(endpointKey2, endPointValue2);
        tmpMap.put(furthestKey2, furthestValue2);
        List<Map.Entry<Integer,Integer>> result = new ArrayList<Map.Entry<Integer,Integer>>(tmpMap.entrySet());*/
        List<Map.Entry<Integer,Integer>> result = new ArrayList<Map.Entry<Integer,Integer>>();
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(furthestKey1, furthestValue1));
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(furthestKey2, furthestValue2));
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(endpointKey1, endPointValue1));
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(endpointKey2, endPointValue2));

        return result;
    }

    /*public static List<Map.Entry<Integer,Integer>> findLongestNonRootedTree(int countRemoveCore, int[] vertex) {
        char typeTree = 'n';
        int endPoint1 = 0;
        int endPoint2 = 0;
        int key1 = 0;
        int key2 = 0;

        for (int i = 0; i < countRemoveCore; ++i) {
            for (Integer key : sth.st[i].keys()) {
                if ((endPoint2 == 0 || endPoint2 < sth.st[i].get(key)) && (sth.st[i].get(key) != 0) && (typeTree == vertexTreeStatus[key])) {
                    endPoint2 = sth.st[i].get(key);
                    key2 = key;

                    key1 = vertex[i];
                    endPoint1 = 0;
                }
            }
        }

        Map<Integer,Integer> tmpMap = new HashMap<Integer,Integer>();
        tmpMap.put(key1, endPoint1);
        tmpMap.put(key2, endPoint2);
        List<Map.Entry<Integer,Integer>> result = new ArrayList<Map.Entry<Integer,Integer>>(tmpMap.entrySet());

        return result;
    }*/

    private static void updateVertex(List<Map.Entry<Integer,Integer>> anchorVertices, int countRemoveCore, char typeTree) {
        int index = 0;
        outerloop:
        for (; index < countRemoveCore; ++index) {
            for (Integer key : sth.st[index].keys()) {
                if (typeTree == 'r' && key == anchorVertices.get(0).getKey())
                    break outerloop;
                if (typeTree == 'n' && key == anchorVertices.get(3).getKey())
                    break outerloop;
            }
        }

        if (index < countRemoveCore) {
            for (Integer key : sth.st[index].keys())
                degreeKCore[key] += 100;
            if (typeTree == 'n')
                degreeKCore[anchorVertices.get(2).getKey()] += 100;
        }
    }
}

