import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Algorithms Class
 */
public class Algorithms {
    
    /**
     * Dijkstra's algorithm
     * @param adjm the input adjacency matrix
     * @param s the input source vertex
     * @return the distance and parent pointer arrays
     */
    public static Object[] dijkstra(int[][] adjm, int s) {
        // Create distance and parent pointer arrays
        int[] dist = new int[adjm.length];
        int[] parent = new int[adjm.length];

        // Create a priority queue of vertices keyed on their distance values
        Comparator<Integer> vertexDistComparator = (v1, v2) -> {
            return dist[v1] - dist[v2];
        };
        PriorityQueue<Integer> q = new PriorityQueue<>(vertexDistComparator);

        // Initialize data structures
        for (int i = 0; i < adjm.length; i++) {
            dist[i] = (i == s) ? 0 : Integer.MAX_VALUE;
            parent[i] = -1;
            q.offer(i);
        }

        while (!q.isEmpty()) {
            // Extract min distance value vertex
            int u = q.poll();
            for (int v = 0; v < adjm.length; v++) {

                // Edge relaxation
                if (adjm[u][v] != 0 && dist[v] > dist[u] + adjm[u][v]) {
                    dist[v] = dist[u] + adjm[u][v];
                    parent[v] = u;
                    // Reinsert vertex into priority queue (Java's implementation does not dynamically update order)
                    q.remove(v);
                    q.offer(v);
                }

            }
        }
        return new Object[] { dist, parent };
    }

    /**
     * Gets the median vertex on the shortest path from s to t based on edge weights
     * @param s the source vertex
     * @param t the target vertex
     * @param dist the distance array
     * @param parent the parent pointers array
     * @return the index of the median vertex
     */
    public static int getMedianVertex(int s, int t, int[] dist, int[] parent) {
        
        // Calculate middle distance value
        double midDist = dist[t] / 2.0;

        double minDiff = Double.MAX_VALUE;
        int med = -1;
        
        // Iterate through shortest path from s to t
        int curr = t;
        while (curr != s) {
            // Find the vertex with distance from s closest to the middle distance value
            double diff = Math.abs(dist[curr] - midDist);
            if (diff < minDiff) {
                minDiff = diff;
                med = curr;
            }
            curr = parent[curr];
        }
        
        return med;
    }

}
