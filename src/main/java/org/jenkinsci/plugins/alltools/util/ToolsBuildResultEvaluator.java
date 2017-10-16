package org.jenkinsci.plugins.alltools.util;


import org.jenkinsci.plugins.alltools.config.ToolsConfigSeverityEvaluation;

import hudson.model.BuildListener;
import hudson.model.Result;

/**
 * @author Gregory Boissinot
 */
public class ToolsBuildResultEvaluator {
    public Result evaluateBuildResult(
            final BuildListener listener,
            int errorsCount,
            int newErrorsCount,
            ToolsConfigSeverityEvaluation severityEvaluation) {

        if (isErrorCountExceeded(errorsCount, severityEvaluation.getFailureThreshold())) {
            ToolsLogger.log(listener,
                    "Setting build status to FAILURE since total number of issues '"
                            + errorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(newErrorsCount, severityEvaluation.getNewFailureThreshold())) {
            ToolsLogger.log(listener,
                    "Setting build status to FAILURE since number of new issues '"
                            + newErrorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getNewFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(errorsCount, severityEvaluation.getThreshold())) {
            ToolsLogger.log(listener,
                    "Setting build status to UNSTABLE since total number of issues '"
                            + errorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getThreshold() + "'.");
            return Result.UNSTABLE;
        }
        if (isErrorCountExceeded(newErrorsCount, severityEvaluation.getNewThreshold())) {
            ToolsLogger.log(listener,
                    "Setting build status to UNSTABLE since number of new issues '"
                            + newErrorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getNewThreshold() + "'.");
            return Result.UNSTABLE;
        }

        ToolsLogger.log(listener,
                "Not changing build status, since no threshold has been exceeded.");
        return Result.SUCCESS;
    }

    private boolean isErrorCountExceeded(final int errorCount, final String errorThreshold) {
        if (errorCount > 0 && ToolsMetricUtil.isValid(errorThreshold)) {
            return errorCount >= ToolsMetricUtil.convert(errorThreshold);
        }
        return false;
    }
}
