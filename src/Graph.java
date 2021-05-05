import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import com.google.gson.Gson;

import MatrixResponse.Element;

public class Graph {

    private static final String API_KEY = "AIzaSyDbGKLgDehhTw5e74VYe3jvACTBS9GdrVI";

    private Map<Integer, Cafe> vertexMap;
    private int[][] adjm;

    public Graph(Coordinate loc1, Coordinate loc2) {
        // Compute distance and midpoint b/w loc1 and loc2
        int distance = getDistance(loc1, loc2);
        
        // Get cafes within radius (distance/2) of midpoint
        Coordinate mid = Coordinate.mid(loc1, loc2);
        int radius = distance / 2;
        // Map vertex indices to object containing more info (i.e address, coordinates, ...etc.)
        vertexMap = new HashMap<>();
        getCafes(mid, radius);
         
        // Let V = set of cafes and loc1, loc2
        if (vertexMap.size() > 0) {
            getDistances();
        }
        

        // Let E = edge iff distance b/w vertices is < some constant
    }

    private int getDistance(Coordinate loc1, Coordinate loc2) {
        String apiCall = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
        apiCall += "&origins=" + loc1.getLat() + "," + loc1.getLng();
        apiCall += "&destinations=" + loc2.getLat() + "," + loc2.getLng();
        apiCall += "&key=" + API_KEY;
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

    private void getDistances() {
        String apiCall = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
        
        // construct apicall string
        StringBuilder locs = new StringBuilder();
        for (Entry<Integer, Cafe> e : vertexMap.entrySet()) {
            Cafe c = e.getValue();
            locs.append(c.getGeometry().getLocation().getLat() + "," + c.getGeometry().getLocation().getLng() + "|");
        }
        String locsString = locs.toString().substring(0, locs.length() - 1);
       
        apiCall += "&origins=" + locsString + "&destinations=" + locsString + "&key=" + API_KEY;

        // connect to url
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
                
                for (int i = 0; i < res.length; i++) {
                    for (int j = 0; j < res[0].length; j++) {
                        // filtering
                        System.out.println(res[i][j]);
                    }
                }

                // populate edges
                adjm = res;



                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        
       
    }
    
    /**
     * Calls Google Maps API to get cafes within a radius of a given location
     * @param loc the input location 
     * @param radius the input radius
     */
    private void getCafes(Coordinate loc, int radius) {
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
                    vertexMap.put(i + offset, cafes.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

}
