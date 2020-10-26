package org.keycloak.events.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;
import org.keycloak.events.admin.helpers.ApiConnectionHelper;

public class AdminEventListenerProvider implements EventListenerProvider {

    private final Logger log;
    private RealmModel realm;
    private final KeycloakSession session;
    private ApiConnectionHelper httpHelper;
     
    public AdminEventListenerProvider(KeycloakSession session, Logger log) {
        this.session = session;
        this.log = log;
        this.realm = session.getContext().getRealm();
        this.httpHelper = new ApiConnectionHelper();   
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        log.info("## NEW ADMIN EVENT ##");
        log.info("-----------------------------------------------------------");
        log.info("Resource pathdd" + ": " + adminEvent.getResourcePath());
        log.info("Resource type" + ": " + adminEvent.getResourceType());
        log.info("Operation type" + ": " + adminEvent.getOperationType());
        log.info("-----------------------------------------------------------");
        if(adminEvent.getResourceType().equals(ResourceType.USER)
         && adminEvent.getOperationType().equals(OperationType.DELETE)){
            try {
                UserRepresentation user = JsonSerialization.readValue(
                    new ByteArrayInputStream(adminEvent.getRepresentation().getBytes()),
                    new TypeReference<UserRepresentation>() {
                });
                for (Map.Entry<String, List<String>> entry : user.getAttributes().entrySet()) {
                    if(entry.getKey() == "pagerduty"){
                        log.info(this.httpHelper.deleteExternalUser(user.getEmail(), "pagerduty"));
                    }
                    for(String item : entry.getValue()){
                        log.info(item);
                    }
                }                
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        } 
        if(adminEvent.getResourceType().equals(ResourceType.USER)
         && adminEvent.getOperationType().equals(OperationType.UPDATE)){
            try {
                UserRepresentation user = JsonSerialization.readValue(
                    new ByteArrayInputStream(adminEvent.getRepresentation().getBytes()),
                    new TypeReference<UserRepresentation>() {
                });
                for (Map.Entry<String, List<String>> entry : user.getAttributes().entrySet()) {
                    if(entry.getKey() == "pagerduty_role"){
                        for(String item : entry.getValue()){
                            log.info(this.httpHelper.updateExternalUser(user.getEmail(), "pagerduty", item));
                            log.info(item);
                        }
                    }
                    
                }                
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        } 
    }

    @Override
    public void onEvent(Event event) {
        // No action required on non-admin events
    }

    @Override
    public void close() {
        // Nothing to close
    }
}