package net.sf.jremoterun.utilities.nonjdk.eclipse.svn.password;

import java.util.logging.Logger;

import org.apache.subversion.javahl.ClientException;
import org.apache.subversion.javahl.callback.ConflictResolverCallback;
import org.apache.subversion.javahl.callback.UserPasswordCallback;
import org.tmatesoft.svn.core.internal.wc.ISVNAuthenticationStorage;
import org.tmatesoft.svn.core.javahl17.SVNClientImpl;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class SvnClientJrr extends SVNClientImpl {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	
	
	
	
	@Override
	public void password(String password) {
		super.password(password);
	}
	
	@Override
	public void username(String username) {
		super.username(username);
	}
	
	
	@Override
	public void setPrompt(UserPasswordCallback prompt) {
		super.setPrompt(prompt);
	}
	
	
	@Override
	public void setConflictResolver(ConflictResolverCallback callback) {
		super.setConflictResolver(callback);
	}
	
	@Override
	public void setClientCredentialsStorage(ISVNAuthenticationStorage storage) {
		super.setClientCredentialsStorage(storage);
	}
	
	
	@Override
	public void setConfigDirectory(String configDir) throws ClientException {
		super.setConfigDirectory(configDir);
	}
	
	
	void setMyAuthenticationManager(){
		
	}
	
}
