import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class Graph {

    private static final String API_KEY = "AIzaSyDbGKLgDehhTw5e74VYe3jvACTBS9GdrVI";

    private Map<Integer, Object> vertexLoc;
    private int[][] adjm;

    public Graph(Coordinate loc1, Coordinate loc2) {
        // Compute distance and midpoint b/w loc1 and loc2
        
        // Get cafes within radius (distance/2) of midpoint
        Coordinate mid = new Coordinate(26.272514, 50.195682);
        int radius = 15000;
        getCafes(mid, radius);
         
        // Let V = set of cafes and loc1, loc2
        // Map vertex indices to object containing more info (i.e address, coordinates, ...etc.)

        // Let E = edge iff distance b/w vertices is < some constant
    }
    
    private void getCafes(Coordinate mid, int radius) {
        try {
            URL url = new URL(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + 
                mid.getLat() + "," + mid.getLng() + 
                "&radius=" + radius + 
                "&types=cafe&key=" + API_KEY);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();
            
            if (code == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                System.out.println(sb.toString());
                // map string to json obj
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

}
