package Model;

import java.util.Arrays;

public class Monument extends Location {
    private String architect;
    private int inauguration;

    public Monument(String name, double[] coordinates, String description,
                    String architect, int inauguration){
        super(name, coordinates, description);
        this.architect = architect;
        this.inauguration = inauguration;
    }

    public Monument(Monument m) {
        super(m.getName(), m.getCoordinates(), m.getDescription());
        this.architect = m.getArchitect();
        this.inauguration = m.getInauguration();
    }

    public String getArchitect() {
        return architect;
    }

    public int getInauguration() {
        return inauguration;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return super.toString() + System.lineSeparator() +
                "Arquitecte: " + architect + System.lineSeparator() +
                "Any d'inauguració: " + inauguration;
    }

}
