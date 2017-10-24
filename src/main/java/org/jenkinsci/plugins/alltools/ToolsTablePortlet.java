package org.jenkinsci.plugins.alltools;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.alltools.Messages;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.view.dashboard.DashboardPortlet;

/**
 * Dashboard portlet that shows a sortable table with jobs and tools
 * statistics per severity type.
 * 
 * @author Michal Turek
 */
public class ToolsTablePortlet extends DashboardPortlet {
    /**
     * Constructor.
     * 
     * @param name
     *            the name of the portlet
     */
    @DataBoundConstructor
    public ToolsTablePortlet(String name) {
        super(name);
    }

    /**
     * Get latest available tools statistics of a job.
     * 
     * @param job
     *            the job
     * @return the statistics, always non-null value
     */
    public List<ToolsStatistics> getStatistics(Job<?, ?> job) {
        Run<?, ?> build = job.getLastBuild();
        List<ToolsStatistics> toolsStatisticsList = new ArrayList<ToolsStatistics>();
        while(build != null){
            ToolsBuildAction action = build.getAction(ToolsBuildAction.class);

            if (action != null) {
                List<ToolsResult> result = action.getResult();
                for (ToolsResult toolsResult : result) {
                	if(toolsResult != null) {
                		toolsStatisticsList.add(toolsResult.getStatistics());
                	}
				}
            }

            build = build.getPreviousBuild();
        }

        return toolsStatisticsList;
    }

    /**
     * Extension point registration.
     * 
     * @author Michal Turek
     */
    @Extension(optional = true)
    public static class CppcheckTableDescriptor extends Descriptor<DashboardPortlet> {
        @Override
        public String getDisplayName() {
            return Messages.tools_PortletName();
        }
    }
}
