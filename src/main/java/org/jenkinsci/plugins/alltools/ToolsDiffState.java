package org.jenkinsci.plugins.alltools;

import org.jenkinsci.plugins.alltools.Messages;

/**
 * Status of comparison of two reports.
 * 
 * Implementation note: The upper case of the constants and declaration order
 * are significant, the second one is used in sorting.
 * 
 * @see ToolsResult#diffCurrentAndPrevious(java.util.Set)
 * @author Michal Turek
 */
public enum ToolsDiffState {
    /** The issue is present only in the current report. */
    NEW {
        @Override
        public String getCss() {
            return "new";
        }

        @Override
        public String getText() {
            return Messages.tools_DiffNew();
        }
    },

    /** The issue is present only in the previous report. */
    SOLVED {
        @Override
        public String getCss() {
            return "solved";
        }

        @Override
        public String getText() {
            return Messages.tools_DiffSolved();
        }
    },

    /** The issue is present in both the current and the previous report. */
    UNCHANGED {
        @Override
        public String getCss() {
            return "unchanged";
        }

        @Override
        public String getText() {
            return Messages.tools_DiffUnchanged();
        }
    };

    /**
     * Get CSS class.
     * 
     * @return the class
     */
    public abstract String getCss();

    /**
     * Get localized text.
     * 
     * @return the localized text
     */
    public abstract String getText();
}
