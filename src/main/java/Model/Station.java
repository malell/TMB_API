package Model;

import java.util.LinkedList;

public class Station {
    private String name;
    private int code;
    private String service;
    private LinkedList<Line> lines;

    public Station(String name, int code, String service, LinkedList<Line> lines) {
        this.name = name;
        this.code = code;
        this.service = service;
        this.lines = lines;
    }

    public Station(String name, int code) {
        this.name = name;
        this.code = code;
        this.service = "";
        this.lines = null;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getService() {
        return service;
    }

    public LinkedList<Line> getLines() {
        return lines;
    }

    /***
     * Formata el text a mostrar per consola
     * @return  Text a mostrar
     */
    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", service='" + service + '\'' +
                ", lines=" + lines +
                '}' + System.lineSeparator();
    }
}
