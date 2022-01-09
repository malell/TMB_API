package Model;

import java.util.LinkedList;

public class Route {
    private String origin;
    private String destination;
    private boolean arrivalDate;
    private String date;
    private String hour;
    private int maxWalkingDist;
    private int time;
    private LinkedList<Step> steps;

    public Route(String origin, String destination, boolean arrivalDate, String date, String hour, int maxWalkingDist, int time, LinkedList<Step> steps) {
        this.origin = origin;
        this.destination = destination;
        this.arrivalDate = arrivalDate;
        this.date = date;
        this.hour = hour;
        this.maxWalkingDist = maxWalkingDist;
        this.time = time;
        this.steps = steps;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return System.lineSeparator() +
                "\t-Origen: " + origin + System.lineSeparator() +
                "\t-Destí: " + destination + System.lineSeparator() +
                "\t-Dia " + (arrivalDate ? "d'arribada: " : "de sortida: ") + date + " a les " + hour + System.lineSeparator() +
                "\t-Màxima distància caminant: " + maxWalkingDist + " metres" + System.lineSeparator() +
                "\t-Combinació més ràpida:" +
                stepsToString(true);
    }

    /***
     * Mostra la combinació més ràpida tenint en compte el format
     * @param extraTab  Si volem una tabulació extra
     * @return  Text a mostrar per consola
     */
    public String stepsToString(boolean extraTab) {
        String extra = extraTab ? "\t" : "";
        StringBuilder str = new StringBuilder(System.lineSeparator() +
                extra + "\tTemps del trajecte: " + time + " min" + System.lineSeparator() +
                extra + "\tOrigen" + System.lineSeparator() +
                extra + "\t|");
        for (Step step : steps) {
            str.append(System.lineSeparator()).append(extra).append("\t").append(step.toString()).append(System.lineSeparator()).append(extra).append("\t|");
        }
        str.append(System.lineSeparator()).append(extra).append("\tDestí");
        return str.toString();
    }
}
