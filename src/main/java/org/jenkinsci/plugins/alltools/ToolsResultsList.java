/**
 * 
 */
package org.jenkinsci.plugins.alltools;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.alltools.model.ToolsWorkspaceFile;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractBuild;
import hudson.model.Item;

/**
 * @author jain
 *
 */
public class ToolsResultsList {
	/** The related build. */
	private final AbstractBuild<?, ?> owner;
	private List<ToolsResult> result;

	public ToolsResultsList(AbstractBuild<?, ?> owner, List<ToolsResult> results) {
		this.owner = owner;
		this.result = results;
	}

	/**
	 * @return the owner
	 */
	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	/**
	 * @return the result
	 */
	public List<ToolsResult> getResult() {
		return result;
	}

	/**
	 * Gets the dynamic result of the selection element.
	 *
	 * @param link
	 *            the link to identify the sub page to show
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @return the dynamic result of the analysis (detail page).
	 * @throws java.io.IOException
	 *             if an error occurs
	 */
	public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response)
			throws IOException {
		System.out.println("my new link = " + link);
		if (link.equals("source.all")) {
			if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
				response.sendRedirect2("nosourcepermission");
				return null;
			}

			Set<ToolsDiffState> filter = this.result.get(0).parseStatesFilter(request.getParameter("states"));
			Collection<ToolsWorkspaceFile> files = this.result.get(0).diffCurrentAndPrevious(filter);
			int before = this.result.get(0).parseIntWithDefault(request.getParameter("before"), 5);
			int after = this.result.get(0).parseIntWithDefault(request.getParameter("after"), 5);

			return new ToolsSourceAll(owner, files, before, after);
		} else if (link.startsWith("source.")) {
			if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
				response.sendRedirect2("nosourcepermission");
				return null;
			}

			Map<Integer, ToolsWorkspaceFile> agregateMap = this.result.get(0).getCppcheckSourceContainer()
					.getInternalMap();

			if (agregateMap != null) {
				ToolsWorkspaceFile vCppcheckWorkspaceFile = agregateMap
						.get(Integer.parseInt(StringUtils.substringAfter(link, "source.")));

				if (vCppcheckWorkspaceFile == null) {
					throw new IllegalArgumentException("Error for retrieving the source file with link:" + link);
				}

				return new ToolsSource(owner, vCppcheckWorkspaceFile);
			}
		} else {
			for (ToolsResult tResult : this.result) {
				if (tResult.getType().equals(link)) {
					System.out.println("returning result..");
					return tResult;
				}
			}
		}
		return null;
	}
}
