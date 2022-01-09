package Model;

import java.util.LinkedList;

public class User {
    private String userName;
    private String email;
    private int yearBirth;
    private LinkedList<Location> createdLocations;
    private LinkedList<StarredLocation> starredLocations;
    private LinkedList<Location> searchedLocations;
    private LinkedList<Route> createdRoutes;

    public User (String userName, String email, int yearBirth){
        this.userName = userName;
        this.email = email;
        this.yearBirth = yearBirth;
        createdLocations = new LinkedList<>();
        starredLocations = new LinkedList<>();
        searchedLocations = new LinkedList<>();
        createdRoutes = new LinkedList<>();
    }

    public String getUserName() {
        return userName;
    }

    public int getYearBirth() {
        return yearBirth;
    }

    public LinkedList<Location> getCreatedLocations() {
        return createdLocations;
    }

    public LinkedList<Location> getSearchedLocations() {
        return searchedLocations;
    }

    public LinkedList<StarredLocation> getStarredLocations() {
        return starredLocations;
    }

    public LinkedList<Route> getCreatedRoutes() {
        return createdRoutes;
    }

    /***
     * Formata el text a guardar al fitxer json
     * @return  Text a guardar
     */
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", yearBirth=" + yearBirth +
                ", createdLocations=" + createdLocations +
                ", starredLocations=" + starredLocations +
                ", searchedLocations=" + searchedLocations +
                '}';
    }

}
