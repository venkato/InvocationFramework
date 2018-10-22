package nik.git.forcepush.jrr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.StreamSupport;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
//import org.eclipse.jgit.pgm.CommandRef;
//import org.eclipse.jgit.pgm.TextBuiltin;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate.Status;
import org.eclipse.jgit.util.io.ThrowingPrintWriter;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.JrrUtilities;
import net.sf.jremoterun.utilities.nonjdk.git.GitProgressMonitorJrr;
import net.sf.jremoterun.utilities.nonjdk.git.GitRemoteFind;
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils;

public class PushForceImpl2 implements IObjectActionDelegate {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(JrrClassUtils.getCurrentClass().getName());

	public static String myUserName = System.getProperty("user.name");
	public static boolean needSetMyRemote = true;
	public static GitProgressMonitorJrr progressMonitor = new GitProgressMonitorJrr();
	
	private IWorkbenchPart activePart;
	private File selectedRepositoryLocation;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		LOG.info("1");
		activePart = targetPart;
		updateActionEnablement(action);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		LOG.info("1");
		SelectedRepositoryComputer computer = new SelectedRepositoryComputer(selection, activePart);
		selectedRepositoryLocation = computer.compute();
		updateActionEnablement(action);
	}

	@Override
	public void run(IAction action) {
		LOG.info("1" + selectedRepositoryLocation);
		try {
			// Git git = Git.open(selectedRepositoryLocation);
			// Iterable<PushResult> call = git.push().setForce(true).call();
			forceFush(selectedRepositoryLocation);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			JrrUtilities.showException("Push force failed", e);
		}
	}

	private static void forceFush(File file) throws Exception {
		// CommandRef commandRef =
		// org.eclipse.jgit.pgm.CommandCatalog.get("push");
		// TextBuiltin create = commandRef.create();
		// create.subcommand;
		org.eclipse.jgit.api.Git git = Git.open(file);
		PushCommand push = git.push();
		push.setProgressMonitor(progressMonitor);
		if(needSetMyRemote) {
			push.setRemote(findMyRemote(file));
		}
		push.setForce(true);
		Iterable<PushResult> callResult = push.call();
		StringBuilder sb = new StringBuilder();

		// StreamSupport.stream(callResult.spliterator(),
		// false).map(PushResult::getRemoteUpdates).reduce((a, b) -> {
		// //a.
		// ArrayList<org.eclipse.jgit.transport.RemoteRefUpdate> al = new
		// ArrayList<>();
		// al.addAll(a);
		// al.addAll(b);
		// return al;
		// }).

		// StreamUtils.asStream(callResult);

		callResult.forEach((pr) -> {
			pr.getRemoteUpdates().forEach((rru) -> {
				sb.append("status = "+rru.getStatus()+ "\n"+rru.getMessage()+"\n");
			});
		});

		// StringWriter outwW = new StringWriter();
		// StringWriter errw2 = new StringWriter();
		// // StringWriter sw2 = new StringWriter();
		// org.eclipse.jgit.util.io.ThrowingPrintWriter errw = new
		// ThrowingPrintWriter(errw2);
		// org.eclipse.jgit.util.io.ThrowingPrintWriter outw = new
		// ThrowingPrintWriter(outwW);
		// ByteArrayOutputStream outs = new ByteArrayOutputStream();
		// ByteArrayOutputStream errs = new ByteArrayOutputStream();
		// JrrClassUtils.invokeMethod(create, "init", git.getRepository(),
		// file.getAbsolutePath());
		// JrrClassUtils.setFieldValue(create, "force", true);
		// JrrClassUtils.setFieldValue(create, "outs", outs);
		// JrrClassUtils.setFieldValue(create, "errs", errs);
		// JrrClassUtils.setFieldValue(create, "errw", errw);
		// JrrClassUtils.setFieldValue(create, "outw", outw);
		//
		// String[] args = {};
		// create.execute(args);
		// String output = "1: " + outwW.toString() + " 2: " + errw2.toString()
		// + " 3: " + new String(outs.toByteArray())
		// + " 4: " + new String(errs.toByteArray());
		// LOG.info(output);
		// test2
		MessageDialog.openWarning(null, "Git push force", file.getAbsolutePath() + " \n" + sb);
	}
	
	public static String findMyRemote(File file) {
		GitRepoUtils gitRepoUtils = new GitRepoUtils(file);
		GitRemoteFind gitRemoteFind = new GitRemoteFind() {

			@Override
			public boolean isRepoGood(String uri) {
				return uri.startsWith(myUserName);
			}
			
		};
		String remote = gitRemoteFind.detectRemote(gitRepoUtils);
		return remote;
	}

	private void updateActionEnablement(IAction action) {
		action.setEnabled(activePart != null && selectedRepositoryLocation != null);
	}
	

}
