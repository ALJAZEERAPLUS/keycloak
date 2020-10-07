
package org.keycloak.events.admin;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.logging.Logger;

public class AdminEventListenerProviderFactory implements EventListenerProviderFactory,
    ServerInfoAwareProviderFactory {

    private static final Logger log = Logger.getLogger("org.keycloak.events");

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new AdminEventListenerProvider(session, log);
    }

    @Override
    public void init(Config.Scope scope) {
        //
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        //
    }

    @Override
    public void close() {
        //
    }

    @Override
    public String getId() {
        return "admin-listener";
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("Admin Event Listener", "It provides triggers that manage users on external systems. F.x: A users that gets deleted on Keycloak also gets deleted on PagerDuty");
        return ret;
    }
}