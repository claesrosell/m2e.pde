/**
 * 
 */
package org.eclipse.m2e.pde;

/**
 * @author crosell
 *
 */
public class MavenDependency {

	private String groupId = "";
	private String artifactId = "";
	private String version = "";
	private String scope = "";
	private String optional = "";

	public MavenDependency(String groupId, String artifactId)
	{
		this.groupId = groupId;
		this.artifactId = artifactId;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getOptional() {
		return optional;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}

}
