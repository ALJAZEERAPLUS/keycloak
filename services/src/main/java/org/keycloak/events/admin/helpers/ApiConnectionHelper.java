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
        HttpURLConnection httpClient = null;
        if(service == "pagerduty"){
            try {          
                String userId = fetchPagerDutyUserId(userEmail, httpClient);
                return deletePagerDutyUser(userId, httpClient);
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
        return "No user-deletion follow-up trigger for SP: "+service;
    }

    private String deletePagerDutyUser(String userId, HttpURLConnection httpClient) throws IOException{
        httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"/"+userId).openConnection();
        httpClient.setRequestMethod("DELETE");  
        return getResponse(httpClient);
    }

    private String fetchPagerDutyUserId(String userEmail, HttpURLConnection httpClient) throws IOException{
        httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"?query="+userEmail)
            .openConnection();
        httpClient.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        String jsonResponse = getResponse(httpClient);
        Users userList = mapper.readValue(jsonResponse, Users.class);
        
        return userList.getUsers().get(0).getId();
    }

    private String getResponse(HttpURLConnection httpClient) throws IOException {
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept", "application/vnd.pagerduty+json;version=2");
        httpClient.setRequestProperty("Content-Type", "application/json");
        httpClient.setRequestProperty("Authorization", "Token token="+System.getenv("pagerduty_token"));
        BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }
}