package net.mj.adaptor;

import java.security.PublicKey;

import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class MyPublickeyAuthenticator implements PublickeyAuthenticator {
	public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) {
        return false;
    }
}
