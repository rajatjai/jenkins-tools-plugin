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
        for (ToolsFile toolFile : files) {
            ToolsWorkspaceFile toolWorkspaceFile = getToolWorkspaceFile(listener, workspace, scmRootDir, toolFile);
            //The key must be unique for all the files/errors through the merge
            toolFile.setKey(key);
            toolWorkspaceFile.setCppcheckFile(toolFile);
            internalMap.put(key, toolWorkspaceFile);
            ++key;
        }
    }

    private ToolsWorkspaceFile getToolWorkspaceFile(BuildListener listener,
                                                           FilePath workspace,
                                                           FilePath scmRootDir,
                                                           ToolsFile toolFile) throws IOException, InterruptedException {

        String toolFileName = toolFile.getFileName();

        if (toolFileName == null) {
            ToolsWorkspaceFile toolWorkspaceFile = new ToolsWorkspaceFile();
            toolWorkspaceFile.setFileName(null);
            toolWorkspaceFile.setSourceIgnored(true);
            return toolWorkspaceFile;
        }

        ToolsWorkspaceFile toolWorkspaceFile = new ToolsWorkspaceFile();
        FilePath sourceFilePath = getSourceFile(workspace, scmRootDir, toolFileName);
        if (!sourceFilePath.exists()) {
            ToolsLogger.log(listener, "[WARNING] - The source file '" + sourceFilePath.toURI() + "' doesn't exist on the slave. The ability to display its source code has been removed.");
            toolWorkspaceFile.setFileName(null);
            toolWorkspaceFile.setSourceIgnored(true);
        } else if (sourceFilePath.isDirectory()) {
            toolWorkspaceFile.setFileName(sourceFilePath.getRemote());
            toolWorkspaceFile.setSourceIgnored(true);
        } else {
            toolWorkspaceFile.setFileName(sourceFilePath.getRemote());
            toolWorkspaceFile.setSourceIgnored(false);
        }

        return toolWorkspaceFile;
    }

    private FilePath getSourceFile(FilePath workspace, FilePath scmRootDir, String toolFileName) throws IOException, InterruptedException {
        FilePath sourceFilePath = new FilePath(scmRootDir, toolFileName);
        if (!sourceFilePath.exists()) {
            //try from workspace
            sourceFilePath = new FilePath(workspace, toolFileName);
        }
        return sourceFilePath;
    }


    public Map<Integer, ToolsWorkspaceFile> getInternalMap() {
        return internalMap;
    }

}
