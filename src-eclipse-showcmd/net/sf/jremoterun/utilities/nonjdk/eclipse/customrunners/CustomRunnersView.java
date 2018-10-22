package net.sf.jremoterun.utilities.nonjdk.eclipse.customrunners;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities;
import net.sf.jremoterun.utilities.OsInegrationClientI;
import net.sf.jremoterun.utilities.classpath.JrrGroovyScriptRunner;

// <extension point="org.eclipse.ui.views">
//   <view class="net.sf.jremoterun.utilities.nonjdk.eclipse.customrunners.CustomRunnersView"
//         id="net.sf.jremoterun.utilities.nonjdk.eclipse.customrunners.CustomRunnersView"
//         name="CustomRunnersViewV">
//   </view>
// </extension>
public class CustomRunnersView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = CustomRunnersView.class.getName();

	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	public static Composite viewer;

	public static Composite parentView;

	@Override
	public void createPartControl(Composite parent) {
		parentView = parent;
		log.info("Runner dir : " + runnersDir);
		boolean dirSet = runnersDir != null;
		if (dirSet) {
			refresh3();
		}

	}

	public static File runnersDir;

	public static void setRunnerDirAndRefresh(File runnerDir2) throws Exception {
		JrrUtilities.checkFileExist(runnerDir2);
		runnersDir = runnerDir2;
		log.info("do refresh " + runnerDir2);
		boolean viewSet = parentView != null;
		log.info("view set ? " + viewSet);
		if (viewSet) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					refresh3();

				}
			});
		}
	}

	public static JrrGroovyScriptRunner jrrGroovyScriptRunner = new JrrGroovyScriptRunner();
	public static OsInegrationClientI osInegrationClient;

	static void addRefershButton(Composite panel2) {
		Button refreshB = new Button(panel2, SWT.PUSH);
		refreshB.setText("Refresh");
//		panel2.add(openFileButton);
		refreshB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshB.setEnabled(false);
				try {
					refresh3();
				} finally {
					refreshB.setEnabled(true);
				}
			}
		});
	}

	static void refresh3() {
		try {
			if (viewer != null) {
				log.info("removing prev view ..");
				viewer.dispose();
				viewer = null;
			}
			log.info("creating view ..");
			viewer = new Composite(parentView, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			viewer.setLayout(new RowLayout());
			refresh(viewer);
			viewer.pack();
		} catch (Throwable e1) {
			log.info(" " + e1);
			JrrUtilities.showException("Failed refresh", e1);
		}
	}

	static void refresh(Composite panel2) {
//		panel2.removeAll();
		addRefershButton(panel2);
		File[] listFiles = runnersDir.listFiles();
		log.info("found files : " + listFiles.length + " : " + Arrays.toString(listFiles));

		for (final File file : listFiles) {
			if (file.isFile() && file.getName().endsWith(".groovy")) {
				log.info("creating button for : " + file.getName());
				createActionButton(panel2, file);
				Button openFileButton = new Button(panel2, SWT.PUSH);
				openFileButton.setText("O");
//				panel2.add(openFileButton);
				openFileButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							log.info("opening file : " + file);
							if(osInegrationClient==null) {
								throw new NullPointerException("osInegrationClient is null");
							}
							osInegrationClient.openFile(file, null);
						} catch (Throwable e1) {
							log.info(file.getName() + " " + e1);
							JrrUtilities.showException(file.getName(), e1);
						}
					}
				});

			}
		}
	}

	static Button createActionButton(Composite parent, final File f) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(f.getName().replace(".groovy", ""));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (osInegrationClient != null) {
						osInegrationClient.saveAllEditors();
					}
					button.setEnabled(false);
					Thread thread = new Thread("${f.name} custom runner") {
						public void run() {
							try {
								log.info("file " + f.getName() + " calling ..");
								Class clazz = jrrGroovyScriptRunner.createScriptClass(
										FileUtils.readFileToString(f, Charset.defaultCharset()), f.getName());
								Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
								Object instance = clazz.newInstance();
								JrrClassUtils.invokeJavaMethod(instance, "run");
								log.info("file " + f.getName() + " called");
							} catch (Throwable e) {
								log.info(f.getName() + " " + e);
								JrrUtilities.showException(f.getName(), e);
							} finally {
								Display.getDefault().asyncExec(new Runnable() {

									@Override
									public void run() {
										button.setEnabled(true);
									}
								});
							}
						}
					};
					thread.start();
				} catch (Throwable e2) {
					log.info(f.getName() + " " + e2);
					JrrUtilities.showException(f.getName(), e2);
				}
			}
		});
		return button;
	}

	@Override
	public void setFocus() {
		// log.info("request focus");
		viewer.setFocus();
	}
}
