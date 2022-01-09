package Controller;

import Model.*;
import View.Menu;

import java.util.LinkedList;

public class Function {
    private static final double radiusEarth = 6378100.0;
    public static final int iLongitude = 0;
    public static final int iLatitude = 1;

    /**
     * Mostra les localitzacions del usuari
     * @param locations Localitzacions del json
     * @param createdLocations  Localitzacions propies del usuari
     */
    public static void myLocations(LinkedList<Location> locations, LinkedList<Location> createdLocations){
        String answer;
        do {
            if (createdLocations.isEmpty()) {
                Menu.printScreen("No tens cap localització creada.");
            } else {
                Menu.showLocationNames(createdLocations);
            }
            answer = Menu.yesNoRequest("Vols crear una nova localització? (sí/no) ");
            if (answer.equals("si")) {
                String name = Menu.locationNameRequest(locations, createdLocations);
                double[] coordinates = Menu.locationCoordinatesRequest();
                String description = Menu.printScreenGetString("", "Descripció: " + System.lineSeparator());
                Location createdLocation = new Location(name, coordinates, description);
                createdLocations.add(createdLocation);
                Menu.printScreen(System.lineSeparator() +
                        "La informació s'ha registrat amb èxit!" +
                        System.lineSeparator());
            }
        } while(answer.equals("si"));
    }

    /**
     * Mostra les localitzacions buscades per l'usuari
     * @param searchedLocations List de localitzacions buscades
     */
    public static void locHistory(LinkedList<Location> searchedLocations) {
        if (searchedLocations.isEmpty()) {
            Menu.printScreen(System.lineSeparator() +
                    "Encara no has buscat cap localització!" + System.lineSeparator() +
                    "Per buscar-ne una, accedeix a l'opció 2 del menú principal.");
        }
        else {
            Menu.showSearchedLocations(searchedLocations);
        }
    }

    /**
     * Mostra les rutes creades per a l'usuari
     * @param routes    List de rutes creades
     */
    public static void myRoutes(LinkedList<Route> routes) {
        if (routes.isEmpty()) {
            Menu.printScreen(System.lineSeparator() +
                            "Encara no has realitzat cap ruta :(" + System.lineSeparator() +
                            "Per buscar-ne una, accedeix a l'opció 3 del menú principal.");
        }
        else {
            for (int i = 0; i < routes.size(); i++) {
                Menu.printScreen(System.lineSeparator() +
                        "->Ruta " + (i+1) + ":" +
                        routes.get(i).toString());
            }
        }
    }

    /**
     * Mostra les parades/estacions preferides de l'usuari
     * @param starredLocations List de localitzacions preferides
     */
    public static void favLocations(LinkedList<StarredLocation> starredLocations) {
        StringBuilder str = new StringBuilder();
        if (starredLocations.isEmpty()) {
            str = new StringBuilder(System.lineSeparator() +
                    "Per tenir parades i estacions preferides es requereix haver creat una localització preferida anteriorment");
        }
        else {
            for (StarredLocation sl : starredLocations) {
                str.append(System.lineSeparator()).append("-").append(sl.getLocation().getName());
                LinkedList<Station> stations = DataModel.getNearbyBusStopsAndMetroStations(sl);
                if (stations.isEmpty()) {
                    str.append(System.lineSeparator()).append("\tTMB està fent tot el possible perquè el bus i el metro arribin fins aquí.");
                }
                else for (int i = 0; i < stations.size(); i++) {
                    Station s = stations.get(i);
                    StringBuilder lines = new StringBuilder();
                    for (Line l : s.getLines()) {
                        lines.append(" ").append(l.getName());
                    }
                    //IntelIJ me dise que asi eh mejó, not my fault
                    str.append(System.lineSeparator()).append("\t").append(i + 1).append(") ").append(s.getName())
                            .append(" (").append(s.getCode()).append(") ").append(s.getService()).append(lines);
                }
            }
        }
        Menu.printScreen(str.toString());
    }

    /**
     * Busca a la API i mostra per pantalla les estacions i línies relacionades a elles, que s'han inaugurat l'any de
     * naixament de l'usuari (paràmetre)
     * @param yearBirth any de naixement de l'usuari amb el que s'evalua les estacions inaugurades l'any mencionat
     */
    public static void yearStations(int yearBirth){
        StringBuilder str = new StringBuilder(System.lineSeparator() +
                "Estacions inaugurades el " + yearBirth + ":");
        //Omple la llista amb les estacions inaugurades aquell any
        LinkedList<Station> stations = DataModel.getStationsInauguredIn(yearBirth);

        //En el cas que no existissin estacions inaugurades aquell any es moltra missatge informatiu
        if (stations.isEmpty()) {
            str = new StringBuilder(System.lineSeparator() +
                    "Cap estació de metro es va inaugurar el teu any de naixement :(");
        }
        //En el cas que sí existeixin estacions es van acumulant en un String per mostrar-ho per pantalla
        else {
            for (Station s : stations) {
                StringBuilder lines = new StringBuilder();
                for (Line l : s.getLines()) {
                    lines.append(" ").append(l.getName());
                }
                str.append(System.lineSeparator()).append("- ").append(s.getName()).append(lines);
            }
        }
        Menu.printScreen(str.toString());
    }

    /**
     * Informa si unes coordenades compleixen el sistema de coordenades EPSG:4326
     * @param arr   coordenades que hi ha que verificar
     * @return      booleà que informa si les coordenades són correctes
     */
    public static boolean isEPSG(double[] arr){
        boolean foo = false;
        if (arr[iLongitude] >= -180.0 && arr[iLongitude] <= 180.0)
            if (arr[iLatitude] >= -90.0 && arr[iLatitude] <= 90.0)
                foo = true;
        return foo;
    }

    /**
     * Comprova que el tipus de localització preferida sigui correcte
     * @param type  tipus que hi ha que verificar
     * @return  Si es correcte o no
     */
    public static boolean isCorrectType (String type) {
        return type.equals("casa") ||
                type.equals("feina") ||
                type.equals("estudis") ||
                type.equals("oci") ||
                type.equals("cultura");
    }

    /**
     *  Calcula la distancia aproximada entre dues coordenades i informa si aquesta és menor o igual a 500 metres
     * @param coord1    Coordenada 1
     * @param coord2    Coordenada 2
     * @return  La distància entre les coordenades
     */
    public static double getDistance (double[] coord1, double[] coord2) {
        double latDist = toRad(coord2[iLatitude] - coord1[iLatitude]);
        double lonDist = toRad(coord2[iLongitude] - coord1[iLongitude]);
        double a = Math.sin(latDist/2) * Math.sin(latDist/2) +
                Math.cos(toRad(coord1[iLatitude]) * Math.cos(toRad(coord2[iLatitude]))) *
                Math.sin(lonDist/2) * Math.sin(lonDist/2);
        return 2 * radiusEarth * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    /**
     * Comprova el format de coordenades mitjanaçant REGularEXpresions
     * @param coord Coordenada a comprovar
     * @return Cert en cas que compleixi
     */
    public static boolean isCoordFormat(String coord) {
        return coord.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$");
    }

    /**
     * Instancia una copia d'un objecte Location en un nou objecte Location segons la seva classificació
     * @param l     localització a copiar
     * @return      retorna una nova localització
     */
    private static Location instantiateNewLocation(Location l) {
        Location foundLocation;
        if (l instanceof Monument) {
            foundLocation = new Monument((Monument) l);
        }
        else if (l instanceof Restaurant) {
            foundLocation = new Restaurant((Restaurant) l);
        }
        else if (l instanceof Hotel) {
            foundLocation = new Hotel((Hotel) l);
        }
        else {
            foundLocation = new Location(l);
        }
        return foundLocation;
    }

    /**
     * Busca una localització segons el nom d'aquesta entre les localitzacions carregades al principi o les localitzacions
     * creades per l'usuari
     * @param name              Nom a buscar entre les llistes
     * @param locations         llista de localitzacions carregades on buscar
     * @param createdLocations  llista de localitzacions creades on buscar
     * @return      Informació de la localització, si ha coincidit amb el nom
     */
    public static Location getLocationIfExists(String name, LinkedList<Location> locations, LinkedList<Location> createdLocations) {
        Location locationFound = null;
        boolean found = false;
        for (Location l : locations) {
            if (found) break;
            if (name.toLowerCase().equals(l.getName().toLowerCase())) {
                locationFound = instantiateNewLocation(l);
                found = true;
            }
        }
        for (Location l : createdLocations) {
            if (found) break;
            if (name.toLowerCase().equals(l.getName().toLowerCase())) {
                locationFound = instantiateNewLocation(l);
                found = true;
            }
        }
        return locationFound;
    }

    /**
     * Transforma graus en radiants
     * @param value Valor en graus
     * @return  Valor en radiants
     */
    private static double toRad(double value) {
        return (value * Math.PI) / 180;
    }

    /**
     * Transforma segons en minuts enters
     * @param seconds   Número de segons a transformar
     * @return  Minuts enters
     */
    public static int toMinutes(int seconds) {
        int minutes = seconds / 60;
        if ((seconds % 60) > 30) minutes++;
        return minutes;
    }

    /**
     * Comprova si dues coordenades son iguals
     * @param p1    Primera coordenada
     * @param p2    Segona coordenada
     * @return  Si són o no iguals
     */
    public static boolean equalCoordinates (double[] p1, double[] p2) {
        return p1[iLongitude] == p2[iLongitude] && p1[iLatitude] == p2[iLatitude];
    }
}
