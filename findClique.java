import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class findClique {
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
	
	static public void vertexCover(int[][] mat, boolean[] visit, int currentVertex, int k, int edges) {
		int coversize = 0;
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
//					
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
        	int size = Integer.parseInt(it.next());
        	ArrayList<Integer> vertices = new ArrayList<Integer>();
        	int edges = 0;
            int[][] mat = new int[size][size];
            for (Integer k = 0; k < size; k++) {
            	String str = it.next();
            	String[] line = str.split(" ");
            	vertices.add(k);
            	for (int j = 0; j < line.length; j++) {
            		Integer e = Integer.parseInt(line[j]);
            		if (k == j)
            			mat[k][j] = 0;
                    if (e == 0) {
                    	mat[k][j] = 1;
                    } else if (e == 1) {
                    	edges++;
                    }
            	}
            }
//            if (x != 100) {
//            	continue;
//            }
            int low = 0;
            int high = size;
            int mid = -1;
            ArrayList<Integer> cover = new ArrayList<Integer>(vertices);
            c = new ArrayList<Integer>();
            int itr = 0;
            Instant startTime = Instant.now();
            while (low != high && itr < 10) {
            	itr++;
            	boolean[] visit = new boolean[size];
            	mid = (low + high) / 2;
            	K = mid;
            	
            	c.clear();
            	boolean found = false;
            	vertexCover(mat, visit, 0, mid, edges);
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
            boolean[] visit = new boolean[size];
            K = cover.size() - 1;
            vertexCover(mat, visit, 0, cover.size() - 1, edges);
            if (c.size() != 0) {
            	cover.clear();
        		for (int i = 0; i < c.size(); i++) {
        			cover.add(c.get(i));
        		}
            }
            
            ArrayList<Integer> clique = new ArrayList<Integer>();
            for (int i = 0; i < vertices.size(); i++) {
            	if (!cover.contains(vertices.get(i))) {
            		clique.add(i);
            	}
            }
            Instant endTime = Instant.now();
            System.out.println();
			System.out.print("G" + current + " " + "(" + size + ", " + edges + ")");
			System.out.print(" (size" + clique.size() + " ms=" + Duration.between(startTime,endTime).toMillis() + ") ");
			System.out.print("{");
			for (int i = 0; i < clique.size() - 1; i++) {
				System.out.print(clique.get(i) + ",");
			}
			System.out.print(clique.get(clique.size() - 1));
			System.out.print("}");
            System.out.println();
        }

	}
}
