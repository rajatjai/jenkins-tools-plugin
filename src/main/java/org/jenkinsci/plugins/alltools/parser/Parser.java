/**
 * 
 */
package org.jenkinsci.plugins.alltools.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.jenkinsci.plugins.alltools.model.ErrorData;
import org.jenkinsci.plugins.alltools.model.RefinedErrors;
import org.jenkinsci.plugins.alltools.model.ToolErrors;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * @author jain
 *
 */
public class Parser {

//	public static void main(String[] args) throws Exception {
//		Parser parser = new Parser();
//		Gson gson = new GsonBuilder().create();
//		parser.parseToolsErrors(gson, toolErrorFile);
//		parser.parseMergedErrors(gson, mergedErrorFile);
//		parser.parseRefinedErrors(gson, refinedErrorFile);		
//	}
	
	public static List<ToolErrors> parseToolsErrors(Gson gson, String jsonFilePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {

		ToolErrors[] toolErrors = gson.fromJson(new JsonReader(new InputStreamReader(new FileInputStream(jsonFilePath), StandardCharsets.UTF_8)), ToolErrors[].class);
		List<ToolErrors> asList = Arrays.asList(toolErrors);
		System.out.println(asList.size());
		return asList;
	}
	
	public static List<ErrorData> parseMergedErrors(Gson gson, String jsonFilePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {

		ErrorData[] mergedErrors = gson.fromJson(new JsonReader(new InputStreamReader(new FileInputStream(jsonFilePath), StandardCharsets.UTF_8)), ErrorData[].class);
		List<ErrorData> asList = Arrays.asList(mergedErrors);
		System.out.println(asList.size());
		return asList;
	}
	
	public static List<RefinedErrors> parseRefinedErrors(Gson gson, String jsonFilePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {

		RefinedErrors[] refinedErrors = gson.fromJson(new JsonReader(new InputStreamReader(new FileInputStream(jsonFilePath), StandardCharsets.UTF_8)), RefinedErrors[].class);
		List<RefinedErrors> asList = Arrays.asList(refinedErrors);
		System.out.println(asList.size());
		return asList;
	}
}
