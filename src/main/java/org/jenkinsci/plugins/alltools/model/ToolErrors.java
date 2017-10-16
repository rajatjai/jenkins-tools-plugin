/**
 * 
 */
package org.jenkinsci.plugins.alltools.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author jain
 *
 */
public class ToolErrors implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8508421415000097650L;
	
	@SerializedName("tool")
	@Expose
	private String toolname;
	
	@SerializedName("errors")
	@Expose
	private List<ErrorData> errorList;
	/**
	 * @return the toolname
	 */
	public String getToolname() {
		return toolname;
	}
	/**
	 * @param toolname the toolname to set
	 */
	public void setToolname(String toolname) {
		this.toolname = toolname;
	}
	/**
	 * @return the errorList
	 */
	public List<ErrorData> getErrorList() {
		return errorList;
	}
	/**
	 * @param errorList the errorList to set
	 */
	public void setErrorList(List<ErrorData> errorList) {
		this.errorList = errorList;
	}
}
