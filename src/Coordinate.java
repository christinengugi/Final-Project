/**
 * Coordinate class representing a location's latitude and longitude
 */
public class Coordinate {
    private double lat;
    private double lng;

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    /**
     * Computes the midpoint between two coordinates
     * Uses the Haversine formula to calculate the great-circle distance
     * @param loc1 the first input location as a Coordinate object
     * @param loc2 the second input location as a Coordinate object
     * @return the midpoint as a Coordinate object 
     */
    public static Coordinate mid(Coordinate loc1, Coordinate loc2) {

        double dLng = Math.toRadians(loc2.getLng() - loc1.getLng());
    
        double lat1 = Math.toRadians(loc1.getLat());
        double lat2 = Math.toRadians(loc2.getLat());
        double lon1 = Math.toRadians(loc1.getLng());
    
        double bx = Math.cos(lat2) * Math.cos(dLng);
        double by = Math.cos(lat2) * Math.sin(dLng);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + bx) * (Math.cos(lat1) + bx) + by * by));
        double lon3 = lon1 + Math.atan2(by, Math.cos(lat1) + bx);
    
        return new Coordinate(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }
}