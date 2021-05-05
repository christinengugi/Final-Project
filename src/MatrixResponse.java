import java.util.List;

public class MatrixResponse {

    public class Distance {
        private String text;
        private int value;
        public String getText() {
            return text;
        }
        public int getValue() {
            return value;
        }
    }

    public class Element {
        private String status;
        private Distance distance;
        public String getStatus() {
            return status;
        }
        public Distance getDistance() {
            return distance;
        }
    }

    public class Row {
        private List<Element> elements;
        public List<Element> getElements() {
            return elements; 
        }
    }

    private String status;
    private List<Row> rows;

    public List<Row> getRows() {
        return rows;
    }


    public int[][] getData() {
        int[][] res = new int[rows.size()][rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            List<Element> elements = r.getElements();
            for (int j = 0; j < elements.size(); j++) {
                Element e = elements.get(j);
                res[i][j] = e.getDistance().getValue();
            }
        }

        return res;
    }


}