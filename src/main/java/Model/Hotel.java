package Model;

import java.util.Arrays;

public class Hotel extends Location {
    private int stars;

    public Hotel(String name, double[] coordinates, String description, int stars){
        super(name, coordinates, description);
        this.stars = stars;
    }

    public Hotel(Hotel h) {
        super(h.getName(), h.getCoordinates(), h.getDescription());
        this.stars = h.getStars();
    }

    public int getStars() {
        return stars;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return super.toString() + System.lineSeparator() +
                "Estrelles: " + stars;
    }

}
