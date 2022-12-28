import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class find3SAT {
	static ArrayList<Integer> c;
	static int K;

	static void printGraph(int[][] mat) {
		for (int i = 0; i < mat[0].length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				System.out.print(mat[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	static int[][] copyMat(int[][] mat) {
		int[][] newMat = new int[mat[0].length][mat[0].length];
		for (int i = 0; i < mat[0].length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				newMat[i][j] = mat[i][j];
			}
		}
		return newMat;
	}
	
	static int edgeCount(int[][] mat) {
		int count = 0;
		for (int i = 0; i < mat[0].length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (mat[i][j] == 1) {
					count++;
				}
			}
		}
		return count;
	}
	
	static int[][] removeEdges(int[][] mat, int v) {
		int[][] newMat = new int[mat[0].length][mat[0].length];
		for (int i = 0; i < mat[0].length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (i == v || j == v) {
					newMat[i][j] = 0;
				} else {
					newMat[i][j] = mat[i][j];
				}
			}
		}
		return newMat;
	}
	
	static Boolean coverValidation(int[][] mat, ArrayList<Integer> cover) {
		for (int i = 0; i < cover.size(); i++) {
			mat = removeEdges(mat, cover.get(i));
		}
		if (edgeCount(mat) == 0) {
			System.out.println("Cover validation success");
			return true;
		}
		System.out.println("Cover validation failed");
		return false;
	}
	
	static public void vertexCover(int[][] mat, boolean[] visit, int currentVertex, int k, int edges) {
		int coversize = 0;
//		printGraph(mat);
		for (int c = 0; c < visit.length; c++) {
			if (visit[c]) {
				coversize++;
			}
		}
		if (edges <= 0) {
			if (k >= 0) {
				if (c.size() != 0 && c.size() > coversize) {
					return;
				}
				c.clear();
				for (int i = 0; i < mat[0].length; i++) {
					if (visit[i]) {
						c.add(i);
					}
				}
				return;
			}
		}
		if (k <= 0) {
			return;
		}
		ArrayList<Integer> currentVertexEdges = new ArrayList<Integer>();
		int count = 0;
		int vertex = -1;
		for (int i = 0; i < mat[0].length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (mat[i][j] == 1 && !visit[i]) {
					currentVertexEdges.add(j);
				}
			}
			if (currentVertexEdges.size() > 0) {
				vertex = i;
				break;
			}
		}

		if (c.size() > 0) {
			return;
		}
		if (currentVertexEdges.size() > 0) {
			visit[vertex] = true;
			int[][] newGraph = removeEdges(mat, vertex);
			int newGraphEdgeCount = edgeCount(newGraph);
			vertexCover(newGraph, visit, vertex, k - 1, newGraphEdgeCount);
			if (c.size() > 0) {
				return;
			}
			visit[vertex] = false;
			int[][] newGraph2 = copyMat(mat);
			ArrayList<Integer> x = new ArrayList<Integer>();
			for (int j = 0; j < currentVertexEdges.size(); j++) {
				if (!visit[currentVertexEdges.get(j)]) {
					visit[currentVertexEdges.get(j)] = true;
					newGraph2 = removeEdges(newGraph2, currentVertexEdges.get(j));
					x.add(currentVertexEdges.get(j));
				}
			}
			int newGraphEdgeCount2 = edgeCount(newGraph2);
			vertexCover(newGraph2, visit, vertex, k - x.size(), newGraphEdgeCount2);
			for (int p = 0; p < x.size(); p++) {
				visit[x.get(p)] = false;
			}
		}
		
		return;
	}
	
	public static List<String> readFile(String fileName) {
		List<String> lines = Collections.emptyList();
		try {
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static void main(String[] args) throws IOException {
		String fileName = args[0];
        File file = new File(fileName);
        List<String> data = readFile(fileName);

        int current = 0;
        Iterator<String> it = data.iterator();
        while (it.hasNext()) {
        	current++;
        	String row = it.next();
        	String[] rowVals = row.split(" ");
        	ArrayList<Integer> vars = new ArrayList<Integer>();
        	for (int i = 0; i < rowVals.length; i++) {
        		int t = Integer.parseInt(rowVals[i]);
        		if (!vars.contains(t)) { 
        			vars.add(t);
        			vars.add(-1 * t);
        		}
        	}
        	int n = vars.size() / 2;
        	int k = rowVals.length / 3;
        	int V = (2 * n) + (3 * k);
        	K = n + (2 * k);
        	for (int i = 0; i < rowVals.length; i++) {
        		vars.add(Integer.parseInt(rowVals[i]));
        	}
        	ArrayList<Integer> vertices = new ArrayList<Integer>();
        	int edges = 0;
            int[][] mat = new int[V][V];
            for (Integer i = 0; i < V; i++) {
            	vertices.add(i);
            	for (int j = 0; j < V; j++) {
            		if (i < n * 2 && j < n * 2) {
            			if (vars.get(j) == -1 * vars.get(i)) {
            				mat[i][j] = 1;
            			}
            		} else {
            			int c = (j - (n * 2)) % 3;
        				mat[vars.indexOf(vars.get(j))][j] = 1;
        				mat[j][vars.indexOf(vars.get(j))] = 1;
            			if (c == 1) {
            				mat[j][j-1] = 1;
            				mat[j-1][j] = 1;
            			}
            			if (c == 2) {
            				mat[j][j-2] = 1;
            				mat[j-2][j] = 1;
            				mat[j][j-1] = 1;
            				mat[j-1][j] = 1;
            			}
            		}
            	}
            }
            for (int i = 0; i < V; i++) {
//            	System.out.println();
            	for (int j = 0; j < V; j++) {
            		if (i == j) {
            			mat[i][j] = 0;
            		}
//            		System.out.print(mat[i][j] + " ");
            		if (mat[i][j] == 1) {
            			edges++;
            		}
            	}
//            	System.out.println();
            }
//            System.out.println("n " + n + " k " + k + " V " + V + " K " + K + " E " + edges);
            int low = 0;
            int high = V;
            int mid = -1;
            ArrayList<Integer> cover = new ArrayList<Integer>(vertices);
            c = new ArrayList<Integer>();
            int itr = 0;
            Instant startedAt = Instant.now();
            while (low != high && itr < 10) {
            	itr++;
            	boolean[] visit = new boolean[V];
            	mid = (low + high) / 2;
            	
            	c.clear();
            	boolean found = false;
            	vertexCover(mat, visit, 0, K, edges);
            	if (c.size() != 0) {
            		found = true;
            		high = mid;
            	} else {
            		low = mid;
            	}
            	
            	if (found) {
            		cover.clear();
            		for (int i = 0; i < c.size(); i++) {
            			cover.add(c.get(i));
            		}
            	}
            }
            c.clear();
            boolean[] visit = new boolean[V];
//            K = cover.size() - 1;
            vertexCover(mat, visit, 0, cover.size() - 1, edges);
            if (c.size() != 0) {
            	cover.clear();
        		for (int i = 0; i < c.size(); i++) {
        			cover.add(c.get(i));
        		}
            }
//            coverValidation(mat, cover);
            Instant endedAt = Instant.now();
//            System.out.println(vars);
            Map<Integer, String> solution = new HashMap<Integer, String>();
            for (int i = 0; i < 2 * n; i++) {
            	if (cover.contains(i)) {
//            		System.out.println(vars.get(i));
            		solution.putIfAbsent(vars.get(i), "T");
            	} else {
            		solution.putIfAbsent(vars.get(i), "F");
            	}
            }
            long timeTaken = Duration.between(startedAt,endedAt).toMillis();
            
            System.out.print("3CNF No." + current + ":[n=" + n + " k=" + k + "]->");
            System.out.print("[V=" + V + ", E=" + edges / 2 + ", k=" + k + "(" + timeTaken + " ms) ");
            System.out.print("Soulution: [");
            for (Integer key : solution.keySet()) {
            	if (key>0) {
            		System.out.print(key+":"+solution.get(key));
            	}
            }
            System.out.println("]");
            for (int i = 0; i < k; i++) {
            	System.out.print("(");
            	for (int j = 0; j < 3; j++) {
            		if (j != 2) {
            			System.out.print(rowVals[(i * 3) + j] + "|");
            		} else {
            			System.out.print(rowVals[(i * 3) + j]);
            		}
            	}
            	System.out.print(")");
            	if (i != k - 1) {
            		System.out.print("∧");
            	} else {
            		System.out.println(" ==>");
            	}
            }
            for (int i = 0; i < k; i++) {
            	System.out.print("(");
            	for (Integer j = 0; j < 3; j++) {
            		Integer rowval = Integer.parseInt(rowVals[(i * 3) + j]);
            		Integer idx = vars.indexOf(rowval); 
            		if (j != 2) {
            			System.out.print(solution.get(vars.get(idx)) + "|");
            		} else {
            			System.out.print(solution.get(vars.get(idx)));
            		}
            	}
            	System.out.print(")");
            	if (i != k - 1) {
            		System.out.print("∧");
            	} else {
            		System.out.println();
            	}
            }
            System.out.println();
        }

	}

}
