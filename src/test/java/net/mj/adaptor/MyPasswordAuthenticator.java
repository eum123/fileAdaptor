package net.mj.adaptor;

import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class MyPasswordAuthenticator implements PasswordAuthenticator {
	public boolean authenticate(String username, String password, ServerSession session) {
        boolean retour = false;

        if ("login".equals(username) && "11".equals(password)) {
            retour = true;
        }

        return retour;
    }
}
