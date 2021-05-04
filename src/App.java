public class App {
    public static void main(String[] args) throws Exception {
        int[][] g = new int[][] {
            { 0, 5, 3, 0, 0, 0 },
            { 5, 0, 1, 2, 0, 0 },
            { 3, 1, 0, 4, 0, 0 },
            { 0, 2, 4, 0, 2, 0 },
            { 0, 0, 0, 2, 0, 6 },
            { 0, 0, 0, 0, 6, 0 },
        };
        Object[] res = Algorithms.dijkstra(g, 0);
        int[] dist = (int[]) res[0];
        int[] parent = (int[]) res[1];
        int med = Algorithms.getMedianVertex(0, 5, dist, parent);
        System.out.println(med);
        
    }
}
