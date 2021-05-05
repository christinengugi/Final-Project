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

    public static Coordinate mid(Coordinate loc1, Coordinate loc2) {

        double dLng = Math.toRadians(loc2.getLng() - loc1.getLng());
    
        double lat1 = Math.toRadians(loc1.getLat());
        double lat2 = Math.toRadians(loc2.getLat());
        double lon1 = Math.toRadians(loc1.getLng());
    
        double Bx = Math.cos(lat2) * Math.cos(dLng);
        double By = Math.cos(lat2) * Math.sin(dLng);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
    
        return new Coordinate(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }
}