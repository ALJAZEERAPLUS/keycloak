package org.keycloak.events.admin.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import org.keycloak.events.admin.helpers.pagerduty.Users;

public class ApiConnectionHelper {
    private String pagerdutyUrl = "https://api.pagerduty.com/users";
		
		//convert json string to object
		
    public String deleteExternalUser(String userEmail, String service) {
        if(service == "pagerduty"){
            return deletePagerDutyUser(userEmail);
        }
        return "No user-deletion follow-up trigger for SP: "+service;
    }

    private String deletePagerDutyUser(String userEmail){
        HttpURLConnection httpClient = null;
        try {
            httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"?query="+userEmail)
                .openConnection();

            //GET REQUEST FETCHING USER IDs
            httpClient.setRequestMethod("GET");
            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
            httpClient.setRequestProperty("Accept", "application/vnd.pagerduty+json;version=2");
            httpClient.setRequestProperty("Content-Type", "application/json");
            httpClient.setRequestProperty("Authorization", System.getenv("pagerduty_token"));

            BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String jsonResponse = response.toString();
            Users userList = mapper.readValue(jsonResponse, Users.class);
            
            String userId = userList.getUsers().get(0).getId();
            
            //DELETE REQUEST
            httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"/"+userId).openConnection();

            httpClient.setRequestMethod("DELETE");
            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
            httpClient.setRequestProperty("Accept", "application/vnd.pagerduty+json;version=2");
            httpClient.setRequestProperty("Content-Type", "application/json");
            httpClient.setRequestProperty("Authorization", System.getenv("pagerduty_token"));
            in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
            response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        }
        catch(IOException io){  
            return  io.toString();
        }
        finally {
            if (httpClient != null) {
                httpClient.disconnect();
            }
        }   
    }
}