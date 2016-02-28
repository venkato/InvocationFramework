package nik.git.forcepush.jrr;


import org.eclipse.ui.internal.ide.IDEWorkbenchErrorHandler;

import net.sf.jremoterun.utilities.jrrbean.JrrBeanMaker;

public class JrrCommonEclipseBean {

	

	public static JrrCommonEclipseBean bean = JrrBeanMaker
	.makeBeanAndRegisterMBeanNoEx(JrrCommonEclipseBean.class);
	

	IDEWorkbenchErrorHandler ideWorkbenchErrorHandler;
	
	public IDEWorkbenchErrorHandler getIdeWorkbenchErrorHandler() {
		return ideWorkbenchErrorHandler;
	}
	
	public void setIdeWorkbenchErrorHandler(IDEWorkbenchErrorHandler ideWorkbenchErrorHandler) {
		this.ideWorkbenchErrorHandler = ideWorkbenchErrorHandler;
	}
	
	
	
}
