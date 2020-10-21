package org.keycloak.authentication.authenticators.browser;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.jboss.logging.Logger;

public class RequireRoleAuthenticator implements Authenticator {
    private final Logger log = Logger.getLogger("org.keycloak.events");
    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        AuthenticatorConfigModel authConfig = context.getAuthenticatorConfig();
        if(user != null && authConfig!=null && authConfig.getConfig()!=null){
            String requiredRole = authConfig.getConfig().get(RequireRoleAuthenticatorFactory.CONDITIONAL_USER_ROLE);
            RoleModel role = KeycloakModelUtils.getRoleFromString(realm, requiredRole);
            user = removeExpiredRole(requiredRole, user, role);
            if (user.hasRole(role)) {
                context.success();
                return;
            }
        }
        context.failure(AuthenticationFlowError.INVALID_USER);
    }
    
    private UserModel removeExpiredRole(String requiredRole, UserModel user, RoleModel role){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        try{
            for(String attribute : user.getAttribute(requiredRole)){
                Date expirationDate = sdf.parse(attribute);
                if(today.compareTo(expirationDate) >= 0){
                    user.deleteRoleMapping(role);
                    user.removeAttribute(requiredRole);
                }
            }
        }catch(Exception e){
           log.error(e.getMessage());
        }
        return user;
    }

    @Override
    public void action(AuthenticationFlowContext context) {

    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {

    }
}
