package Model;

import Controller.Function;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.rmi.server.ExportException;
import java.util.*;


//Parsing JSON using GSON within a Maven project in IntelliJ IDEA
//https://www.youtube.com/watch?v=_kHKp_CuVQI

public class DataModel {
    private LinkedList<Location> locations;
    private User user;
    private static final String pathLocations = "src\\main\\resources\\localitzacions.json";
    public static final String pathUser = "src\\main\\resources\\user.json";

    public DataModel() {
        locations = new LinkedList<>();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LinkedList<Location> getLocations() {
        return locations;
    }

    public User getUser() {
        return user;
    }

    /**
     * Llegeix el fitxer localitzacions.json i omple la llista locations amb tot el contingut del fitxer
     * @throws IOException  en cas de l'apertura o el tancament incorrecte del fitxer
     */
    public void parseLocationsJsonAndFillIn() throws IOException {

        FileReader reader = null;
        try {
            reader = new FileReader(pathLocations);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("No s'ha pogut obrir el fitxer localitzacions.json");
        }

        JsonParser parser = new JsonParser();
        JsonObject datum = parser.parse(reader).getAsJsonObject();

        if(datum.isJsonNull())
            throw new ExceptionInInitializerError("No s'ha pogut llegir correctament el fitxer localitzacions.json");
        reader.close();

        JsonArray arrDatum = datum.getAsJsonArray("locations");
        for (JsonElement l : arrDatum) {
            JsonObject location = l.getAsJsonObject();
            Location locationAux;

            //Get coordinates array
            JsonArray arrCoord = location.getAsJsonArray("coordinates");
            double[] coordinatesAux = new double[arrCoord.size()];
            for (int i = 0; i < coordinatesAux.length; i++) {
                coordinatesAux[i] = arrCoord.get(i).getAsDouble();
            }

            //It distinguishes between Monuments, Restaurants, Hotels and ordinary Locations
            //Model.Monument:
            if (location.has("architect")) {
                locationAux = new Monument(location.get("name").getAsString(),
                        coordinatesAux,
                        location.get("description").getAsString(),
                        location.get("architect").getAsString(),
                        location.get("inauguration").getAsInt());
            }
            //Model.Restaurant:
            else if (location.has("characteristics")) {
                //Get characteristics array
                JsonArray arrCharact = location.getAsJsonArray("characteristics");
                String[] characteristicsAux = new String[arrCharact.size()];
                for (int i = 0; i < characteristicsAux.length; i++) {
                    characteristicsAux[i] = arrCharact.get(i).getAsString();
                }

                locationAux = new Restaurant(location.get("name").getAsString(),
                        coordinatesAux,
                        location.get("description").getAsString(),
                        characteristicsAux);

            }
            //Model.Hotel:
            else if (location.has("stars")) {
                locationAux = new Hotel(location.get("name").getAsString(),
                        coordinatesAux,
                        location.get("description").getAsString(),
                        location.get("stars").getAsInt());
            }
            //Ordinary Model.Location:
            else {
                locationAux = new Location(location.get("name").getAsString(),
                        coordinatesAux,
                        location.get("description").getAsString());
            }
            locations.add(locationAux);
        }
    }

    /**
     * Llegeix el fitxer user.json i omple user amb tot el contingut del fitxer
     * @throws IOException  en cas de l'apertura o el tancament incorrecte del fitxer
     */
    public void parseUserJsonAndFillIn () throws IOException {
        FileReader reader = new FileReader(pathUser);
        Gson gson = new Gson();
        user = gson.fromJson(reader, User.class);
        reader.close();
    }

    /**
     * Crea el fitxer user.json
     */
    public void createUserFile() {
        try {
            FileWriter writer = new FileWriter(pathUser);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sobrescriu el fitxer user.json amb la informació de les localitzacions creades i preferides de l'user
     */
    public void writeUserJson() {
        try {
            this.getUser().getSearchedLocations().clear();
            this.getUser().getCreatedRoutes().clear();
            FileWriter writer = new FileWriter(pathUser);
            Gson gson = new Gson();
            writer.write(gson.toJson(user));
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Ordena una array associativa segons el valor, de forma creixent
     * @param hm    Hashmap a ordenar
     * @return  Hashmap ordenada
     */
    //https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
    // function to sort hashmap by values
    public static HashMap<Station, Double> sortByValue(HashMap<Station, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Station, Double>> list = new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        // put data from sorted list to hashmap
        HashMap<Station, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<Station, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     * Recull la informació de la api respecte estacions de bus i de metro, i retorna una llista de les que estan
     * próximes a l'estació preferida passada per paràmetre
     * @param sl    Localització preferida a comprovar
     * @return  Llista de les estacions próximes
     */
    public static LinkedList<Station> getNearbyBusStopsAndMetroStations(StarredLocation sl) {
        final String busService = "BUS";
        final String metroService = "METRO";

        JsonParser parser = new JsonParser();

        JsonObject stopsApiContent = parser.parse(TMB.callAPI( "transit/parades", "")).getAsJsonObject();
        JsonArray stops = stopsApiContent.get("features").getAsJsonArray();
        int maxStops = stopsApiContent.get("totalFeatures").getAsInt();

        JsonObject stationsApiContent = parser.parse(TMB.callAPI("transit/estacions", "")).getAsJsonObject();
        JsonArray stations = stationsApiContent.get("features").getAsJsonArray();
        int maxStations = stationsApiContent.get("totalFeatures").getAsInt();

        int max = Integer.max(maxStops, maxStations);
        HashMap<Station, Double> stationDistance = new HashMap<>();


        for (int i = 0; i < max; i++) {
            JsonObject entity;

            if (i < maxStops) {
                entity = stops.get(i).getAsJsonObject();
                JsonArray coordJson = entity.get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray();
                double[] coordinates = getCoordinatesFromJson(coordJson);

                if (Function.getDistance(coordinates, sl.getLocation().getCoordinates()) <= 500) {
                    Double distance = Function.getDistance(coordinates, sl.getLocation().getCoordinates());

                    JsonObject stopProperties = entity.get("properties").getAsJsonObject();
                    String stopName = stopProperties.get("NOM_PARADA").getAsString();
                    int stopCode = stopProperties.get("CODI_PARADA").getAsInt();

                    LinkedList<Line> lines = getLines(busService, stopCode, coordinates);
                    Station stop_aux = new Station (stopName, stopCode, busService, lines);
                    stationDistance.put(stop_aux, distance);
                    //starredStations.add(stop_aux);
                }
            }
            if (i < maxStations) {
                entity = stations.get(i).getAsJsonObject();
                JsonArray coordJson = entity.get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray();
                double[] coordinates = getCoordinatesFromJson(coordJson);

                if (Function.getDistance(coordinates, sl.getLocation().getCoordinates()) <= 500) {
                    Double distance = Function.getDistance(coordinates, sl.getLocation().getCoordinates());

                    JsonObject stationProperties = entity.get("properties").getAsJsonObject();
                    String stationName = stationProperties.get("NOM_ESTACIO").getAsString();
                    int stationCode = stationProperties.get("CODI_GRUP_ESTACIO").getAsInt();

                    LinkedList<Line> lines = getLines(metroService, stationCode, null);
                    Station station_aux = new Station (stationName, stationCode, metroService, lines);
                    stationDistance.put(station_aux, distance);
                    //starredStations.add(station_aux);
                }
            }
        }
        //https://javaconceptoftheday.com/convert-hashmap-to-arraylist-in-java/
        HashMap<Station, Double> sortedMap = sortByValue(stationDistance);
        return new LinkedList<>(sortedMap.keySet());

    }


    /**
     * Retorna una llista de les línies que contenen la parada passada per paràmetre,
     * especificat el tipus de servei i les seves coordenades per la seva comprovació. Segons el tipus de servei
     * realitza una consulta diferent.
     * @param service   Nom del servei a consultar: METRO o BUS
     * @param code      Codi de la parada a consultar
     * @param coordinates   Coordenades de la parada a consultar
     * @return  Llista de les línies que passen per la parada corresponent
     */
    private static LinkedList<Line> getLines(String service, int code, double[] coordinates) {
        LinkedList<Line> lines_aux = new LinkedList<>();
        String lineName;
        JsonObject linesApiContent;
        JsonArray lines;

        String codeClass = service.equals("BUS") ? "CODI_PARADA" : "CODI_GRUP_ESTACIO";
        String section = service.equals("BUS") ? "/parades" : "/estacions";
        JsonParser parser = new JsonParser();

        if (service.equals("BUS")) {
            linesApiContent = parser.parse(TMB.callAPI("transit/parades/" + code + "/corresp",
                    "&filter=NOM_OPERADOR='TB'")).getAsJsonObject();
            lines = linesApiContent.get("features").getAsJsonArray();
            for (JsonElement l : lines) {
                if (Function.equalCoordinates(coordinates, getCoordinatesFromJson(l.getAsJsonObject()
                        .getAsJsonObject("geometry").getAsJsonArray("coordinates")))) {
                    lineName = l.getAsJsonObject().getAsJsonObject("properties").get("NOM_LINIA").getAsString();
                    lines_aux.add(new Line(lineName, null, null, -1));
                }

            }
        } else {
            linesApiContent = parser.parse(TMB.callAPI("transit/linies/" + service.toLowerCase(), "")).getAsJsonObject();
            lines = linesApiContent.get("features").getAsJsonArray();
            int maxLines = linesApiContent.get("totalFeatures").getAsInt();

            for (int line = 0; line < maxLines; line++) {
                int lineCode = lines.get(line).getAsJsonObject().get("properties").getAsJsonObject().get("CODI_LINIA").getAsInt();

                JsonObject stopsStationsByLineApiContent = parser.parse(TMB.callAPI("transit/linies/" +
                        service.toLowerCase() + "/" + lineCode + section, "&filter=" + codeClass + "=" + code)).getAsJsonObject();

                if (stopsStationsByLineApiContent.get("totalFeatures").getAsInt() != 0) {
                    lines_aux.add(new Line(lines.get(line).getAsJsonObject().get("properties").getAsJsonObject().get("NOM_LINIA").getAsString()));
                }
            }
        }

        return lines_aux;
    }

    /**
     * Extreu les coordenades d'un JsonArray i les tradueix a una array de double que les conté
     * @param coordJson JsonArray que conté les coordenades en format Json
     * @return          Array de dos posicions amb les coordenades traduides
     */
    private static double[] getCoordinatesFromJson(JsonArray coordJson) {
        return new double[]{coordJson.get(Function.iLongitude).getAsDouble(), coordJson.get(Function.iLatitude).getAsDouble()};
    }

    /**
     * Obté un llistat de totes les estacions que es van inaugurar l'any indicat. Tot a través de la API, recorrent totes
     * les línies de metro existents i filtrant, segons cada línia, les estacions que es van inaugurar entre el gener i
     * decembre de l'any anomenat.
     * @param year      any de naixament de l'usuari, enter de referència.
     * @return          llista d'estacions inaugurades amb les línies que corresponen a cada parada
     */
    public static LinkedList<Station> getStationsInauguredIn(int year) {
        LinkedList<Station> stations_aux = new LinkedList<>();

        JsonParser parser = new JsonParser();
        JsonObject linesApiContent = parser.parse(TMB.callAPI("transit/linies/metro", "")).getAsJsonObject();
        JsonArray lines = linesApiContent.getAsJsonArray("features");
        //Es recorre totes les línies existents
        for (JsonElement l : lines) {
            JsonObject line = l.getAsJsonObject().getAsJsonObject("properties");
            int id_line = line.getAsJsonObject().get("CODI_LINIA").getAsInt();
            String name_line = line.getAsJsonObject().get("NOM_LINIA").getAsString();

            //Per cada línia es filtra segons l'id d'aquesta i les dates corresponents per averiguar les estacions inaugurades aquell any
            JsonObject stationsApiContent = parser.parse(
                    TMB.callAPI("transit/linies/metro/" + id_line + "/estacions",
                                "&filter=DATA_INAUGURACIO<='" + year + "-12-30Z'&filter=DATA_INAUGURACIO>='" + year + "-01-01Z'"))
                    .getAsJsonObject();
            JsonArray stations = stationsApiContent.getAsJsonArray("features");
            //Es recorre totes les estacions filtrades per l'any i es va emmagatzemant dintre de la llista tant l'estació com la línia
            for (JsonElement st : stations) {
                String stationName = st.getAsJsonObject().getAsJsonObject("properties").get("NOM_ESTACIO").getAsString();
                int stationCode = st.getAsJsonObject().getAsJsonObject("properties").get("CODI_GRUP_ESTACIO").getAsInt();

                //Es busca si aquesta estació ja ha estat afegida a la llista o no (per no repetir-la)
                boolean containsStation = false;
                for (Station s : stations_aux) {
                    //Si ja s'havia afegit la estació, s'afegeix la línia a la estació
                    if (s.getName().equals(stationName)) {
                        containsStation = true;
                        s.getLines().add(new Line(name_line));
                    }
                }
                //Si no s'havia afegit l'estació, s'afegeix aquesta junt amb la línia mencionada
                if (!containsStation) {
                    LinkedList<Line> ln = new LinkedList<>();
                    ln.add(new Line(name_line));
                    stations_aux.add(new Station(stationName, stationCode, "METRO", ln));
                }
            }
        }

        return stations_aux;
    }

    /**
     * Recull les línies de bus d'una parada i el temps que triguen en arribar
     * @param stopCode Codi de parada
     * @return Llista de linies ordenada per temps
     * @throws APIAccessException   En cas que el número de parada no sigui correcte
     */
    public LinkedList<Line> getNextLinesTimeFrom(int stopCode) throws APIAccessException {
        JsonParser parser = new JsonParser();
        JsonObject iBusApiContent = parser.parse(TMB.callAPI("ibus/stops/" + stopCode, "")).getAsJsonObject();
        if (iBusApiContent.has("code")) {
            throw new APIAccessException("Error, codi de parada no vàlid!");
        }
        JsonArray iBusInfo = iBusApiContent.getAsJsonObject("data").getAsJsonArray("ibus");
        if (iBusInfo.toString().equals("[]")) {
            throw new APIAccessException("Error, codi de parada no vàlid!");
        }
        LinkedList<Line> lines = new LinkedList<>();
        for (JsonElement info : iBusInfo) {
            int time_s =info.getAsJsonObject().get("t-in-s").getAsInt();

            String lineName = info.getAsJsonObject().get("line").getAsString();
            String lineDestination = info.getAsJsonObject().get("destination").getAsString();
            String time = info.getAsJsonObject().get("text-ca").getAsString();
            lines.add(new Line(lineName, lineDestination, time, time_s));
        }
        lines.sort(Comparator.comparingInt(Line::getTimeInS));
        return lines;
    }

    /**
     *  Comprova si la parada és preferida o no
     * @param code  Codi de la parada a comprovar
     * @return  Cert si es preferida
     */
    public boolean isStarredLocation(int code) {
        JsonParser parser = new JsonParser();
        JsonObject stopApiContent = parser.parse(TMB.callAPI( "transit/parades",
                "&filter=CODI_PARADA=" + code)).getAsJsonObject();
        JsonArray coordStopJson = stopApiContent.getAsJsonArray("features").get(0).getAsJsonObject()
                .getAsJsonObject("geometry").getAsJsonArray("coordinates");
        double[] coordStop = getCoordinatesFromJson(coordStopJson);
        for (StarredLocation sl : this.getUser().getStarredLocations()) {
            if (Function.getDistance(sl.getLocation().getCoordinates(), coordStop) <= 500) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna la ruta óptima segons les dades demanades
     * @param originCoord   Coordenades origen
     * @param destinationCoord  Coordenades destí
     * @param originName    Nom de l'origen
     * @param destinationName   Nom del destí
     * @param isArrival Arribada o sortida
     * @param date  Data
     * @param hour  Hora
     * @param maxWalkingDistance    Distància màxima de caminar
     * @return  Ruta calculada
     * @throws APIAccessException   En cas que no existeixi aquesta ruta
     */
    public Route getRoute(String originCoord, String destinationCoord, String originName, String destinationName,
                          boolean isArrival, String date, String hour, int maxWalkingDistance) throws APIAccessException {
        JsonParser parser = new JsonParser();
        String filters = "&fromPlace=" + originCoord + "&toPlace=" + destinationCoord +
                        "&date=" + date + "&time=" + hour + "&arriveBy=" + isArrival +
                        "&maxWalkDistance=" + maxWalkingDistance + "&mode=TRANSIT,WALK&showIntermediateStops=false";
        JsonObject routesApiContent = parser.parse(TMB.callAPI( "planner/plan", filters)).getAsJsonObject();

        if (routesApiContent.has("status")) {
            throw new APIAccessException("TMB està fent tot el possible perquè el bus i el metro facin aquesta ruta en un futur.");
        }
        JsonObject route = routesApiContent.getAsJsonObject("plan").getAsJsonArray("itineraries").get(0).getAsJsonObject();

        int totalTime = route.get("duration").getAsInt();
        totalTime = Function.toMinutes(totalTime);

        LinkedList<Step> steps_aux = new LinkedList<>();

        for (JsonElement e : route.getAsJsonArray("legs")) {
            JsonObject step = e.getAsJsonObject();
            Step step_aux;
            String mode = step.get("mode").getAsString();
            int duration = step.get("duration").getAsInt();
            duration = Function.toMinutes(duration);

            if (mode.equals("WALK")) {
                step_aux = new Step(mode, duration);
                steps_aux.add(step_aux);
            } else if (mode.equals("SUBWAY") || mode.equals("BUS")) {
                JsonObject from = step.getAsJsonObject("from");
                JsonObject to = step.getAsJsonObject("to");
                Station start = new Station(from.get("name").getAsString(),
                                            from.get("stopCode").getAsInt());
                Station end = new Station(to.get("name").getAsString(),
                                            to.get("stopCode").getAsInt());
                Line line = new Line(step.get("route").getAsString());
                step_aux = new Step(mode, duration, line, start, end);
                steps_aux.add(step_aux);
            }
        }

        return new Route(originName, destinationName, isArrival, date, hour, maxWalkingDistance, totalTime, steps_aux);
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Location l : locations) {
            str.append(l.toString()).append("\n");
        }
        return "Model.DataModel{\n" +
                str +
                '}';
    }
}
