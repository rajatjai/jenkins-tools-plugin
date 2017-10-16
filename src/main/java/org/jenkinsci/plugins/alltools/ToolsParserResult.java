package org.jenkinsci.plugins.alltools;


import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.jenkinsci.plugins.alltools.parser.ToolsParser;
import org.jenkinsci.plugins.alltools.util.ToolsLogger;

import java.io.File;
import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class ToolsParserResult implements FilePath.FileCallable<ToolsReport> {

    private static final long serialVersionUID = 1L;

    private final BuildListener listener;

    private final String toolsReportPattern;

    private final boolean ignoreBlankFiles;

    public static final String DELAULT_REPORT_MAVEN = "**Errors**";

    public ToolsParserResult(final BuildListener listener, String toolsReportPattern, boolean ignoreBlankFiles) {

        if (toolsReportPattern == null) {
            toolsReportPattern = DELAULT_REPORT_MAVEN;
        }

        if (toolsReportPattern.trim().length() == 0) {
            toolsReportPattern = DELAULT_REPORT_MAVEN;
        }

        this.listener = listener;
        this.toolsReportPattern = toolsReportPattern;
        this.ignoreBlankFiles = ignoreBlankFiles;
    }

    public ToolsReport invoke(java.io.File basedir, VirtualChannel channel) throws IOException {

        ToolsReport toolReportResult = new ToolsReport();
        try {
            String[] toolsReportFiles = findToolsReports(basedir);
            if (toolsReportFiles.length == 0) {
                String msg = "No tools test report file(s) were found with the pattern '"
                        + toolsReportPattern + "' relative to '"
                        + basedir + "'."
                        + "  Did you enter a pattern relative to the correct directory?"
                        + "  Did you generate the report(s)?";
                ToolsLogger.log(listener, msg);
                throw new IllegalArgumentException(msg);
            }

            ToolsLogger.log(listener, "Processing " + toolsReportFiles.length + " files with the pattern '" + toolsReportPattern + "'.");

            for (String toolsReportFileName : toolsReportFiles) {
                ToolsReport toolReport = new ToolsParser().parse(new File(basedir, toolsReportFileName), listener);
                ToolsLogger.log(listener, "Merging reports..");
                mergeReport(toolReportResult, toolReport);
            }
        } catch (Exception e) {
            ToolsLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
            return null;
        }

        return toolReportResult;
    }


    private static void mergeReport(ToolsReport toolsReportResult, ToolsReport toolsReport) {
        toolsReportResult.getErrorSeverityList().addAll(toolsReport.getErrorSeverityList());
        toolsReportResult.getWarningSeverityList().addAll(toolsReport.getWarningSeverityList());
        toolsReportResult.getStyleSeverityList().addAll(toolsReport.getStyleSeverityList());
        toolsReportResult.getPerformanceSeverityList().addAll(toolsReport.getPerformanceSeverityList());
        toolsReportResult.getInformationSeverityList().addAll(toolsReport.getInformationSeverityList());
        toolsReportResult.getNoCategorySeverityList().addAll(toolsReport.getNoCategorySeverityList());
        toolsReportResult.getPortabilitySeverityList().addAll(toolsReport.getPortabilitySeverityList());
        toolsReportResult.getFalseAlarmSeverityList().addAll(toolsReport.getFalseAlarmSeverityList());
        toolsReportResult.getAllErrors().addAll(toolsReport.getAllErrors());
        toolsReportResult.getVersions().add(toolsReport.getVersion());
    }

    /**
     * Return all tools report files
     *
     * @param parentPath parent
     * @return an array of strings
     */
    private String[] findToolsReports(File parentPath) {
        FileSet fs = Util.createFileSet(parentPath, this.toolsReportPattern);
        if (this.ignoreBlankFiles) {
            fs.add(new FileSelector() {
                public boolean isSelected(File basedir, String filename, File file) throws BuildException {
                    return file != null && file.length() != 0;
                }
            });
        }
        DirectoryScanner ds = fs.getDirectoryScanner();
        return ds.getIncludedFiles();
    }

    public String getCppcheckReportPattern() {
        return toolsReportPattern;
    }
}
