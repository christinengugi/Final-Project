import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Graph representation of network of nearby cafes
 */
public class Graph {

    // Google Maps API key
    private String key;

    // Maps vertex indices to Cafe objects
    private Map<Integer, Cafe> vertexMap;

    // Adjacency matrix representation
    private int[][] adjm;

    // Number of input user locations 
    private int numLocs;

    // User locations
    private Coordinate loc1;
    private Coordinate loc2;

    // Gson object, converts a json string to a useable Java object
    private Gson gson;

    /**
     * Constructor with user locations as string addresses 
     * @param addr1 the first user location as an address
     * @param addr2 the second user location as an address 
     * @param key the Google Maps API key
     */
    public Graph(String addr1, String addr2, String key) {
        // Call the constructor with user locations as coordinates
        this(getCoordinates(addr1, key), getCoordinates(addr2, key), key);
    }

    /**
     * Constructor with user locations as Coordinate objects
     * @param loc1 the first user location as a Coordinate
     * @param loc2 the second user location as a Coordinate
     * @param key the Google Maps API key
     */
    public Graph(Coordinate loc1, Coordinate loc2, String key) {
        // Initialize instance variables
        this.key = key;
        this.numLocs = 2;
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.gson = new Gson();

        // Compute distance and midpoint between loc1 and loc2
        int distance = getDistance(loc1, loc2);
        Coordinate mid = Coordinate.mid(loc1, loc2);

        // Get nearby cafes
        int radius = distance / 2;
        this.vertexMap = new HashMap<>();
        getCafes(mid, radius);
         
        // Create adjacency matrix representation
        this.adjm = new int[vertexMap.size() + numLocs][vertexMap.size() + numLocs];
        if (vertexMap.size() > 0) {
            // Populate adjacency matrix with weights between vertices
            getDistances();
            // Remove edges with weights greater than radius
            filterWeights(radius);
        }
    }

    /**
     * Filters edge weights of adjacency matrix
     * This method prevents a trivial complete graph
     * @param maxWeight the maximum allowed edge weight
     */
    private void filterWeights(int maxWeight) {
        for (int i = 0; i < adjm.length; i++) {
            for (int j = 0; j < adjm[0].length; j++) {

                if (adjm[i][j] >= maxWeight) {
                    adjm[i][j] = 0;
                }
            }
        }
    }

    /**
     * Gets response as json from an API call url
     * @param apiCall the input API call url
     * @return the json response as a string
     */
    private static String getJsonResponse(String apiCall) {
        try {
            // Open a HTTP connection to the API call url
            URL url = new URL(apiCall);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            // If the request has succeeded 
            if (httpConnection.getResponseCode() == 200) {

                // Read in the input stream
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ( (line = br.readLine()) != null ) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return "ERROR: Could not get Json response";
    }

    /**
     * Gets coordinates from a string address
     * @param addr the input string address
     * @return the converted Coordinate object
     */
    public static Coordinate getCoordinates(String addr, String key) {
        // Construct Geocode API call URL    
        String apiCall = "https://maps.googleapis.com/maps/api/geocode/json?" + 
            "address=" + addr.replaceAll(" ", "+") + "&key=" + key;

        // Get response in json and map to GeocodeResponse class
        String json = getJsonResponse(apiCall);
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(json, GeocodeResponse.class);

        return response.getLocationData()[0];
    }

    /**
     * Gets address from a Coordinate object
     * @param loc the input Coordinate object
     * @return the converted formatted address as a string
     */
    public static String getAddress(Coordinate loc, String key) {
        // Construct Reverse Geocode API call url    
        String apiCall = "https://maps.googleapis.com/maps/api/geocode/json?" + 
            "latlng=" + loc.getLat() + "," + loc.getLng() + "&key=" + key;

        // Get response in json and map to GeocodeResponse class
        String json = getJsonResponse(apiCall);
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(json, GeocodeResponse.class);

        return response.getAddressData()[0];
    }

    /**
     * Gets the distance between two locations based on Google Maps API
     * @param loc1 the input location 1
     * @param loc2 the input location 2
     * @return distance between input locations, -1 if API call failed
     */
    private int getDistance(Coordinate loc1, Coordinate loc2) {
        // Construct Distance Matrix API call url
        String apiCall = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
            "&origins=" + loc1.getLat() + "," + loc1.getLng() +
            "&destinations=" + loc2.getLat() + "," + loc2.getLng() +
            "&key=" + key;

        // Get response in json and map to MatrixResponse class
        String json = getJsonResponse(apiCall);
        MatrixResponse response = gson.fromJson(json, MatrixResponse.class);

        return response.getData()[0][0];
    }

    /**
     * Populates the adjacency matrix with edge weights (distances) between every vertex
     * Note this is an undirected graph and adjm[v][v] = 0 for all v == v
     */
    private void getDistances() {

        for (int i = 0; i < adjm.length; i++) {
            for (int j = i + 1; j < adjm[i].length; j++) {
                // If an entry is not yet filled
                if (i != j && adjm[i][j] == 0 && adjm[j][i] == 0) {

                    Coordinate locI;
                    Coordinate locJ;

                    // 0 is first input user location, 1 is second input user location
                    if (i <= 1) {
                        locI = (i == 0) ? loc1 : loc2;
                    } else {
                        Cafe cafe = vertexMap.get(i);
                        Cafe.Location cafeLoc = cafe.getGeometry().getLocation();
                        locI = new Coordinate(cafeLoc.getLat(), cafeLoc.getLng());
                    }

                    if (j == 1) {
                        locJ = loc2;
                    } else {
                        Cafe cafe = vertexMap.get(j);
                        Cafe.Location cafeLoc = cafe.getGeometry().getLocation();
                        locJ = new Coordinate(cafeLoc.getLat(), cafeLoc.getLng());
                    }

                    // Get distance from calling Google Maps API
                    int distanceIJ = getDistance(locI, locJ);
                    adjm[i][j] = distanceIJ;
                    adjm[j][i] = distanceIJ;
                }
            }
        }
    }
    
    /**
     * Gets cafe within a radius of a location 
     * @param loc the input location as a Coordinate object
     * @param radius the input radius in meters
     */
    private void getCafes(Coordinate loc, int radius) {
        // Construct Nearby Search API call url
        String apiCall = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + 
            loc.getLat() + "," + loc.getLng() + 
            "&radius=" + radius + 
            "&types=cafe&key=" + key;

        // Get response in json and map to CafeResponse object
        String json = getJsonResponse(apiCall);
        CafeResponse response = gson.fromJson(json, CafeResponse.class);

        // Get cafes as a list
        List<Cafe> cafes = response.getResults();
        
        // Filter cafes
        filter(cafes, 3.0);

        // Map vertex indices to filtered cafes
        for (int i = 0; i < cafes.size(); i++) {
            vertexMap.put(i + numLocs, cafes.get(i));
        }
            
    } 

    /**
     * Filters cafes
     * Note this method has implemented only a "rating" filter so far
     * @param cafes the input list of cafes
     * @param rating the minimum rating
     */
    private void filter(List<Cafe> cafes, double rating) {
        for (int i = 0; i < cafes.size(); i++) {
            if (cafes.get(i).getRating() < rating) {
                cafes.remove(i);
            }

        }
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
     * @return the nearest cafe to both input locations 
     */
    public Cafe getNearestCafe() { 
        // Run Dijkstra's starting at vertex 0, the first input user location
        Object[] res = dijkstra(0);
        int[] dist = (int[]) res[0];
        int[] parent = (int[]) res[1];

        // Get the median vertex on the shortest path from vertex 0 to vertex 1
        int med = getMedianVertex(0, 1, dist, parent);

        return vertexMap.get(med);
    }
}
