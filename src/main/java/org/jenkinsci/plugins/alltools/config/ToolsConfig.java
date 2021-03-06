package org.jenkinsci.plugins.alltools.config;


import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class ToolsConfig implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private String pattern;
    private boolean ignoreBlankFiles;
    private boolean allowNoReport;
    private ToolsConfigSeverityEvaluation configSeverityEvaluation = new ToolsConfigSeverityEvaluation();
    private ToolsConfigGraph configGraph = new ToolsConfigGraph();

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setIgnoreBlankFiles(boolean ignoreBlankFiles) {
        this.ignoreBlankFiles = ignoreBlankFiles;
    }

    public void setAllowNoReport(boolean allowNoReport) {
        this.allowNoReport = allowNoReport;
    }

    public void setConfigSeverityEvaluation(ToolsConfigSeverityEvaluation configSeverityEvaluation) {
        this.configSeverityEvaluation = configSeverityEvaluation;
    }

    public void setConfigGraph(ToolsConfigGraph configGraph) {
        this.configGraph = configGraph;
    }

    public void setCppcheckReportPattern(String cppcheckReportPattern) {
        this.cppcheckReportPattern = cppcheckReportPattern;
    }

    public void setUseWorkspaceAsRootPath(boolean useWorkspaceAsRootPath) {
        this.useWorkspaceAsRootPath = useWorkspaceAsRootPath;
    }

    public String getPattern() {
        return pattern;
    }

    @Deprecated
    public String getCppcheckReportPattern() {
        return cppcheckReportPattern;
    }

    public boolean isUseWorkspaceAsRootPath() {
        return useWorkspaceAsRootPath;
    }

    public boolean isIgnoreBlankFiles() {
        return ignoreBlankFiles;
    }

    public boolean getAllowNoReport() {
        return allowNoReport;
    }

    public ToolsConfigSeverityEvaluation getConfigSeverityEvaluation() {
        return configSeverityEvaluation;
    }

    public ToolsConfigGraph getConfigGraph() {
        return configGraph;
    }

    /*
    Backward compatibility
     */
    private transient String cppcheckReportPattern;
    private transient boolean useWorkspaceAsRootPath;

    private Object readResolve() {
        if (this.cppcheckReportPattern != null) {
            this.pattern = cppcheckReportPattern;
        }
        return this;
    }
}
