package org.jenkinsci.plugins.alltools;

import org.jenkinsci.plugins.alltools.model.ToolsFile;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gregory Boissinot
 */
@ExportedBean
public class ToolsReport implements Serializable {

    private String version;
    private String type;
    
    private transient List<ToolsFile> allErrors = new ArrayList<ToolsFile>();
    private transient Set<String> versions = new HashSet<String>();

    private List<ToolsFile> errorSeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> warningSeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> styleSeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> performanceSeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> informationSeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> noCategorySeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> portabilitySeverityList = new ArrayList<ToolsFile>();
    private List<ToolsFile> falseAlarmSeverityList = new ArrayList<ToolsFile>();


    /**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
        return version;
    }

    public Set<String> getVersions() {
        return versions;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ToolsFile> getAllErrors() {
        return allErrors;
    }

    public void setAllErrors(List<ToolsFile> allErrors) {
        this.allErrors = allErrors;
    }

    public List<ToolsFile> getErrorSeverityList() {
        return errorSeverityList;
    }

    public void setErrorSeverityList(List<ToolsFile> errorSeverityList) {
        this.errorSeverityList = errorSeverityList;
    }

    public List<ToolsFile> getWarningSeverityList() {
        return warningSeverityList;
    }

    public void setWarningSeverityList(List<ToolsFile> warningSeverityList) {
        this.warningSeverityList = warningSeverityList;
    }

    public List<ToolsFile> getStyleSeverityList() {
        return styleSeverityList;
    }

    public void setStyleSeverityList(List<ToolsFile> styleSeverityList) {
        this.styleSeverityList = styleSeverityList;
    }

    public List<ToolsFile> getPerformanceSeverityList() {
        return performanceSeverityList;
    }

    public void setPerformanceSeverityList(List<ToolsFile> performanceSeverityList) {
        this.performanceSeverityList = performanceSeverityList;
    }

    public List<ToolsFile> getInformationSeverityList() {
        return informationSeverityList;
    }

    public void setInformationSeverityList(List<ToolsFile> informationSeverityList) {
        this.informationSeverityList = informationSeverityList;
    }

    public List<ToolsFile> getNoCategorySeverityList() {
        return noCategorySeverityList;
    }

    public void setNoCategorySeverityList(List<ToolsFile> noCategorySeverityList) {
        this.noCategorySeverityList = noCategorySeverityList;
    }

    public List<ToolsFile> getPortabilitySeverityList() {
        return portabilitySeverityList;
    }

    public void setPortabilitySeverityList(List<ToolsFile> portabilitySeverityList) {
        this.portabilitySeverityList = portabilitySeverityList;
    }

    /**
	 * @return the falseAlarmSeverityList
	 */
	public List<ToolsFile> getFalseAlarmSeverityList() {
		return falseAlarmSeverityList;
	}

	/**
	 * @param falseAlarmSeverityList the falseAlarmSeverityList to set
	 */
	public void setFalseAlarmSeverityList(List<ToolsFile> falseAlarmSeverityList) {
		this.falseAlarmSeverityList = falseAlarmSeverityList;
	}

	@Exported
    public int getNumberTotal() {
        return (allErrors == null) ? 0 : allErrors.size();
    }

    @Exported
    public int getNumberErrorSeverity() {
        return (errorSeverityList == null) ? 0 : errorSeverityList.size();
    }

    @Exported
    public int getNumberWarningSeverity() {
        return (warningSeverityList == null) ? 0 : warningSeverityList.size();
    }

    @Exported
    public int getNumberStyleSeverity() {
        return (styleSeverityList == null) ? 0 : styleSeverityList.size();
    }

    @Exported
    public int getNumberPerformanceSeverity() {
        return (performanceSeverityList == null) ? 0 : performanceSeverityList.size();
    }

    @Exported
    public int getNumberInformationSeverity() {
        return (informationSeverityList == null) ? 0 : informationSeverityList.size();
    }

    @Exported
    public int getNumberNoCategorySeverity() {
        return (noCategorySeverityList == null) ? 0 : noCategorySeverityList.size();
    }

    @Exported
    public int getNumberPortabilitySeverity() {
        return (portabilitySeverityList == null) ? 0 : portabilitySeverityList.size();
    }

    @Exported
    public int getNumberFalseAlarmSeverity() {
        return (falseAlarmSeverityList== null) ? 0 : falseAlarmSeverityList.size();
    }

    private Object readResolve() {
        this.allErrors = new ArrayList<ToolsFile>();
        this.allErrors.addAll(errorSeverityList);
        this.allErrors.addAll(warningSeverityList);
        this.allErrors.addAll(styleSeverityList);
        this.allErrors.addAll(performanceSeverityList);
        this.allErrors.addAll(informationSeverityList);
        this.allErrors.addAll(noCategorySeverityList);
        this.allErrors.addAll(falseAlarmSeverityList);
 
        // Backward compatibility with version 1.14 and less
        if(portabilitySeverityList == null)
        {
            portabilitySeverityList = new ArrayList<ToolsFile>();
        }

        this.allErrors.addAll(portabilitySeverityList);

        return this;
    }

    /**
     * Get statistics for this report.
     * 
     * @return the statistics
     */
    public ToolsStatistics getStatistics() {
        return new ToolsStatistics(getNumberErrorSeverity(),
                getNumberWarningSeverity(), getNumberStyleSeverity(),
                getNumberPerformanceSeverity(), getNumberInformationSeverity(),
                getNumberNoCategorySeverity(), getNumberPortabilitySeverity(),
                getNumberFalseAlarmSeverity(),
                versions);
    }
}
