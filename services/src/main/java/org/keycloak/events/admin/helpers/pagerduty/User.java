package org.keycloak.events.admin.helpers.pagerduty;
//import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    
    private String id;

    private String name;

    private String email;

    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }

    public void setId(String id){
        this.id = id;
    } 
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public User() {
    }
}
