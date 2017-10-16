package org.jenkinsci.plugins.alltools.util;

import hudson.model.BuildListener;

import java.io.Serializable;

/**
 * @author Rajat Jain
 */
public class ToolsLogger implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log output to the given logger, using the ToolsErrorVisualizer identifier
     *
     * @param listener The current listener
     * @param message  The message to be outputted
     */
    public static void log(BuildListener listener, final String message) {
        listener.getLogger().println("[ToolsErrorVisualizer] " + message);
    }

}