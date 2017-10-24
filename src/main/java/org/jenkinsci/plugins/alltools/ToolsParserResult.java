package org.jenkinsci.plugins.alltools;


import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.jenkinsci.plugins.alltools.model.ReportType;
import org.jenkinsci.plugins.alltools.parser.ToolsParser;
import org.jenkinsci.plugins.alltools.util.ToolsLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gregory Boissinot
 */
public class ToolsParserResult implements FilePath.FileCallable<Map<ReportType, ToolsReport>> {

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

    public Map<ReportType, ToolsReport> invoke(java.io.File basedir, VirtualChannel channel) throws IOException {

    	Map<ReportType, ToolsReport> toolReportResult = new HashMap<ReportType, ToolsReport>();
    	ToolsReport toolsReport = new ToolsReport();
    	ToolsReport refinedReport = new ToolsReport();
    	ToolsReport mergedReport = new ToolsReport();
    	toolReportResult.put(ReportType.TOOLS_ERRORS, toolsReport);
    	toolReportResult.put(ReportType.REFINED_ERRORS, refinedReport);
    	toolReportResult.put(ReportType.MERGED_ERRORS, mergedReport);
    	
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
                Map<ReportType, ToolsReport> toolReportMap = new ToolsParser().parse(new File(basedir, toolsReportFileName), listener);
                ToolsLogger.log(listener, "Merging reports..");
                mergeReport(toolReportResult, toolReportMap);
            }
        } catch (Exception e) {
            ToolsLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
            return null;
        }

        return toolReportResult;
    }


    private static void mergeReport(Map<ReportType, ToolsReport> toolReportResultMap, Map<ReportType, ToolsReport> toolReportCurrentMap) {
    	for (Entry<ReportType, ToolsReport> tool : toolReportCurrentMap.entrySet()) {
    		ToolsReport toolReportResult = toolReportResultMap.get(tool.getKey());
    		ToolsReport toolReport = tool.getValue();
    		toolReportResult.getErrorSeverityList().addAll(toolReport.getErrorSeverityList());
    		toolReportResult.getWarningSeverityList().addAll(toolReport.getWarningSeverityList());
    		toolReportResult.getStyleSeverityList().addAll(toolReport.getStyleSeverityList());
    		toolReportResult.getPerformanceSeverityList().addAll(toolReport.getPerformanceSeverityList());
    		toolReportResult.getInformationSeverityList().addAll(toolReport.getInformationSeverityList());
    		toolReportResult.getNoCategorySeverityList().addAll(toolReport.getNoCategorySeverityList());
    		toolReportResult.getPortabilitySeverityList().addAll(toolReport.getPortabilitySeverityList());
    		toolReportResult.getFalseAlarmSeverityList().addAll(toolReport.getFalseAlarmSeverityList());
    		toolReportResult.getAllErrors().addAll(toolReport.getAllErrors());
    		toolReportResult.getVersions().add(toolReport.getVersion());
		}
    	
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
