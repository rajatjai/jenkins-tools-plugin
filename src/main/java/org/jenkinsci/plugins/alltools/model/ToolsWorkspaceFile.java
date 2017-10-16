/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package org.jenkinsci.plugins.alltools.model;

import hudson.model.AbstractBuild;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.alltools.ToolsDiffState;

import java.io.File;
import java.io.Serializable;

public class ToolsWorkspaceFile implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Subdirectory of build directory to store the workspace files.
     */
    public static final String DIR_WORKSPACE_FILES = "workspace-files";

    private String fileName;

    private ToolsFile cppcheckFile;

    /**
     * Useful for files that are not found on the build file system
     */
    private boolean sourceIgnored;

    /**
     * State of compare. It is a runtime parameter, don't store it anywhere
     * (transient).
     * 
     * @since 1.15
     */
    private transient ToolsDiffState diffState = null;

    public ToolsWorkspaceFile(File file) {
        if (file != null)
            this.fileName = file.getAbsolutePath().replace('\\', '/');
    }

    public ToolsWorkspaceFile() {

    }


    public ToolsFile getCppcheckFile() {
        return cppcheckFile;
    }


    public void setCppcheckFile(ToolsFile cppcheckFile) {
        this.cppcheckFile = cppcheckFile;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (fileName == null) {
            setSourceIgnored(true);
        }
    }


    /**
     * Returns a file name for a temporary file that will hold the contents of the source.
     *
     * @return the temporary name
     */
    public String getTempName() {
        return Integer.toHexString(getFileName().hashCode()) + ".tmp";
    }

    /**
     * Returns the name of this file.
     *
     * @return the name of this file
     */
    public final String getFileName() {
        return fileName;
    }

    public String getTempName(final AbstractBuild<?, ?> owner) {
        if (fileName != null) {
            return owner.getRootDir().getAbsolutePath() + "/"
                    + DIR_WORKSPACE_FILES + "/"
                    + Integer.toHexString(fileName.hashCode()) + ".tmp";
        }
        return StringUtils.EMPTY;
    }

    public boolean isSourceIgnored() {
        return sourceIgnored;
    }

    public void setSourceIgnored(boolean sourceIgnored) {
        this.sourceIgnored = sourceIgnored;
    }

    public ToolsDiffState getDiffState() {
        return diffState;
    }

    public void setDiffState(ToolsDiffState diffState) {
        this.diffState = diffState;
    }
}
