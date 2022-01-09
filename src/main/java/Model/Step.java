package Model;

public class Step {
    private String mode;
    private int time;
    private Line line;
    private Station startStation;
    private Station endStation;

    public Step (String mode, int time) {
        this.mode = mode;
        this.time = time;
    }

    public Step(String mode, int time, Line line, Station start, Station end) {
        this(mode, time);
        this.line = line;
        this.startStation = start;
        this.endStation = end;
    }

    /***
     * Mostra el pas tal com es demana per consola
     * @return String a mostrar
     */
    @Override
    public String toString() {
        if (mode.equals("WALK")) {
            return "caminar " + time + " min";
        } else if (mode.equals("SUBWAY") || mode.equals("BUS")) {
            return line.getName() + " " + startStation.getName() + "(" + startStation.getCode() + ") -> " +
                    endStation.getName() + "(" + endStation.getCode() + ") " + time + " min";
        }
        return "";
    }
}
