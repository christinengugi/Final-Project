public class App {

    public static final String API_KEY = "AIzaSyDbGKLgDehhTw5e74VYe3jvACTBS9GdrVI";
    public static void main(String[] args) throws Exception {
        Graph g = new Graph(new Coordinate(39.950820, -75.198550), new Coordinate(39.947220, -75.153600), API_KEY);
        Cafe cafe = g.getNearestCafe();
        Coordinate cafeLoc = new Coordinate(cafe.getGeometry().getLocation().getLat(), cafe.getGeometry().getLocation().getLng());
        System.out.println(cafe.getGeometry().getLocation().getLat() + "," + cafe.getGeometry().getLocation().getLng());
        System.out.println(g.getAddress(cafeLoc));

    }
}
