/**
 * 
 */
package org.jenkinsci.plugins.alltools.model;

import java.io.Serializable;

/**
 * @author jain
 *
 */
public class ErrorData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4296983879605991936L;
	private String file;
	private Integer line;
	private String qualifier;
	private String variable;
	private String procedure;
	private String type;
	private Integer clazz;
	private String tools;
	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * @return the line
	 */
	public Integer getLine() {
		return line;
	}
	/**
	 * @param line the line to set
	 */
	public void setLine(Integer line) {
		this.line = line;
	}
	/**
	 * @return the qualifier
	 */
	public String getQualifier() {
		return qualifier;
	}
	/**
	 * @param qualifier the qualifier to set
	 */
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	/**
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}
	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}
	/**
	 * @return the procedure
	 */
	public String getProcedure() {
		return procedure;
	}
	/**
	 * @param procedure the procedure to set
	 */
	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}
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
	/**
	 * @return the clazz
	 */
	public Integer getClazz() {
		return clazz;
	}
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Integer clazz) {
		this.clazz = clazz;
	}
	/**
	 * @return the tools
	 */
	public String getTools() {
		return tools;
	}
	/**
	 * @param tools the tools to set
	 */
	public void setTools(String tools) {
		this.tools = tools;
	}
}
