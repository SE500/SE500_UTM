package org.eclipse.utm;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.utm.UML2JavaMessages;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class UTMActivator extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.eclipse.utm"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static UTMActivator plugin;

	/**
	 * The plug-in Job Family Constant
	 */
	public static final String UTM_JOB_FAMILY = "org.eclipse.utm.jobfamily";
	
	/** 
	 *  The plug-in Console Name
	 */
	public static final String UTM_CONSOLE_NAME = "UML Trace Magic - Console";
	
	/**
	 * The plug-in Console
	 */
	public static final MessageConsole utmConsole = findConsole(UTM_CONSOLE_NAME);
	
	/**
	 * 	The Console Output Stream for the plug-in
	 */
	public static final MessageConsoleStream out = utmConsole.newMessageStream();
	
	/**
	 * 	The debug option key for debug tracing within .options
	 */
	private static final String DEBUG_OPTION = PLUGIN_ID + "/debug";
	
	/**
	 * 	The debug flag obtained from the .options file
	 */
	public static final String DEBUG = Platform.getDebugOption(DEBUG_OPTION); 

	/**
	 * The constructor
	 */
	public UTMActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		IJobManager jobMan = Job.getJobManager();
		jobMan.cancel(UTM_JOB_FAMILY);
		jobMan.join(UTM_JOB_FAMILY, null);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UTMActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Trace an Exception in the error log.
	 * 
	 * @param e
	 *            Exception to log.
	 * @param blocker
	 *            <code>True</code> if the exception must be logged as error, <code>False</code> to log it as
	 *            a warning.
	 */
	public static void log(Exception e, boolean blocker) {
		if (e == null) {
			throw new NullPointerException(UML2JavaMessages.getString("UML2JavaPlugin.LoggingNullException")); //$NON-NLS-1$
		}

		if (getDefault() == null) {
			// We are out of eclipse. Prints the stack trace on standard error.
			// CHECKSTYLE:OFF
			e.printStackTrace();
			// CHECKSTYLE:ON
		} else if (e instanceof CoreException) {
			log(((CoreException)e).getStatus());
		} else if (e instanceof NullPointerException) {
			int severity = IStatus.WARNING;
			if (blocker) {
				severity = IStatus.ERROR;
			}
			log(new Status(severity, PLUGIN_ID, severity, UML2JavaMessages
					.getString("UML2JavaPlugin.RequiredElementNotFound"), e)); //$NON-NLS-1$
		} else {
			int severity = IStatus.WARNING;
			if (blocker) {
				severity = IStatus.ERROR;
			}
			log(new Status(severity, PLUGIN_ID, severity, e.getMessage(), e));
		}
	}

	/**
	 * Puts the given status in the error log view.
	 * 
	 * @param status
	 *            Error Status.
	 */
	public static void log(IStatus status) {
		// Eclipse platform displays NullPointer on standard error instead of throwing it.
		// We'll handle this by throwing it ourselves.
		if (status == null) {
			throw new NullPointerException(UML2JavaMessages.getString("UML2JavaPlugin.LoggingNullStats")); //$NON-NLS-1$
		}

		if (getDefault() != null) {
			getDefault().getLog().log(status);
		} else {
			// We are out of eclipse. Prints the message on standard error.
			// CHECKSTYLE:OFF
			System.err.println(status.getMessage());
			status.getException().printStackTrace();
			// CHECKSTYLE:ON
		}
	}

	/**
	 * Log an information.
	 * 
	 * @param information
	 *            the message to log.
	 */
	public static void log(String information) {
		if ("true".equalsIgnoreCase(DEBUG))		
			System.out.println(information);
		else {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					UTMActivator.out.println(information);
				}
			});
		}
	}
	
	/**
	 * The method will search through available consoles and find the
	 * plug-in specific console if it is not found it will be created
	 * @param name
	 * 		The name of the console to find/create
	 * @return
	 * 		Returns the Message Console associated with the name
	 */
	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
}
