package View;

import Controller.Function;
import Model.Location;
import Model.User;

import java.text.Normalizer;
import java.util.LinkedList;
import java.util.Scanner;

public class Menu {

    /***
     * Mostra missatge, pregunta per consola i retorna resposta
     * @param message   missatge a mostrar
     * @param request   demana resposta
     * @return  retorna resposta
     */
    public static String printScreenGetString(String message, String request) {
        System.out.println(message);
        System.out.print(request);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /***
     * Mostra per pantalla
     * @param message Missatge a mostrar per pantalla
     */
    public static void printScreen(String message) {
        System.out.println(message);
    }

    /***
     * Demana credencials d'usuari en cas que no existeixi
     * @return  Retorna l'objecte de l'usuari creat per consola
     */
    public static User newUser(){

        System.out.println("Benvingut a l'aplicació de TMBJson! Si us plau, introdueix les dades que se't demanen.\n");
        Scanner input = new Scanner(System.in);

        System.out.println("Nom d'usuari:");
        String name = input.nextLine();
        System.out.println();

        System.out.println("Correu electrònic:");
        String email = input.nextLine();
        System.out.println();

        System.out.println("Any de naixement:");
        int year = input.nextInt();
        System.out.println();

        System.out.println("La informació s'ha registrat amb èxit!");
        System.out.println();

        return new User(name, email, year);
    }

    /***
     * Mostra les localitzacions amb format "   -loc" de més a menys recent
     * @param locations Array de localitzacions a mostrar
     */
    //Apartat "A" Opció 1 Messages
    public static void showLocationNames(LinkedList<Location> locations) {
        for (Location l : locations) {
            printScreen("\t-" + l.getName());
        }
    }

    /***
     * Comprova que la resposta sigui si o no
     * @param request   pregunta sobre la que es demana resposta
     * @return  retorna un si o no
     */
    public static String yesNoRequest(String request) {
        String answer;
        boolean error;
        do {
            answer = printScreenGetString("", request);
            //Treu accents i majúscules
            answer = Normalizer.normalize(answer, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            error = !answer.equals("si") && !answer.equals("no");
            if (error) {
                printScreen("Error! S'ha d'introduir \"sí\" o \"no\".");
            }
        } while (error);
        return answer;
    }

    /***
     * Troba una localització ja existent
     * @param locations Array de localitzacions json
     * @param createdLocations  Array de localitzacions creades per l'usuari
     * @return  Localització testejada
     */
    public static String locationNameRequest(LinkedList<Location> locations, LinkedList<Location> createdLocations) {
        boolean error;
        String str;
        do {
            error = false;
            str = printScreenGetString("", "Nom de la localització: ");
            for (Location l : locations) {
                if (l.getName().toLowerCase().equals(str.toLowerCase())) {
                    error = true;
                    break;
                }
            }
            for (Location l : createdLocations) {
                if (l.getName().toLowerCase().equals(str.toLowerCase()) || error) {
                    error = true;
                    break;
                }
            }
            if (error) {
                printScreen(System.lineSeparator() +
                        "Error! Aquesta localizació ja existeix.");
            }
        } while(error);
        return str;
    }

    /***
     * Retorna si les coordenades compleixen la norma EPSG
     * @return  Array de coordenades
     */
    public static double[] locationCoordinatesRequest() {
        boolean error;
        double[] coord = new double[2];
        do {
            error = false;
            String aux = Menu.printScreenGetString("", "Longitud: " + System.lineSeparator());
            coord[Function.iLongitude] = Double.parseDouble(aux);

            aux = Menu.printScreenGetString("", "Latitud: " + System.lineSeparator());
            coord[Function.iLatitude] = Double.parseDouble(aux);
            if (!Function.isEPSG(coord)) {
                error = true;
                printScreen("Error! Les coordenades introduides no compleixen el sistema de coordenades EPSG:4326.");
            }
        } while (error);
        return coord;
    }

    /***
     * Mostra les localitzacions buscades per consola
     * @param searchedLocations List de localitzacions buscades
     */
    //Apartat "B" Opció 1 Messages
    public static void showSearchedLocations(LinkedList<Location> searchedLocations) {
        printScreen(System.lineSeparator() +
                    "Localitzacions buscades:");
        showLocationNames(searchedLocations);
    }

    //...

    /***
     * Comprova que el tipus de localització a guardar sigui correcte
     * @return Localització comprovada
     */
    //Opció 2 Messages
    public static String starredTypeRequest() {
        boolean error;
        String type;
        do {
            error = false;
            type = printScreenGetString("","Tipus (\"casa\", \"feina\", \"estudis\", \"oci\" o \"cultura\"): ").toLowerCase();
            if (!Function.isCorrectType(type)) {
                error = true;
                printScreen("Error! S'ha d'introduir \"casa\", \"feina\", \"estudis\", \"oci\" o \"cultura\".");
            }
        } while (error);

        return type;
    }

    /***
     * Determina si un número de parada és vàlid
     * @return  Num parada
     */
    public static int stopCodeRequest() {
        boolean error;
        int code = -1;
        do {
            error = false;
            try {
                String stopCode = Menu.printScreenGetString("", "Introdueix el codi de parada: ");
                code = Integer.parseInt(stopCode);
            } catch (NumberFormatException e) {
                Menu.printScreen("Error, codi de parada no vàlid!");
                error = true;
            }
        } while(error);
        return code;
    }

    /***
     *  Mostra per consola el text relacionades amb origen/desti de l'opció "Planejar ruta"
     * @param from  Input a determinar
     * @param locations List de localitzacions json
     * @param createdLocations  List de localitzacions propies de l'usuari
     * @return  L'input propi de l'usuari
     */
    public static String getCoordLocationInfo(String from, LinkedList<Location> locations, LinkedList<Location> createdLocations) {
        boolean error;
        String locationInfo;
        Menu.printScreen("");
        do {
            locationInfo = Menu.printScreenGetString(from + "? (lat,lon/nom localització)", "");
            //Es diferencia si s'han introduit coordenades o un nom de localització
            if (!Function.isCoordFormat(locationInfo)) {
                Location lFound = Function.getLocationIfExists(locationInfo, locations, createdLocations);
                //Es comprova si s'ha introduit un nom d'una localització del sistema
                error = lFound == null;
            }
            else error = false;

            if (error) {
                Menu.printScreen(System.lineSeparator() +
                        "Ho sentim, aquesta localització no és vàlida :(");
            }
        } while(error);
        return locationInfo;
    }

    /***
     *  Mostra per consola el text relacionat amb l'hora d'arribada o sortida
     * @return comprova que la opció introduida sigui correcte
     */
    public static boolean exitArrivalRequest() {
        boolean error;
        String answer;
        Menu.printScreen("");
        do {
            answer = Menu.printScreenGetString("Dia/hora seran de sortida o d'arribada? (s/a)", "").toLowerCase();
            if (answer.equals("s") || answer.equals("a")) error = false;
            else {
                Menu.printScreen(System.lineSeparator() +
                                "Error! S'ha d'introduir \"s\" o \"a\"!");
                error = true;
            }
        } while (error);
        return answer.equals("a");
    }

    /***
     * Mostra i comprova el format de dia i hora introduit
     * @param request   Determina si es dia o hora
     * @param regex     Regular Expression utilitzada
     * @return  Input de l'usuari
     */
    public static String dateHourRequest(String request, String regex) {
        boolean error;
        String answer;
        String message = request.equals("data") ? "Dia? (MM-DD-YYYY)" : "Hora? (HH:MMam/HH:MMpm)";
        Menu.printScreen("");
        do {
            answer = Menu.printScreenGetString(message, "");
            if (answer.matches(regex)) error = false;
            else {
                Menu.printScreen(System.lineSeparator() +
                                "La " + request + " introduïda és errònia!");
                error = true;
            }
        } while(error);
        return answer;
    }
}
