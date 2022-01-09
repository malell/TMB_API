package Controller;

import Model.*;
import View.Menu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Control {
    private DataModel dataModel;

    /**
     * Prova de llegir el fitxer i inicia el sistema
     */
    public void begin(){
        try {
            //s'omple localitzacions
            dataModel = new DataModel();
            dataModel.parseLocationsJsonAndFillIn();
            session();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sessió de l'usuari
     * @throws IOException En cas de l'apertura o el tancament incorrecte del fitxer
     */
    private void session() throws IOException {
        String optionMenu;
        boolean exitMenu = false;
        //Comprovem si existeix fitxer amb user guardat
        Path path = Paths.get(DataModel.pathUser);
        if (Files.exists(path)) {
            //s'omple user de DataModel amb el contingut d'user.json
            dataModel.parseUserJsonAndFillIn();
            Menu.printScreen("Benvingut de nou a l'aplicació de TMBJson " + dataModel.getUser().getUserName() + "!");
        } else {
            //s'omple user de DataModel amb el que escriu l'usuari via teclat
            dataModel.setUser(Menu.newUser());
            //Per petició de l'enunciat, s'ha de crear el fitxer "user.json" en aquest moment (pàg.5 on "Opcionalment,...")
            dataModel.createUserFile();
        }
        //En aquest punt user està guardat en dataModel

        //La sessió procedeix a mostrar per pantalla totes les opcions

        do {
            optionMenu = Menu.printScreenGetString(
                    System.lineSeparator() +
                    "1. Gestió d'usuari" + System.lineSeparator() +
                    "2. Buscar localitzacions" + System.lineSeparator() +
                    "3. Planejar ruta" + System.lineSeparator() +
                    "4. Temps d'espera del bus" + System.lineSeparator() +
                    "5. Sortir" + System.lineSeparator(),
                    "Selecciona una opció: "
            );
            switch (optionMenu) {
                case "1":
                    //Gestió d'usuari
                    userAdm();
                    break;
                case "2":
                    //Buscar localitzacions:
                    findLoc();
                    break;
                case "3":
                    //Planejar ruta:
                    planRoute();
                    break;
                case "4":
                    //Temps d'espera del bus:
                    waitTime();
                    break;
                case "5":
                    //Sortir
                    exitMenu = true;
                    //Menu.exitMainMenu();
                    Menu.printScreen("Gràcies per utilitzar l'aplicació de TMBJson!");
                    dataModel.writeUserJson();
                    break;
                default:
                    //Menu.mainMenuOptionError();
                    Menu.printScreen(System.lineSeparator() +
                            "Opció incorrecta! L'opció ha de ser un valor enter entre 1 i 5, ambdós inclosos.");
            }
        } while (!exitMenu);
    }

    /**
     * Primera opció
     */
    private void userAdm(){
        String optionMenu;
        boolean exitMenu = false;
        do {
            //Menu.userManagementMenu();
            optionMenu = Menu.printScreenGetString(
                    System.lineSeparator() +
                    "a)Les meves localitzacions" + System.lineSeparator() +
                    "b)Historial de localitzacions" + System.lineSeparator() +
                    "c)Les meves rutes" + System.lineSeparator() +
                    "d)Parades i estacions preferides" + System.lineSeparator() +
                    "e)Estacions inaugurades el meu any de naixement" + System.lineSeparator() +
                    "f)Tornar al menú principal" + System.lineSeparator(),
                    "Selecciona una opció: "
            );
            switch (optionMenu.toLowerCase()) {
                case "a":
                    //Les meves localitzacions
                    Function.myLocations(dataModel.getLocations(), dataModel.getUser().getCreatedLocations());
                    break;
                case "b":
                    //Historial de localitzacions
                    Function.locHistory(dataModel.getUser().getSearchedLocations());
                    break;
                case "c":
                    //Les meves rutes
                    Function.myRoutes(dataModel.getUser().getCreatedRoutes());
                    break;
                case "d":
                    //Parades i estacions preferides
                    Function.favLocations(dataModel.getUser().getStarredLocations());
                    break;
                case "e":
                    //Estacions inaugurades el meu any de naixament
                        //Function.yearStations(dataModel.getUser().getYearBirth());
                    Function.yearStations(dataModel.getUser().getYearBirth());
                    break;
                case "f":
                    //Tornar al menú principal
                    exitMenu = true;
                    break;
                default:
                    //Menu.userManagementOptionError();
                    Menu.printScreen(System.lineSeparator() +
                            "Opció incorrecta! L'opció ha de ser una lletra entre a i f, ambdues incloses");
            }
        } while (!exitMenu);
    }


    /**
     * Segona opció
     */
    private void findLoc(){
        String answer;
        boolean isStarredAlready = false;
        String name = Menu.printScreenGetString("","Introdueix el nom d'una localització: ");
        //Consulta les localitzacions creades i Json
        Location locationFound = Function.getLocationIfExists(name, dataModel.getLocations(), dataModel.getUser().getCreatedLocations());

        if (locationFound == null) {
            Menu.printScreen(System.lineSeparator() +
                    "Ho sentim, no hi ha cap localització amb aquest nom.");
        } else {

            //Afegeix la localització buscada a la primera fila de les buscades
            dataModel.getUser().getSearchedLocations().addFirst(locationFound);

            Menu.printScreen(System.lineSeparator() +
                    locationFound);
            answer = Menu.yesNoRequest("Vols guardar la localització trobada com a preferida? (sí/no) ");

            //Comprovem si ja és preferida
            String starred = locationFound.getName().toLowerCase();
            for (StarredLocation sl : dataModel.getUser().getStarredLocations()){
                if(starred.equals(sl.getLocation().getName().toLowerCase())){
                    isStarredAlready = true;
                    break;
                }
            }

            if (answer.equals("si")) {
                if(!isStarredAlready) {
                    String type = Menu.starredTypeRequest();
                    dataModel.getUser().getStarredLocations().add(new StarredLocation(locationFound, type));
                    Menu.printScreen(System.lineSeparator() +
                            locationFound.getName() + " s'ha assignat com a una nova localització preferida.");
                } else Menu.printScreen("Aquesta localització ja és preferida!");
            }
        }
    }

    //Apartat 3
    //Planner TMB
    //https://www.tmb.cat/en/barcelona/journey-planner
    // e.g
    //https://www.tmb.cat/en/barcelona/journey-planner?coords__ori=2.1302711999999997%2C41.4093011&coords__dest=2.1209085000000414%2C41.3790446&
    //name__ori=Carrer%20dels%20Quatre%20Camins%2C%2030%2C%2008022%20Barcelona%2C%20Spain&name__dest=Camp%20Nou%20Acceso%201%2C%20Barcelona&date=&near=false&time=&optimizationMode=BEST_ROUTE&arriveBy=false
    //https://developer.tmb.cat/api-docs/v1/planner#
    /**
     * Tercera opció
     */
    private void planRoute() {
        String originName = Menu.getCoordLocationInfo("Origen", dataModel.getLocations(), dataModel.getUser().getCreatedLocations());
        String destinationName = Menu.getCoordLocationInfo("Destí", dataModel.getLocations(), dataModel.getUser().getCreatedLocations());
        boolean isArrivalDate = Menu.exitArrivalRequest();

        //Comprovem que la data especificada sigui real, tenint en compte també els anys de traspas
        String date = Menu.dateHourRequest("data", "^(02-29-(2000|2400|2800|(19|2[0-9](0[48]|[2468][048]|[13579][26]))))$"
                                            + "|^(02-(0[1-9]|1[0-9]|2[0-8])-((19|2[0-9])[0-9]{2}))$"
                                            + "|^((0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01])-((19|2[0-9])[0-9]{2}))$"
                                            + "|^((0[469]|11)-(0[1-9]|[12][0-9]|30)-((19|2[0-9])[0-9]{2}))$");
        String hour = Menu.dateHourRequest("hora", "^([0-1]?[0-9]|2[0-3]):[0-5][0-9](am|pm)$");
        int maxWalkingDist = Integer.parseInt(Menu.printScreenGetString(System.lineSeparator() +
                                                                        "Màxima distància caminant en metres?", ""));

        Location aux;
        String originCoord = originName;
        aux = Function.getLocationIfExists(originName, dataModel.getLocations(), dataModel.getUser().getCreatedLocations());
        if (aux != null) {
            originCoord = aux.getCoordinates()[Function.iLatitude] + ", " + aux.getCoordinates()[Function.iLongitude];
            originName = aux.getName();
        }
        String destinationCoord = destinationName;
        aux = Function.getLocationIfExists(destinationName, dataModel.getLocations(), dataModel.getUser().getCreatedLocations());
        if (aux != null) {
            destinationCoord = aux.getCoordinates()[Function.iLatitude] + ", " + aux.getCoordinates()[Function.iLongitude];
            destinationName = aux.getName();
        }

        try {
            Route route = dataModel.getRoute(originCoord, destinationCoord, originName, destinationName, isArrivalDate, date, hour, maxWalkingDist);
            Menu.printScreen(System.lineSeparator() +
                    "Combinació més ràpida:" +
                    route.stepsToString(false));
        }
        catch (APIAccessException e) {
            Menu.printScreen(e.getMessage());
        }
    }

    /**
     * Quarta opció
     */
    private void waitTime(){
        String str0 = "";
        StringBuilder str1 = new StringBuilder();
        boolean error;
        do {
            error = false;
            int code = Menu.stopCodeRequest();
            try {
                LinkedList<Line> lines = dataModel.getNextLinesTimeFrom(code);
                if (dataModel.isStarredLocation(code)) {
                    str0 = System.lineSeparator() +
                            "Parada preferida!";
                }

                for (Line l : lines) {
                    str1.append(System.lineSeparator()).append(l.getName()).append(" - ")
                            .append(l.getDestination()).append(" - ").append(l.getTimeInTxt());
                }
                Menu.printScreen(str0 + str1);
            } catch (APIAccessException e) {
                Menu.printScreen(e.getMessage());
                error = true;
            }
        } while(error);
    }

}
