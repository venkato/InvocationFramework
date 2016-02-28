package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.OsInegrationClientI;
import org.fife.rsta.ac.java.ExternalMemberClickedListener;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class RstaOpenMember implements ExternalMemberClickedListener {
	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

	final OsInegrationClientI osInegrationClient;

	public RstaOpenMember(OsInegrationClientI osInegrationClient) {
		this.osInegrationClient = osInegrationClient;
	}

	public void openClass(String className) {
		log.info(className);
		try {
			osInegrationClient.openClass(className);
		} catch (Exception e) {
			log.log(Level.SEVERE, className, e);
		}

	}

	public void gotoMethodInClass(String className, MethodInfo methodInfo) {
		log.info(className);
		log.info(methodInfo.getReturnTypeFull()+" "+methodInfo.getReturnTypeString(false));
		try {
			osInegrationClient.openMethod(className, methodInfo.getName(), methodInfo.getParameterCount());
		} catch (Exception e) {
			log.log(Level.SEVERE, className, e);
		}
	}

	public void gotoFieldInClass(String className, FieldInfo fieldInfo) {
		log.info(className);
		log.info(fieldInfo.getTypeString(false)+" "+fieldInfo.getTypeString(true));
		try {
			osInegrationClient.openField(className, fieldInfo.getName());
		} catch (Exception e) {
			log.log(Level.SEVERE, className, e);
		}
	}

}
