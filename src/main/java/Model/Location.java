package Model;

import java.util.Arrays;

public class Location {
    private String name;
    private double[] coordinates;
    private String description;

    public Location (String name, double[] coordinates, String description) {
        this.name = name;
        this.coordinates = coordinates;
        this.description = description;
    }

    public Location (Location l) {
        this.name = l.getName();
        this.coordinates = l.getCoordinates();
        this.description = l.getDescription();
    }

    public String getName() {
        return name;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public String getDescription() {
        return description;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return "Posició: " + coordinates[1] + ", " + coordinates[0] + System.lineSeparator() +
                    "Descripció:" + System.lineSeparator() +
                    description;
    }
}
