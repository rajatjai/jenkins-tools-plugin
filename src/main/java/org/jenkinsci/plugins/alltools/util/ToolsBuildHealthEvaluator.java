package org.jenkinsci.plugins.alltools.util;

import org.jenkinsci.plugins.alltools.config.ToolsConfigSeverityEvaluation;

/**
 * @author Gregory Boissinot
 */
public class ToolsBuildHealthEvaluator {
    public int evaluatBuildHealth(ToolsConfigSeverityEvaluation severityEvaluation,
            int nbErrorForSeverity) {
        if (severityEvaluation == null) {
            // no thresholds => no report
            return -1;
        }

        if (isHealthyReportEnabled(severityEvaluation)) {
            int percentage;
            
            int healthyNumber = ToolsMetricUtil.convert(severityEvaluation.getHealthy());
            int unHealthyNumber = ToolsMetricUtil.convert(severityEvaluation.getUnHealthy());

            if (nbErrorForSeverity < healthyNumber) {
                percentage = 100;
            } else if (nbErrorForSeverity > unHealthyNumber) {
                percentage = 0;
            } else {
                percentage = 100 - ((nbErrorForSeverity - healthyNumber) * 100
                        / (unHealthyNumber - healthyNumber));
            }

            return percentage;
        }
        return -1;
    }


    private boolean isHealthyReportEnabled(ToolsConfigSeverityEvaluation severityEvaluation) {
        if (ToolsMetricUtil.isValid(severityEvaluation.getHealthy())
                && ToolsMetricUtil.isValid(severityEvaluation.getUnHealthy())) {
            int healthyNumber = ToolsMetricUtil.convert(severityEvaluation.getHealthy());
            int unHealthyNumber = ToolsMetricUtil.convert(severityEvaluation.getUnHealthy());
            return unHealthyNumber > healthyNumber;
        }
        return false;
    }
}
