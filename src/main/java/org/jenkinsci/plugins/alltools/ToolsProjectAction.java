package org.jenkinsci.plugins.alltools;

import java.io.IOException;
import java.util.Calendar;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;

import org.jenkinsci.plugins.alltools.config.ToolsConfigGraph;
import org.jenkinsci.plugins.alltools.graph.ToolsGraph;
import org.jenkinsci.plugins.alltools.util.AbstractToolsProjectAction;
import org.jenkinsci.plugins.alltools.Messages;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Gregory Boissinot
 */
public class ToolsProjectAction extends AbstractToolsProjectAction {
    /** Cppcheck graph configuration. */
    private final ToolsConfigGraph configGraph;

    public String getSearchUrl() {
        return getUrlName();
    }

    public ToolsProjectAction(final AbstractProject<?, ?> project,
    		ToolsConfigGraph configGraph) {
        super(project);
        this.configGraph = configGraph;
    }

    public AbstractBuild<?, ?> getLastFinishedBuild() {
        AbstractBuild<?, ?> lastBuild = project.getLastBuild();
        while (lastBuild != null && (lastBuild.isBuilding()
                || lastBuild.getAction(ToolsBuildAction.class) == null)) {
            lastBuild = lastBuild.getPreviousBuild();
        }
        return lastBuild;
    }

    /**
     * Get build action of the last finished build.
     * 
     * @return the build action or null
     */
    public ToolsBuildAction getLastFinishedBuildAction() {
        AbstractBuild<?, ?> lastBuild = getLastFinishedBuild();
        return (lastBuild != null) ? lastBuild.getAction(ToolsBuildAction.class) : null;
    }

    public final boolean isDisplayGraph() {
        //Latest
        AbstractBuild<?, ?> b = getLastFinishedBuild();
        if (b == null) {
            return false;
        }

        //Affect previous
        b = b.getPreviousBuild();
        if (b != null) {

            for (; b != null; b = b.getPreviousBuild()) {
                if (b.getResult().isWorseOrEqualTo(Result.FAILURE)) {
                    continue;
                }
                ToolsBuildAction action = b.getAction(ToolsBuildAction.class);
                if (action == null || action.getResult() == null) {
                    continue;
                }
                ToolsResult result = action.getResult();
                if (result == null)
                    continue;

                return true;
            }
        }
        return false;
    }

    public Integer getLastResultBuild() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuiltBuild()) {
            ToolsBuildAction r = b.getAction(ToolsBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
    }


    public String getDisplayName() {
        return Messages.tools_ToolsResults();
    }

    public String getUrlName() {
        return ToolsBuildAction.URL_NAME;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

        AbstractBuild<?, ?> lastBuild = getLastFinishedBuild();
        Calendar timestamp = lastBuild.getTimestamp();

        if (req.checkIfModified(timestamp, rsp)) {
            return;
        }

        Graph g = new ToolsGraph(lastBuild, getDataSetBuilder().build(),
                Messages.tools_NumberOfErrors(),
                configGraph.getXSize(),
                configGraph.getYSize());
        g.doPng(req, rsp);
    }

    private DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> getDataSetBuilder() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb
                = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        AbstractBuild<?,?> lastBuild = getLastFinishedBuild();
        ToolsBuildAction lastAction = lastBuild.getAction(ToolsBuildAction.class);

        int numBuilds = 0;

        // numBuildsInGraph <= 1 means unlimited
        for (ToolsBuildAction a = lastAction;
             a != null && (configGraph.getNumBuildsInGraph() <= 1 || numBuilds < configGraph.getNumBuildsInGraph());
             a = a.getPreviousResult(), ++numBuilds) {

            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.getOwner());
            ToolsStatistics statistics = a.getResult().getStatistics();

            // error
            if (configGraph.isDisplayErrorSeverity())
                dsb.add(statistics.getNumberErrorSeverity(),
                        Messages.tools_Error(), label);

            //warning
            if (configGraph.isDisplayWarningSeverity())
                dsb.add(statistics.getNumberWarningSeverity(),
                        Messages.tools_Warning(), label);

            //style
            if (configGraph.isDisplayStyleSeverity())
                dsb.add(statistics.getNumberStyleSeverity(),
                        Messages.tools_Style(), label);

            //performance
            if (configGraph.isDisplayPerformanceSeverity())
                dsb.add(statistics.getNumberPerformanceSeverity(),
                        Messages.tools_Performance(), label);

            //information
            if (configGraph.isDisplayInformationSeverity())
                dsb.add(statistics.getNumberInformationSeverity(),
                        Messages.tools_Information(), label);

            //no category
            if (configGraph.isDisplayNoCategorySeverity())
                dsb.add(statistics.getNumberNoCategorySeverity(),
                        Messages.tools_NoCategory(), label);

            //portability
            if (configGraph.isDisplayPortabilitySeverity())
                dsb.add(statistics.getNumberPortabilitySeverity(),
                        Messages.tools_Portability(), label);

            //falseAlarm
            if (configGraph.isDisplayFalseAlarmSeverity())
                dsb.add(statistics.getNumberFalseAlarmSeverity(),
                        Messages.tools_FalseAlarm(), label);

            // all errors
            if (configGraph.isDisplayAllErrors())
                dsb.add(statistics.getNumberTotal(),
                        Messages.tools_AllErrors(), label);
        }
        return dsb;
    }
}
