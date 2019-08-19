package net.mj.adaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.junit.Test;

public class SFTPServer {
	@Test
	public void test() {
		// Init sftp server stuff
		SshServer sshd = SshServer.setUpDefaultServer();		
		sshd.setPort(22);
        sshd.setPasswordAuthenticator(new MyPasswordAuthenticator());
        sshd.setPublickeyAuthenticator(new MyPublickeyAuthenticator());
        sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
//        sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystemFactory.Builder().build()));
//        sshd.setCommandFactory(new ScpCommandFactory.Builder().build());

        sshd.setCommandFactory(new ScpCommandFactory());

    	List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
    	namedFactoryList.add(new SftpSubsystemFactory.Builder().build());
    	sshd.setSubsystemFactories(namedFactoryList);

    	sshd.setFileSystemFactory(new FileSystemFactory() {

			public FileSystem createFileSystem(Session session) throws IOException {
				Map<String, String> roots = new HashMap();
				
				roots.put("/", "/Work/Temp/Dest");
				return FileSystems.getDefault();
//				return new NativeFileSystemView("brad", roots, "/");
			}
    		
    	});

    	try {
    		sshd.start();
    		
    		Thread.sleep(600000);
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
    
	}
	
	@Test
	public void server1() throws Exception {
		SshServer sshd = SshServer.setUpDefaultServer();		
		sshd.setPort(22);
		SftpSubsystemFactory factory = new SftpSubsystemFactory();

	    @SuppressWarnings("unchecked")
	    List<NamedFactory<Command>> factoryList = Arrays.<NamedFactory<Command>> asList(new NamedFactory[] {factory});
	    sshd.setSubsystemFactories(factoryList);

	    sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
	        public boolean authenticate(String tryUsername, String tryPassword, ServerSession session) {
	            return ("login".equals(tryUsername)) && ("11".equals(tryPassword));
	        }

	    });
	    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
	   

	    VirtualFileSystemFactory vfSysFactory = new VirtualFileSystemFactory();
	    vfSysFactory.setDefaultHomeDir(new File("D:\\").toPath());
	    sshd.setFileSystemFactory(vfSysFactory);
	    
	    sshd.start();
	    
	    Thread.sleep(600000);

	    sshd.stop();
	}
	
	@Test
	public void server2() throws Exception {
		SshServer sshd = SshServer.setUpDefaultServer();		
		sshd.setPort(22);
		SftpSubsystemFactory factory = new SftpSubsystemFactory();

	    @SuppressWarnings("unchecked")
	    List<NamedFactory<Command>> factoryList = Arrays.<NamedFactory<Command>> asList(new NamedFactory[] {factory});
	    sshd.setSubsystemFactories(factoryList);

	    sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
	        public boolean authenticate(String tryUsername, String tryPassword, ServerSession session) {
	            return ("login1".equals(tryUsername)) && ("11".equals(tryPassword)) ||  ("login2".equals(tryUsername)) && ("11".equals(tryPassword));
	        }

	    });
	    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
	   

	    //사용자별로 root directory 적용
	    VirtualFileSystemFactory vfSysFactory = new VirtualFileSystemFactory();
	    vfSysFactory.setUserHomeDir("login1", new File("D:\\project").toPath());
	    vfSysFactory.setUserHomeDir("login2", new File("D:\\sw").toPath());
	    
	    sshd.setFileSystemFactory(vfSysFactory);
	    
	    sshd.start();
	    
	    Thread.sleep(600000);

	    sshd.stop();
	}
	
	/**
	 * 인증서 테스트
	 * @throws Exception
	 */
	@Test
	public void server3() throws Exception {
		SshServer sshd = SshServer.setUpDefaultServer();		
		sshd.setPort(22);
		
		//공개키
	    File file = new File("src/main/test/resources/rsa.pub");
	    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(file.toPath()));
	                                
	    System.out.println(file.getAbsolutePath());

	    List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
	    userAuthFactories.add(new UserAuthPublicKeyFactory());
	    sshd.setUserAuthFactories(userAuthFactories);

	    sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {
	        public boolean authenticate(String username, PublicKey key, ServerSession session) {
	            if ("login".equals(username)) {
	                return true;
	            }

	            return false;
	        }
	    });
	   
	    
	    sshd.setCommandFactory(new ScpCommandFactory());

	    List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
	    namedFactoryList.add(new SftpSubsystemFactory());
	    sshd.setSubsystemFactories(namedFactoryList);
	   

	    //사용자별로 root directory 적용
	    VirtualFileSystemFactory vfSysFactory = new VirtualFileSystemFactory();
	    vfSysFactory.setUserHomeDir("login", new File("D:\\project").toPath());
	    
	    
	    sshd.setFileSystemFactory(vfSysFactory);
	    
	    sshd.start();
	    
	    Thread.sleep(600000);

	    sshd.stop();
	}
}
