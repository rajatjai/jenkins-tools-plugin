package org.jenkinsci.plugins.alltools;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import org.jenkinsci.plugins.alltools.config.ToolsConfig;
import org.jenkinsci.plugins.alltools.config.ToolsConfigGraph;
import org.jenkinsci.plugins.alltools.config.ToolsConfigSeverityEvaluation;
import org.jenkinsci.plugins.alltools.model.ReportType;
import org.jenkinsci.plugins.alltools.model.ToolErrors;
import org.jenkinsci.plugins.alltools.model.ToolsFile;
import org.jenkinsci.plugins.alltools.model.ToolsWorkspaceFile;
import org.jenkinsci.plugins.alltools.util.ToolsBuildResultEvaluator;
import org.jenkinsci.plugins.alltools.util.ToolsLogger;
import org.jenkinsci.plugins.alltools.Messages;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gregory Boissinot
 */
public class ToolsPublisher extends Recorder {
    /**
     * XML file with source container data. Lazy loading instead of data in build.xml.
     * 
     * @since 1.15
     */
    public static final String XML_FILE_DETAILS = "cppcheck_details.xml";

    private ToolsConfig toolsConfig;

    @DataBoundConstructor
    public ToolsPublisher(String pattern,
                             boolean ignoreBlankFiles, String threshold,
                             boolean allowNoReport,
                             String newThreshold, String failureThreshold,
                             String newFailureThreshold, String healthy, String unHealthy,
                             boolean severityError,
                             boolean severityWarning,
                             boolean severityStyle,
                             boolean severityPerformance,
                             boolean severityInformation,
                             boolean severityNoCategory,
                             boolean severityPortability,
                             boolean severityFalseAlarm,
                             int xSize, int ySize,
                             int numBuildsInGraph,
                             boolean displayAllErrors,
                             boolean displayErrorSeverity,
                             boolean displayWarningSeverity,
                             boolean displayStyleSeverity,
                             boolean displayPerformanceSeverity,
                             boolean displayInformationSeverity,
                             boolean displayNoCategorySeverity,
                             boolean displayPortabilitySeverity,
                             boolean displayFalseAlarmSeverity) {

        toolsConfig = new ToolsConfig();

        toolsConfig.setPattern(pattern);
        toolsConfig.setAllowNoReport(allowNoReport);
        toolsConfig.setIgnoreBlankFiles(ignoreBlankFiles);
        ToolsConfigSeverityEvaluation configSeverityEvaluation = new ToolsConfigSeverityEvaluation(
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy, unHealthy,
                severityError,
                severityWarning,
                severityStyle,
                severityPerformance,
                severityInformation,
                severityNoCategory,
                severityPortability,
                severityFalseAlarm);
        toolsConfig.setConfigSeverityEvaluation(configSeverityEvaluation);
        ToolsConfigGraph configGraph = new ToolsConfigGraph(
                xSize, ySize, numBuildsInGraph,
                displayAllErrors,
                displayErrorSeverity,
                displayWarningSeverity,
                displayStyleSeverity,
                displayPerformanceSeverity,
                displayInformationSeverity,
                displayNoCategorySeverity,
                displayPortabilitySeverity,
                displayFalseAlarmSeverity);
        toolsConfig.setConfigGraph(configGraph);
    }


    public ToolsPublisher(ToolsConfig toolsConfig) {
        this.toolsConfig = toolsConfig;
    }

    public ToolsConfig getToolsConfig() {
        return toolsConfig;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new ToolsProjectAction(project, toolsConfig.getConfigGraph());
    }

    protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) throws InterruptedException, IOException {

        if (this.canContinue(build.getResult())) {
            ToolsLogger.log(listener, "Starting the tools analysis.");
            
            EnvVars env = build.getEnvironment(listener);
            String expandedPattern = env.expand(toolsConfig.getPattern());
            

            ToolsParserResult parser = new ToolsParserResult(listener,
            		expandedPattern, toolsConfig.isIgnoreBlankFiles());
            Map<ReportType, ToolsReport> toolsReportMap;
            try {
                toolsReportMap = build.getWorkspace().act(parser);
            } catch (Exception e) {
                ToolsLogger.log(listener, "Error on tools analysis: " + e);
                build.setResult(Result.FAILURE);
                return false;
            }

            if (toolsReportMap == null) {
                // Check if we're configured to allow not having a report
                if (toolsConfig.getAllowNoReport()) {
                    return true;
                } else {
                    build.setResult(Result.FAILURE);
                    return false;
                }
            }
            ToolsLogger.log(listener, "Tools report map retrieved.." + toolsReportMap.size() + "files data");

            ToolsConfigSeverityEvaluation severityEvaluation
            = toolsConfig.getConfigSeverityEvaluation();

            List<ToolsFile> allErrorFilesList = new ArrayList<ToolsFile>();
            List<ToolsResult> resultList = new ArrayList<ToolsResult>();
            List<Result> allBuildResults = new ArrayList<Result>();
            int totalHealthReportPercentage = 0;
            for(Entry<ReportType, ToolsReport> toolsReport: toolsReportMap.entrySet()) {

            	//Get all results
            	ToolsResult result = new ToolsResult(toolsReport.getKey().name(), toolsReport.getValue().getStatistics(), build);
            	resultList.add(result);
            	ToolsLogger.log(listener, "Tools report type = " + toolsReport.getKey() + " , total errors = " + result.getStatistics().getNumberTotal());

            	// Evaluate buildResult for all types of tool results
            	Result buildResult = new ToolsBuildResultEvaluator().evaluateBuildResult(
            			listener, result.getNumberErrorsAccordingConfiguration(severityEvaluation, false),
            			result.getNumberErrorsAccordingConfiguration(severityEvaluation, true),
            			severityEvaluation);
            	allBuildResults.add(buildResult);
            	
            	//Get health report percentage
            	totalHealthReportPercentage += ToolsBuildAction.computeHealthReportPercentage(result, severityEvaluation);
            	//Get all error files
            	allErrorFilesList.addAll(toolsReport.getValue().getAllErrors());
            }
            
            ToolsSourceContainer toolsSourceContainer
                    = new ToolsSourceContainer(listener, build.getWorkspace(),
                            build.getModuleRoot(), allErrorFilesList);

            Result combinedBuildResult = Result.SUCCESS;
            for(Result buildResult: allBuildResults) {
            	
            	if (buildResult != Result.SUCCESS) {
            		combinedBuildResult = buildResult;
            		break;
            	}
            }
            
            if (combinedBuildResult != Result.SUCCESS) {
            	build.setResult(combinedBuildResult);
            }
            ToolsBuildAction buildAction = new ToolsBuildAction(build, resultList, totalHealthReportPercentage);

            build.addAction(buildAction);

            XmlFile xmlSourceContainer = new XmlFile(new File(build.getRootDir(),
                    XML_FILE_DETAILS));
            xmlSourceContainer.write(toolsSourceContainer);

            copyFilesToBuildDirectory(build.getRootDir(), launcher.getChannel(),
                    toolsSourceContainer.getInternalMap().values());

            ToolsLogger.log(listener, "Ending the tools analysis.");
        }
        return true;
    }


    /**
     * Copies all the source files from the workspace to the build folder.
     *
     * @param rootDir      directory to store the copied files in
     * @param channel      channel to get the files from
     * @param sourcesFiles the sources files to be copied
     * @throws IOException                   if the files could not be written
     * @throws java.io.FileNotFoundException if the files could not be written
     * @throws InterruptedException          if the user cancels the processing
     */
    private void copyFilesToBuildDirectory(final File rootDir,
            final VirtualChannel channel,
            final Collection<ToolsWorkspaceFile> sourcesFiles)
            throws IOException, InterruptedException {

        File directory = new File(rootDir, ToolsWorkspaceFile.DIR_WORKSPACE_FILES);
        if (!directory.exists() && !directory.mkdir()) {
            throw new IOException("Can't create directory for copy of workspace files: "
                    + directory.getAbsolutePath());
        }

        for (ToolsWorkspaceFile file : sourcesFiles) {
            if (!file.isSourceIgnored()) {
                File masterFile = new File(directory, file.getTempName());
                if (!masterFile.exists()) {
                    FileOutputStream outputStream = new FileOutputStream(masterFile);
                    new FilePath(channel, file.getFileName()).copyTo(outputStream);
                }
            }
        }
    }

    @Extension
    public static final class ToolsDescriptor extends BuildStepDescriptor<Publisher> {

        public ToolsDescriptor() {
            super(ToolsPublisher.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.tools_PublishResults();
        }

        @Override
        public final String getHelpFile() {
            return getPluginRoot() + "help.html";
        }

        public String getPluginRoot() {
            return "/plugin/tools/";
        }

        public ToolsConfig getConfig() {
            return new ToolsConfig();
        }
    }
}
