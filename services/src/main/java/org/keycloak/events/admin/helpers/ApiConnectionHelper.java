package org.keycloak.events.admin.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import org.keycloak.events.admin.helpers.pagerduty.Users;
import org.keycloak.events.admin.helpers.pagerduty.User;

public class ApiConnectionHelper {
    private String pagerdutyUrl = "https://api.pagerduty.com/users";
		
		//convert json string to object
		
    public String deleteExternalUser(String userEmail, String service) {
        HttpURLConnection httpClient = null;
        if(service == "pagerduty"){
            try {          
                User user = fetchPagerDutyUser(userEmail, httpClient);
                return deletePagerDutyUser(user.getId(), httpClient);
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

    public String updateExternalUser(String userEmail, String service, String userRole) {
        HttpURLConnection httpClient = null;
        if(service == "pagerduty"){
            try {          
                User user = fetchPagerDutyUser(userEmail, httpClient);
                return updatePagerDutyUser(user, httpClient, userRole);
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
        return "No user-update follow-up trigger for SP: "+service;
    }

    private String updatePagerDutyUser(User user, HttpURLConnection httpClient, String userRole) throws IOException {
        httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"/"+user.getId()).openConnection();
        OutputStreamWriter writer = new OutputStreamWriter(httpClient.getOutputStream());
        String jsonBody = "{\"user\":{\"type\": \"user\",\"name\": \""
            +user.getName()+
            "\",\"email\": \""
            +user.getEmail()+
            "\",\"role\": \""
            +userRole+
        "\"}}";
        writer.write(jsonBody);
        httpClient.setRequestMethod("PUT");
        return getResponse(httpClient);
    }

    private String deletePagerDutyUser(String userId, HttpURLConnection httpClient) throws IOException{
        httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"/"+userId).openConnection();
        httpClient.setRequestMethod("DELETE");  
        return getResponse(httpClient);
    }

    private User fetchPagerDutyUser(String userEmail, HttpURLConnection httpClient) throws IOException{
        httpClient = (HttpURLConnection) new URL(pagerdutyUrl+"?query="+userEmail)
            .openConnection();
        httpClient.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        String jsonResponse = getResponse(httpClient);
        Users userList = mapper.readValue(jsonResponse, Users.class);
        
        return userList.getUsers().get(0);
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