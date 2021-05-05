public class App {
    public static void main(String[] args) throws Exception {
        Graph g = new Graph(new Coordinate(39.950820, -75.198550), new Coordinate(39.947220, -75.153600));
        Cafe cafe = g.getNearestCafe();
        System.out.println(cafe.getGeometry().getLocation().getLat() + "," + cafe.getGeometry().getLocation().getLng());

    }
}
