package Model;



public class APIAccessException extends Exception {
    /**
     * Permet rastrejar el tipus d'excepció que es llença
     * @param message Missatge a mostrar
     */
    public APIAccessException(String message) {
        super(message);
    }
}
