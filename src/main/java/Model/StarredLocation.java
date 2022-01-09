package Model;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class StarredLocation {
    private Location location;
    private String type;
    private LocalDate starredDate;

    public StarredLocation (Location location, String type) {
        this.location = location;
        //ERRORS AT TYPE: if != ("casa", "feina", "estudis", "oci", "cultura)
        this.type = type;
        Date date = new Date();
        this.starredDate = (date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Location getLocation() {
        return location;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return "StarredLocation{" +
                "location=" + location +
                ", type='" + type + '\'' +
                ", starredDate=" + starredDate +
                '}';
    }
}
