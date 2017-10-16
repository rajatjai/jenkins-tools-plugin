package org.jenkinsci.plugins.alltools;

import hudson.FilePath;
import hudson.model.BuildListener;

import org.jenkinsci.plugins.alltools.model.ToolsFile;
import org.jenkinsci.plugins.alltools.model.ToolsWorkspaceFile;
import org.jenkinsci.plugins.alltools.util.ToolsLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class ToolsSourceContainer {

    private Map<Integer, ToolsWorkspaceFile> internalMap = new HashMap<Integer, ToolsWorkspaceFile>();

    public ToolsSourceContainer(Map<Integer, ToolsWorkspaceFile> internalMap) {
        this.internalMap = internalMap;
    }

    public ToolsSourceContainer(BuildListener listener,
                                   FilePath workspace,
                                   FilePath scmRootDir,
                                   List<ToolsFile> files) throws IOException, InterruptedException {

        int key = 1;
        for (ToolsFile cppcheckFile : files) {
            ToolsWorkspaceFile cppcheckWorkspaceFile = getCppcheckWorkspaceFile(listener, workspace, scmRootDir, cppcheckFile);
            //The key must be unique for all the files/errors through the merge
            cppcheckFile.setKey(key);
            cppcheckWorkspaceFile.setCppcheckFile(cppcheckFile);
            internalMap.put(key, cppcheckWorkspaceFile);
            ++key;
        }
    }

    private ToolsWorkspaceFile getCppcheckWorkspaceFile(BuildListener listener,
                                                           FilePath workspace,
                                                           FilePath scmRootDir,
                                                           ToolsFile cppcheckFile) throws IOException, InterruptedException {

        String cppcheckFileName = cppcheckFile.getFileName();

        if (cppcheckFileName == null) {
            ToolsWorkspaceFile cppcheckWorkspaceFile = new ToolsWorkspaceFile();
            cppcheckWorkspaceFile.setFileName(null);
            cppcheckWorkspaceFile.setSourceIgnored(true);
            return cppcheckWorkspaceFile;
        }

        ToolsWorkspaceFile cppcheckWorkspaceFile = new ToolsWorkspaceFile();
        FilePath sourceFilePath = getSourceFile(workspace, scmRootDir, cppcheckFileName);
        if (!sourceFilePath.exists()) {
            ToolsLogger.log(listener, "[WARNING] - The source file '" + sourceFilePath.toURI() + "' doesn't exist on the slave. The ability to display its source code has been removed.");
            cppcheckWorkspaceFile.setFileName(null);
            cppcheckWorkspaceFile.setSourceIgnored(true);
        } else if (sourceFilePath.isDirectory()) {
            cppcheckWorkspaceFile.setFileName(sourceFilePath.getRemote());
            cppcheckWorkspaceFile.setSourceIgnored(true);
        } else {
            cppcheckWorkspaceFile.setFileName(sourceFilePath.getRemote());
            cppcheckWorkspaceFile.setSourceIgnored(false);
        }

        return cppcheckWorkspaceFile;
    }

    private FilePath getSourceFile(FilePath workspace, FilePath scmRootDir, String cppcheckFileName) throws IOException, InterruptedException {
        FilePath sourceFilePath = new FilePath(scmRootDir, cppcheckFileName);
        if (!sourceFilePath.exists()) {
            //try from workspace
            sourceFilePath = new FilePath(workspace, cppcheckFileName);
        }
        return sourceFilePath;
    }


    public Map<Integer, ToolsWorkspaceFile> getInternalMap() {
        return internalMap;
    }

}
