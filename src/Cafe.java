public class Cafe {

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

    private String business_status;
    private Geometry geometry;
    private String icon;
    private String name;
    private String place_id;
    private double rating;
    private String reference;
    private String scope;
    private int user_ratings_total;
    private String vicinity;

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

}
