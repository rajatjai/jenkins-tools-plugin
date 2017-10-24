package org.jenkinsci.plugins.alltools;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;

import org.jenkinsci.plugins.alltools.config.ToolsConfig;
import org.jenkinsci.plugins.alltools.config.ToolsConfigSeverityEvaluation;
import org.jenkinsci.plugins.alltools.model.ReportType;
import org.jenkinsci.plugins.alltools.util.AbstractToolsBuildAction;
import org.jenkinsci.plugins.alltools.util.ToolsBuildHealthEvaluator;
import org.jenkinsci.plugins.alltools.Messages;

/**
 * @author Gregory Boissinot
 */
public class ToolsBuildAction extends AbstractToolsBuildAction {

    public static final String URL_NAME = "toolsResult";

    private List<ToolsResult> result;

    /** 
     * The health report percentage.
     * 
     * @since 1.15
     */
    private int healthReportPercentage;

    public ToolsBuildAction(AbstractBuild<?, ?> owner, List<ToolsResult> resultList,
            int healthReportPercentage) {
        super(owner);
        this.result = resultList;
        this.healthReportPercentage = healthReportPercentage;
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-24.png";
    }

    public String getDisplayName() {
        return Messages.tools_ToolsResults();
    }

    public String getUrlName() {
        return URL_NAME;
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public List<ToolsResult> getResult() {
        return this.result;
    }

    AbstractBuild<?, ?> getBuild() {
        return this.owner;
    }

    public Object getTarget() {
        return this.result;
    }

    public HealthReport getBuildHealth() {
        if(healthReportPercentage >= 0 && healthReportPercentage <= 100) {
            return new HealthReport(healthReportPercentage,
                    Messages._tools_BuildStability());
        } else {
            return null;
        }
    }

    public static int computeHealthReportPercentage(ToolsResult result,
            ToolsConfigSeverityEvaluation severityEvaluation) {
        try {
            return new ToolsBuildHealthEvaluator().evaluatBuildHealth(severityEvaluation,
                    result.getNumberErrorsAccordingConfiguration(severityEvaluation,
                            false));
        } catch (IOException e) {
            return -1;
        }
    }

    // Backward compatibility
    @Deprecated
    private transient AbstractBuild<?, ?> build;

    /** Backward compatibility with version 1.14 and less. */
    @Deprecated
    private transient ToolsConfig toolsConfig;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    private Object readResolve() {
        if (build != null) {
            this.owner = build;
        }

        // Backward compatibility with version 1.14 and less
        if (toolsConfig != null) {
            healthReportPercentage = 100;
        }

        return this;
    }
}
