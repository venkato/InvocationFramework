package net.sf.jremoterun.utilities.nonjdk.eclipse.svn.password;

import java.util.logging.Logger;

import org.apache.subversion.javahl.ClientException;
import org.apache.subversion.javahl.callback.ConflictResolverCallback;
import org.apache.subversion.javahl.callback.UserPasswordCallback;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.ISVNAuthenticationStorage;
import org.tmatesoft.svn.core.javahl17.SVNClientImpl;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils;

public class SvnClientJrr extends SVNClientImpl {

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
	
	
	
	
	
	@Override
	public void password(String password) {
		super.password(password);
		//call setMyAuthenticationManager
	}
	
	@Override
	public void username(String username) {
		super.username(username);
		//call setMyAuthenticationManager
	}
	
	
	@Override
	public void setPrompt(UserPasswordCallback prompt) {
		super.setPrompt(prompt);
		//call setMyAuthenticationManager
	}
	
	
	@Override
	public void setConflictResolver(ConflictResolverCallback callback) {
		super.setConflictResolver(callback);
		//call setMyAuthenticationManager
	}
	
	@Override
	public void setClientCredentialsStorage(ISVNAuthenticationStorage storage) {
		super.setClientCredentialsStorage(storage);
		//call setMyAuthenticationManager
	}
	
	
	@Override
	public void setConfigDirectory(String configDir) throws ClientException {
		super.setConfigDirectory(configDir);
		//call setMyAuthenticationManager
	}
	
	
	public void setMyAuthenticationManager(String userName,String password) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		ISVNAuthenticationManager defaultAuthenticationManager = SVNWCUtil.createDefaultAuthenticationManager(null, userName, password.toCharArray(),null,null,false);
		JrrClassUtils.setFieldValue(this, "authenticationManager", defaultAuthenticationManager);
	}
	
	static void setCustomImpl() throws  Exception {
		Class clazz = SVNClientImpl.class;
		CtClass ctClass = JrrJavassistUtils.getClassFromDefaultPool(clazz);
		CtMethod method1 = JrrJavassistUtils.findMethod(clazz, ctClass, "newInstance",0);
		method1.setBody("return new SvnClientJrr()");
		
	}
}
