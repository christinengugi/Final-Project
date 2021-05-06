/**
 * App class that contains the main method
 */
public class App {

    // Google Maps API key
    public static final String API_KEY = "AIzaSyDbGKLgDehhTw5e74VYe3jvACTBS9GdrVI";
    public static void main(String[] args) throws Exception {

        // Create address strings
        String addr1 = "3700 Spruce Street, Philadelphia, PA";
        String addr2 = "Talula's Garden, Philadelphia, PA";

        // Create a graph
        Graph g = new Graph(addr1, addr2, API_KEY);
        // Get teh nearest cafe
        Cafe cafe = g.getNearestCafe();

        // Create a Coordinate object
        Cafe.Location cafeLoc = cafe.getGeometry().getLocation();
        Coordinate cafeCoord = new Coordinate(cafeLoc.getLat(), cafeLoc.getLng());

        // Output the result
        System.out.println("The nearest cafe is " + cafe.getName() + ", located at " + g.getAddress(cafeCoord, API_KEY) + ".");

    }
}
