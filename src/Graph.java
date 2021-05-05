import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.Gson;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Graph {

    private static final String API_KEY = "AIzaSyDbGKLgDehhTw5e74VYe3jvACTBS9GdrVI";

    // Maps vertex indices to Cafe objects
    private Map<Integer, Cafe> vertexMap;

    // Adjacency matrix representation
    private int[][] adjm;

    private Coordinate loc1;
    private Coordinate loc2;

    public Graph(Coordinate loc1, Coordinate loc2) {
        this.loc1 = loc1;
        this.loc2 = loc2;

        // Compute distance and midpoint between loc1 and loc2
        int distance = getDistance(loc1, loc2);
        Coordinate mid = Coordinate.mid(loc1, loc2);

        // Get nearby cafes
        int radius = distance / 2;
        this.vertexMap = getCafes(mid, radius);
         
        // Get distances between cafes and create adjacency matrix 
        this.adjm = new int[vertexMap.size() + 2][vertexMap.size() + 2];
        if (vertexMap.size() > 0) {
            for (int i = 0; i < adjm.length; i++) {
                for (int j = 0; j < adjm[0].length; j++) {
                    if (i == j) {
                        // no weight to same vertex
                        adjm[i][j] = 0;
                    } else if (adjm[i][j] == 0 && adjm[j][i] == 0) {

                        Coordinate locI;
                        Coordinate locJ;

                        if (i == 0) {
                            locI = loc1;
                        } else if (i == 1) {
                            locI = loc2;
                        } else {
                            Cafe cafeI = vertexMap.get(i);
                            locI = new Coordinate(cafeI.getGeometry().getLocation().getLat(), cafeI.getGeometry().getLocation().getLng());
                        }

                        if (j == 0) {
                            locJ = loc1;
                        } else if (j == 1) {
                            locJ = loc2;
                        } else {
                            Cafe cafeJ = vertexMap.get(j);
                            locJ = new Coordinate(cafeJ.getGeometry().getLocation().getLat(), cafeJ.getGeometry().getLocation().getLng());
                        }

                        // distance between cafes
                        int distanceIJ = getDistance(locI, locJ);
                        adjm[i][j] = distanceIJ;
                        adjm[j][i] = distanceIJ;
                    }
                }
            }

            // Filter
            for (int i = 0; i < adjm.length; i++) {
                for (int j = 0; j < adjm[0].length; j++) {
                    int distanceIJ = adjm[i][j];
                    if (distanceIJ >= radius) {
                        adjm[i][j] = 0;
                    }
                }
            }

            // print for debugging
            for (int i = 0; i < adjm.length; i++) {
                String row = "";
                for (int j = 0; j < adjm[0].length; j++) {
                    row += adjm[i][j] + ",";
                }
                row = row.substring(0, row.length() - 1);
                System.out.println(row);
            }
        }
    }

    /**
     * Gets the distance between two locations based on Google Maps API
     * @param loc1 the input location 1
     * @param loc2 the input location 2
     * @return distance between input locations, -1 if API call failed
     */
    private int getDistance(Coordinate loc1, Coordinate loc2) {
        String apiCall = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
            "&origins=" + loc1.getLat() + "," + loc1.getLng() +
            "&destinations=" + loc2.getLat() + "," + loc2.getLng() +
            "&mode=driving" +
            "&key=" + API_KEY;

        try {
            URL url = new URL(apiCall);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            if (httpConnection.getResponseCode() == 200) {
                // Build json response string
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();

                // Map json to Java object
                Gson gson = new Gson();
                MatrixResponse response = gson.fromJson(sb.toString(), MatrixResponse.class);

                int[][] res = response.getData();
                return res[0][0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Calls Google Maps API to get cafes within a radius of a given location
     * @param loc the input location 
     * @param radius the input radius
     */
    private Map<Integer, Cafe> getCafes(Coordinate loc, int radius) {
        Map<Integer, Cafe> res = new HashMap<>();

        try {
            // Cafes API call
            URL url = new URL(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + 
                loc.getLat() + "," + loc.getLng() + 
                "&radius=" + radius + 
                "&types=cafe&key=" + API_KEY);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            
            if (httpConnection.getResponseCode() == 200) {
                // Build json response string
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();

                // Map json to Java object
                Gson gson = new Gson();
                CafeResponse response = gson.fromJson(sb.toString(), CafeResponse.class);
                List<Cafe> cafes = response.getResults();

                // Map vertex indices to cafes
                int offset = 2; // user input of 2 locations
                for (int i = 0; i < cafes.size(); i++) {
                    res.put(i + offset, cafes.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    } 

     /**
     * Dijkstra's algorithm
     * @param s the input source vertex
     * @return the distance and parent pointer arrays
     */
    private Object[] dijkstra(int s) {
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
    private int getMedianVertex(int s, int t, int[] dist, int[] parent) {
        
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

    /**
     * 
     * @return Gets the nearest cafe to both input locations
     */
    public Cafe getNearestCafe() {  
        Object[] res = dijkstra(0);
        int[] dist = (int[]) res[0];
        int[] parent = (int[]) res[1];
        int med = getMedianVertex(0, 1, dist, parent);
        return vertexMap.get(med);
    }

}
