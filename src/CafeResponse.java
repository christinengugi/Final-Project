import java.util.List;

public class CafeResponse {
    private List<String> html_attributions;
    private List<Cafe> results;
    private String status;

    public List<Cafe> getResults() {
        return results;
    }
}
