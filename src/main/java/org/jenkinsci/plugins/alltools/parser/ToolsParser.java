package org.jenkinsci.plugins.alltools.parser;

import hudson.model.BuildListener;

import org.jenkinsci.plugins.alltools.ToolsReport;
import org.jenkinsci.plugins.alltools.model.ToolsFile;
import org.jenkinsci.plugins.alltools.util.ToolsLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.jenkinsci.plugins.alltools.model.Error;
import org.jenkinsci.plugins.alltools.model.Error.Location;
import org.jenkinsci.plugins.alltools.model.ErrorData;
import org.jenkinsci.plugins.alltools.model.Errors;
import org.jenkinsci.plugins.alltools.model.RefinedErrors;
import org.jenkinsci.plugins.alltools.model.Results;
import org.jenkinsci.plugins.alltools.model.ToolErrors;
import org.jenkinsci.plugins.alltools.model.Tools;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Rajat Jain
 */
public class ToolsParser implements Serializable {

	private static final long serialVersionUID = 1L;
	private Gson gson = new GsonBuilder().create();

	public ToolsReport parse(final File file, BuildListener listener) throws IOException {

		if (file == null) {
			throw new IllegalArgumentException("File input is mandatory.");
		}

		if (!file.exists()) {
			throw new IllegalArgumentException("File input " + file.getName() + " must exist.");
		}
		if (file.getName().contains("Refined")) {
			ToolsLogger.log(listener,"Parsing refined errors from file : " + file.getAbsolutePath());
			return parseRefinedErrors(file, listener);
		} else if (file.getName().contains("Merged")) {
			ToolsLogger.log(listener,"Parsing merged errors from file : " + file.getAbsolutePath());
			return parseMergedErrors(file, listener);
		} else if (file.getName().contains("Tool")) {
			ToolsLogger.log(listener,"Parsing all tools errors from file : " + file.getAbsolutePath());
			return parseAllToolsErrors(file, listener);
		}
		return new ToolsReport();
		
	
	}

	private ToolsReport parseAllToolsErrors(File file, BuildListener listener) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<ToolErrors> toolErrors = Parser.parseToolsErrors(gson, file.getAbsolutePath());
		// TODO supposed to be iterated but for now only the first
		// for (ToolErrors toolErrors2 : toolErrors) {
		ToolsLogger.log(listener, "Tool parsed = " + toolErrors.get(0).getToolname());
		String toolname = toolErrors.get(0).getToolname();
		List<ErrorData> errorList = toolErrors.get(0).getErrorList();
		ToolsReport report = getToolsReport(listener, toolname, errorList);
		ToolsLogger.log(listener,"Parsing all tools errors file done");
		return report;
	}



	private ToolsReport parseMergedErrors(File file, BuildListener listener) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<ErrorData> mergedErrors = Parser.parseMergedErrors(gson, file.getAbsolutePath());
		ToolsReport report = getToolsReport(listener, "", mergedErrors);
		ToolsLogger.log(listener,"Parsing merged errors file done");
		return report;
	}

	private ToolsReport parseRefinedErrors(File file, BuildListener listener) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<RefinedErrors> refinedErrors = Parser.parseRefinedErrors(gson, file.getAbsolutePath());
		Results results = new Results();
		Errors errs = new Errors();
		Tools tools = new Tools();
		for (RefinedErrors refinedErr : refinedErrors) {
			if(refinedErr.getFile() == null) {
				ToolsLogger.log(listener, "Skipping error as filename is null");
				continue;
			}
			Error error = new Error();
			error.setId(refinedErr.getType());
			error.setInconclusive(false);
			error.setMsg(refinedErr.getQualifier());
			error.setTools(refinedErr.getTools());
			Location loc = new Location();
			loc.setFile(refinedErr.getFile());
			loc.setLine(String.valueOf(refinedErr.getLine()));
			error.setLocation(loc);
			int status = refinedErr.getStatus();
			if(status == 0) {
				error.setSeverity("error");
			} else if (status == 1) {
				error.setSeverity("falseAlarm");
			} else {
				
			}
			errs.getError().add(error);
		}
		results.setErrors(errs);
		results.setTools(tools);
		ToolsReport report = new ToolsReport();
		ToolsLogger.log(listener, "getting report..");
		report = getReportVersion2(results);
		ToolsLogger.log(listener,"Parsing refined errors file done");
		return report;
	}

	/**
	 * @param listener
	 * @param toolname
	 * @param errorList
	 * @return
	 */
	private ToolsReport getToolsReport(BuildListener listener, String toolname, List<ErrorData> errorList) {
		Results results = new Results();
		Errors errs = new Errors();
		Tools tools = new Tools();
		tools.setVersion(toolname);
		ToolsLogger.log(listener, "Data conversion..");
		for (ErrorData errorData : errorList) {
			if(errorData.getFile() == null) {
				ToolsLogger.log(listener, "Skipping error as filename is null");
				continue;
			}
			Error error = new Error();
			error.setId(errorData.getType());
			error.setInconclusive(false);
			error.setMsg(errorData.getQualifier());
			Location loc = new Location();
			loc.setFile(errorData.getFile());
			loc.setLine(String.valueOf(errorData.getLine()));
			error.setLocation(loc);
			error.setTools(errorData.getTools());
			//error.setSeverity("style");
			//error.setVerbose("medium");
			errs.getError().add(error);
		}
		results.setErrors(errs);
		results.setTools(tools);
		// TODO: process errors and convert to ToolFiles
		// }
		ToolsReport report = new ToolsReport();
		ToolsLogger.log(listener, "getting report..");
		report = getReportVersion2(results);
		return report;
	}
	
	/**
	 * TODO: map to jenkins-etc project
	 * 
	 * @param results
	 * @return
	 */
	private ToolsReport getReportVersion2(Results results) {

		ToolsReport toolReport = new ToolsReport();
		List<ToolsFile> allErrors = new ArrayList<ToolsFile>();
		List<ToolsFile> errorSeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> warningSeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> styleSeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> performanceSeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> informationSeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> noCategorySeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> portabilitySeverityList = new ArrayList<ToolsFile>();
		List<ToolsFile> falseAlarmSeverityList = new ArrayList<ToolsFile>();


		ToolsFile toolFile;

		Errors errors = results.getErrors();

		if (errors != null) {
			for (int i = 0; i < errors.getError().size(); i++) {
				org.jenkinsci.plugins.alltools.model.Error error = errors.getError().get(i);
				toolFile = new ToolsFile();

				toolFile.setCppCheckId(error.getId());
				toolFile.setSeverity(error.getSeverity());
				toolFile.setMessage(error.getMsg());
				toolFile.setInconclusive((error.isInconclusive() != null) ? error.isInconclusive() : false);
				toolFile.setTools(error.getTools());

				// msg and verbose items have often the same text in XML report,
				// there is no need to store duplications
				if (error.getVerbose() != null && !error.getMsg().equals(error.getVerbose())) {
					toolFile.setVerbose(error.getVerbose());
				}

				if ("warning".equals(toolFile.getSeverity())) {
					warningSeverityList.add(toolFile);
				} else if ("style".equals(toolFile.getSeverity())) {
					styleSeverityList.add(toolFile);
				} else if ("performance".equals(toolFile.getSeverity())) {
					performanceSeverityList.add(toolFile);
				} else if ("error".equals(toolFile.getSeverity())) {
					errorSeverityList.add(toolFile);
				} else if ("information".equals(toolFile.getSeverity())) {
					informationSeverityList.add(toolFile);
				} else if ("portability".equals(toolFile.getSeverity())) {
					portabilitySeverityList.add(toolFile);
				} else if ("falseAlarm".equals(toolFile.getSeverity())) {
					falseAlarmSeverityList.add(toolFile);
				} else {
					noCategorySeverityList.add(toolFile);
				}
				allErrors.add(toolFile);

				// FileName and Line
				org.jenkinsci.plugins.alltools.model.Error.Location location = error.getLocation();
				if (location != null) {
					toolFile.setFileName(location.getFile());
					String lineAtr;
					if ((lineAtr = location.getLine()) != null) {
						toolFile.setLineNumber(Integer.parseInt(lineAtr));
					}
				}
			}
		}

		toolReport.setAllErrors(allErrors);
		toolReport.setErrorSeverityList(errorSeverityList);
		toolReport.setInformationSeverityList(informationSeverityList);
		toolReport.setNoCategorySeverityList(noCategorySeverityList);
		toolReport.setPerformanceSeverityList(performanceSeverityList);
		toolReport.setStyleSeverityList(styleSeverityList);
		toolReport.setWarningSeverityList(warningSeverityList);
		toolReport.setPortabilitySeverityList(portabilitySeverityList);
		toolReport.setFalseAlarmSeverityList(falseAlarmSeverityList);

		if (results.getTools() != null) {
			toolReport.setVersion(results.getTools().getVersion());
		}

		return toolReport;
	}
}
