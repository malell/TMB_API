package Model;

import java.util.Arrays;

public class Restaurant extends Location {

    private String[] characteristics;

    public Restaurant (String name, double[] coordinates, String description, String[] characteristics) {
        super(name, coordinates, description);
        this.characteristics = characteristics;
    }

    public Restaurant (Restaurant r) {
        super(r.getName(), r.getCoordinates(), r.getDescription());
        this.characteristics = r.getCharacteristics();
    }

    public String[] getCharacteristics() {
        return characteristics;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        StringBuilder strCharacteristics = new StringBuilder();
        for (String characteristic : characteristics) {
            strCharacteristics.append(System.lineSeparator()).append("\t- ").append(characteristic);
        }
        return super.toString() + System.lineSeparator() +
                    "Característiques:" + strCharacteristics;
    }

}
