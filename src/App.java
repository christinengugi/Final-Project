public class App {
    public static void main(String[] args) throws Exception {
        Graph g = new Graph(new Coordinate(39.954464, -75.217023), new Coordinate(39.952753, -75.166254));
        System.out.println(g.getNearestCafe().getName());
    }
}
