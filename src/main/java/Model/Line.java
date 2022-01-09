package Model;

public class Line {
    String name;
    String destination;
    String time_in_txt;
    int time_in_s;

    public Line(String name, String destination, String time_in_txt, int time_in_s) {
        this.name = name;
        this.destination = destination;
        this.time_in_txt = time_in_txt;
        this.time_in_s = time_in_s;
    }

    public Line(String name) {
        this.name = name;
        this.destination = "";
        this.time_in_txt = "";
        this.time_in_s = -1;
    }

    public String getName() {
        return name;
    }

    public int getTimeInS() {
        return time_in_s;
    }

    public String getDestination() {
        return destination;
    }

    public String getTimeInTxt() {
        return time_in_txt;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", destination='" + destination + '\'' +
                ", time_in_txt='" + time_in_txt + '\'' +
                ", time_in_s=" + time_in_s +
                '}' + System.lineSeparator();
    }
}
