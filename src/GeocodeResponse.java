import java.util.List;

/**
 * Maps the Geocode API call
 */
public class GeocodeResponse {

    public class Location {
        private double lat;
        private double lng;
        public double getLat() {
            return lat;
        }
        public double getLng() {
            return lng;
        }
    }

    public class Geometry {
        private Location location;
        public Location getLocation() {
            return location;
        }
    }
    
    public class Result {
        private String formatted_address;
        private Geometry geometry;
        public Geometry getGeometry() {
            return geometry;
        }
        public String getAddress() {
            return formatted_address;
        }
    }


    private List<Result> results;
    private String status;

    public List<Result> getResults() {
        return results;
    }

    /**
     * 
     * @return the addresses of results as an array of strings
     */
    public String[] getAddressData() {
        String[] res = new String[results.size()];

        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            res[i] = r.getAddress();
        }

        return res;
    }

    /**
     * 
     * @return the locations of results as an array of Coordinate objects
     */
    public Coordinate[] getLocationData() {
        Coordinate[] res = new Coordinate[results.size()];

        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            Location l = r.getGeometry().getLocation();
            res[i] = new Coordinate(l.getLat(), l.getLng());
        }

        return res;
    }
}