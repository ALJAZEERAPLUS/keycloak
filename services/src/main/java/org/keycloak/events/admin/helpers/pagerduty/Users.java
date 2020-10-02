package org.keycloak.events.admin.helpers.pagerduty;

//import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class Users {

    private ArrayList<User> users;

    public ArrayList<User> getUsers(){
        return this.users;
    }

    public void setUsers(ArrayList<User> users){
        this.users = users;
    }

    public Users() {
    }
}
