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

public class RequireRoleAuthenticator implements Authenticator {
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
            if (user.hasRole(role)) {
                context.success();
                return;
            }
        }
        context.failure(AuthenticationFlowError.INVALID_USER);
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
