import java.util.List;

/**
 * Maps the Nearby Search for Cafes API call
 */
public class CafeResponse {
    private List<Cafe> results;
    private String status;

    public List<Cafe> getResults() {
        return results;
    }
}
