/**
 *
 * This is an implementation of the algorithm 2.1. given in: 
 * Kshipra Bhawalkar, Jon Kleinberg, Kevin Lewi, Tim Roughgarden, and Aneesh Sharma
 * Preventing Unraveling in Social Networks: The Anchored k-Core Problem
 * Algorithm 2.1. An efficient, exact algorithm for anchored 2-core
 * 
 * The graph is stored using Webgraph (see P. Boldi and S. Vigna. The webgraph framework I: compression techniques. WWW 04.)
 *
 * @author Babak Tootoonchi, babakt@uvic.ca, 2015
 */

package ca.uvic.css.unrevealing;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.NodeIterator;

import ca.uvic.css.util.Queue;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import ca.uvic.css.graph.*;
import ca.uvic.css.kcore.KCoreWG_BZ;
import ca.uvic.css.util.*;

public class Anchored {
	private static Anchored uniqueInstance;
    private static SeparateChainingHash<Integer, Integer> sth;
    private static int vertexIndex; 
    private static KCoreWG_BZ kc3;
    private static int maxDegree; 
    private static int degree[];
    private static int vertices[];
    private static int degreeKCore[];
    private static int bin[];

    private static char vertexTreeStatus[];
	private static int removeCoreVertices[];
	private static Queue<Integer> q_vertices = new Queue<Integer>();
    private static int keyIndex[];
	
	private Anchored() {}

	public static Anchored getInstance() {
		if (uniqueInstance == null) 
			uniqueInstance = new Anchored();
		return uniqueInstance;
	}

	private static void getKCoreWGBZInstance(String path_input) {
		if (kc3 == null) {
			try {
                kc3 = new KCoreWG_BZ(path_input, "webgraph");
			} catch(Exception e){
        		System.out.println("Cannot create an instance of KCoreWG_BZ!");
    		}
    	}
	}

    public static void findAnchors(String path_input, String path_output, int budget) throws  ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, Exception, IOException {
        long startTime = System.currentTimeMillis();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path_output)));

        kc3 = new KCoreWG_BZ(path_input, "webgraph");
        kc3.KCoreCompute();

        maxDegree = kc3.getMaxDegree();
        degree = kc3.getDegree();
        vertices = kc3.getVertex();

        degreeKCore = new int[degree.length];
        vertexTreeStatus = new char[vertices.length];
		removeCoreVertices = new int[vertices.length];
        boolean verMarked[] = new boolean[vertices.length];
        boolean verVisited[] = new boolean[verMarked.length];
        System.arraycopy(degree, 0, degreeKCore, 0, degree.length);

		out.write("\nvertices.length : " + vertices.length);
        long endTimeb = System.currentTimeMillis();
        out.write("\nbudget " + budget);
        out.write("\nbudget time for processing " + (endTimeb - startTime) + " ms.");
        Arrays.fill(removeCoreVertices, 0);
        int countRemoveCore = createKCore_BZ(2);

        while (budget > 0) {
            Arrays.fill(verMarked, true);
            Arrays.fill(verVisited, false);
            Arrays.fill(vertexTreeStatus, 'v'); 
            
            out.write("\ncountRemoveCore " + countRemoveCore);
            out.write("\n2-core " + (vertices.length - countRemoveCore));
            out.write("\nbudget " + budget);

            if (countRemoveCore > 0) {
                boolean isRooted = false;
                Arrays.fill(verMarked, false);

                sth = new SeparateChainingHash<Integer, Integer>(countRemoveCore+1);
				
				for(int rvIndex = 0; rvIndex <= countRemoveCore; ++rvIndex) {
					vertexIndex  = removeCoreVertices[rvIndex];

					int distance = 0;
                    if (degreeKCore[vertexIndex] == 0) {
                        isRooted = checkRootedTree(rvIndex, degreeKCore, verVisited, vertexIndex, verMarked, distance);
                        if (!isRooted && !verVisited[vertexIndex])
                            vertexTreeStatus[vertexIndex] = 'n';
                    }
				}

                int rootedNumberVerticesSaved = 0, nonRootedNumberVerticesSaved = 0;
                Map<Integer,Integer> vertexMap = new HashMap<Integer,Integer>();
                List<Map.Entry<Integer,Integer>> anchorVertices = new ArrayList<Map.Entry<Integer,Integer>>(vertexMap.entrySet());
                anchorVertices = findFurthestLongestVertices(countRemoveCore, vertices);

                for (int l = 0; l < anchorVertices.size() - 1; ++l) {
                    if (l < 2)
                        rootedNumberVerticesSaved += anchorVertices.get(l).getValue();
                    else
                        nonRootedNumberVerticesSaved += (anchorVertices.get(l).getValue() + 1);
                    System.out.println("key: " + anchorVertices.get(l).getKey() + " value: " + anchorVertices.get(l).getValue());
                    out.write("\nkey: " + anchorVertices.get(l).getKey() + " value: " + anchorVertices.get(l).getValue());
                }

                if (rootedNumberVerticesSaved > nonRootedNumberVerticesSaved || budget == 1) {
                    updateVertex(anchorVertices, countRemoveCore, 'r');
                    --budget;
                } else {
                    updateVertex(anchorVertices, countRemoveCore, 'n');
                    budget -= 2;
                }

                kc3.KCoreReIntial(degree);
                kc3.KCoreCompute();
                degree = kc3.getDegree();
                vertices = kc3.getVertex();
                System.arraycopy(degree, 0, degreeKCore, 0, degree.length);
                Arrays.fill(removeCoreVertices, 0);
                countRemoveCore = createKCore_BZ(2);
            }
            else
                break;
        }

        long endTime = System.currentTimeMillis();
        System.out.println ("Total time for processing " + (endTime - startTime) + " ms.");
        out.write("\nTotal time for processing " + (endTime - startTime) + " ms.");
        out.close();
        System.out.println( "finish" );
    }

    private static int createKCore_BZ(int k) throws Exception, IOException {
        bin = kc3.getBin();
        int rvIndex = 0;
        int index_k_core = bin[k];

        int countRemoveCore = index_k_core;
        for (int v = 0; v < index_k_core; v++) {
                removeCoreVertices[rvIndex] = vertices[v];
                degreeKCore[vertices[v]] = 0;
                rvIndex++;
        }
        return countRemoveCore;
    }

    private static boolean checkRootedTree(int rvIndex, int[] degKCore, boolean[] verVisited, int v, boolean[] verMarked, int distance) throws Exception, IOException {
        boolean isRooted = false;

        if (degKCore[v] == 0 && !verMarked[v]) {
            if (!sth.st[rvIndex].contains(v)) {
                sth.st[rvIndex].put(v, distance);
            }
            verMarked[v] = true;
            int[] neighbors = kc3.graph.getNeighbors(v);
            ++distance;
            for (int i = 0; i <= neighbors.length-1; ++i) {
				if (neighbors[i] > vertices.length-1) {
					continue;
				}
                if (!verMarked[neighbors[i]]) {
					if (!sth.st[rvIndex].contains(neighbors[i])) {
						sth.st[rvIndex].put(neighbors[i], distance);
                   	 	vertexTreeStatus[neighbors[i]] = 'n';
					}
                }

                if (degKCore[neighbors[i]] == 0) {
                    isRooted = checkRootedTree(rvIndex, degKCore, verVisited, neighbors[i], verMarked, distance); 
                }
                else {
                    isRooted = true;
                    distance = 0;
					sth.st[rvIndex].put(neighbors[i], 0);
                    vertexTreeStatus[neighbors[i]] = 'r';
                    computeDistance(rvIndex, degKCore, neighbors[i], verVisited, distance);
                    break;
                }
            }
        }

        return isRooted;
    }

    private static void computeDistance(int rvIndex, int[] degKCore, int v, boolean[] verVisited, int distance) {
        int[] neighbors = kc3.graph.getNeighbors(v);

        verVisited[v] = true;
        ++distance;
        for (int i = 0; i <= neighbors.length-1; ++i) {
            if (degKCore[neighbors[i]] == 0 && !verVisited[neighbors[i]]) {                
                verVisited[neighbors[i]] = true;
                sth.st[rvIndex].put(neighbors[i], distance);
                vertexTreeStatus[neighbors[i]] = 'r';
                computeDistance(rvIndex, degKCore, neighbors[i], verVisited, distance);
            }
        }
    }

    private static List<Map.Entry<Integer,Integer>> findFurthestLongestVertices(int countRemoveCore, int[] vertex) {
        char isRootedTree = 'r', isNonRootedTree = 'n';
        int furthestValue1 = 0, furthestValue2 = 0;
        int furthestKey1 = 0, furthestKey2 = 0;
        int endPointValue1 = 0, endPointValue2 = 0;
        int endpointKey1 = 0, endpointKey2 = 0;
        int j = 0;
        keyIndex = new int[4];
        
        for (int i = 0; i < countRemoveCore; ++i) {
            for (Integer key : sth.st[i].keys()) {
                if ((furthestValue1 == 0 || furthestValue1 < sth.st[i].get(key)) && (sth.st[i].get(key) != 0) && (isRootedTree == vertexTreeStatus[key])) {
                    furthestValue1 = sth.st[i].get(key);
                    furthestKey1 = key;
                    keyIndex[0] = i;
                }
            }
        }

        for (int i = 0; i < countRemoveCore; ++i) {
            for (Integer key : sth.st[i].keys()) {
                if ((furthestValue2 == 0 || furthestValue2 < sth.st[i].get(key)) && (sth.st[i].get(key) <= furthestValue1) && (furthestKey1 != key) && (sth.st[i].get(key) != 0) && (isRootedTree == vertexTreeStatus[key])) {
                    furthestValue2 = sth.st[i].get(key);
                    furthestKey2 = key;
                    keyIndex[1] = i;
                }

                if ((endPointValue2 == 0 || endPointValue2 < sth.st[i].get(key)) && (sth.st[i].get(key) != 0) && (isNonRootedTree == vertexTreeStatus[key])) {
                    endPointValue2 = sth.st[i].get(key);
                    endpointKey2 = key;
                    j = i;
                    keyIndex[2] = i;

                    endpointKey1 = vertex[i];
                    endPointValue1 = endPointValue2;
                }
            }
        }
        
        endPointValue1 = endPointValue2;
        
        List<Map.Entry<Integer,Integer>> result = new ArrayList<Map.Entry<Integer,Integer>>();
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(furthestKey1, furthestValue1));
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(furthestKey2, furthestValue2));
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(endpointKey1, endPointValue1));
        result.add(new AbstractMap.SimpleEntry<Integer, Integer>(endpointKey2, endPointValue2));

        return result;
    }

    private static void updateVertex(List<Map.Entry<Integer,Integer>> anchorVertices, int countRemoveCore, char typeTree) {
        if (typeTree == 'r') {
            degree[anchorVertices.get(0).getKey()] = (maxDegree);
        }
        else if (typeTree == 'n') {
            degree[anchorVertices.get(2).getKey()] = (maxDegree);
            degree[anchorVertices.get(3).getKey()] = (maxDegree);
        }
    }
}
