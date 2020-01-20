package org.eclipse.m2e.pde;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.core.target.TargetFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MavenLocation implements ITargetLocation {

	public static final String MAVEN_LOCATION_TYPE = "MavenLocation";
	
	private boolean resolved = false;

	private String mavenRepository = null;
	private Map<String, TargetBundle> resolvedBundles = new HashMap<>();
	private List<MavenDependency> content = new ArrayList<>();

	public MavenLocation() {

	}

	public MavenLocation(String serializedXML) {
	
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(serializedXML));
			Document document = dBuilder.parse(is);
			NodeList locationElements = document.getElementsByTagName("location");
			if ( locationElements.getLength() > 0 ) {
				Element locationElement = (Element)locationElements.item(0);
				this.mavenRepository = locationElement.getAttribute("repository");
				
				NodeList dependenciesElements = locationElement.getElementsByTagName("dependencies");
				for ( int dependencisElementIndex = 0; dependencisElementIndex < dependenciesElements.getLength(); dependencisElementIndex++ ) {
					Element dependenciesElement = (Element)dependenciesElements.item(dependencisElementIndex);
					NodeList dependencyElements = dependenciesElement.getElementsByTagName("dependency");
					for ( int dependencyElementsIndex = 0; dependencyElementsIndex < dependencyElements.getLength(); dependencyElementsIndex++ ) {
						Element dependencyElement  = (Element) dependencyElements.item(dependencyElementsIndex);
						String groupId = getTextValueFor(dependencyElement.getElementsByTagName("groupId") );
						String artifactId = getTextValueFor(dependencyElement.getElementsByTagName("artifactId") );
						String version = getTextValueFor(dependencyElement.getElementsByTagName("version") );
						String scope = getTextValueFor(dependencyElement.getElementsByTagName("scope") );
						String optional = getTextValueFor(dependencyElement.getElementsByTagName("optional") );

						MavenDependency mavenDependency = new MavenDependency(groupId, artifactId);
						mavenDependency.setVersion(version);
						mavenDependency.setScope(scope);
						mavenDependency.setOptional(optional);

						this.content.add(mavenDependency);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	


	private static String getTextValueFor(NodeList nodeList)
	{
		String result = "";
		if ( nodeList.getLength() > 0 ) {
			Node item = nodeList.item(0);
			result = item.getTextContent();
		}
		
		return result;
	}

	@Override
	public String getLocation(boolean arg0) throws CoreException {
		return this.mavenRepository;
	}

	@Override
	public String getType() {
		return MAVEN_LOCATION_TYPE;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if (adapter == ILabelProvider.class) {
			return null;
		}
		return null;
	}

	@Override
	public TargetBundle[] getBundles() {
		return this.resolvedBundles.values().toArray(new TargetBundle[0] );
	}

	@Override
	public TargetFeature[] getFeatures() {
		return new TargetFeature[0];
	}

	@Override
	public IStatus getStatus() {
		return Status.OK_STATUS;
	}

	@Override
	public String[] getVMArguments() {
		System.out.println("getVMArguments()");
		return null;
	}

	@Override
	public boolean isResolved() {
		return this.resolved;
	}

	@Override
	public IStatus resolve(ITargetDefinition definition, IProgressMonitor monitor) {
		this.resolved = false;
		this.resolvedBundles.clear();

		RepositorySystem system = Booter.newRepositorySystem();

		RepositorySystemSession session = Booter.newRepositorySystemSession(system);

		for ( MavenDependency mavenDependency : this.content) {

			Artifact artifact = new DefaultArtifact(mavenDependency.getGroupId() + ":" + mavenDependency.getArtifactId() + ":" + mavenDependency.getVersion());
			String artifactClassifier = artifact.toString();
			if ( !this.resolvedBundles.containsKey(artifactClassifier) ) {

				try {
					ArtifactRequest artifactRequest = new ArtifactRequest();
					artifactRequest.setArtifact(artifact);
					artifactRequest.setRequestContext("");
					artifactRequest.setRepositories(Booter.newRepositories(system, session));

					ArtifactResult resolvedArtifactResult = system.resolveArtifact(session, artifactRequest);
					File resolvedArtifactFile = resolvedArtifactResult.getArtifact().getFile();
					if ( resolvedArtifactFile != null && resolvedArtifactFile.exists() ) {
						TargetBundle resolvedArtifactBundle = new TargetBundle(resolvedArtifactFile);
						this.resolvedBundles.put(artifactClassifier, resolvedArtifactBundle);					
					}
				} catch (ArtifactResolutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(new Dependency(artifact, ""));

			collectRequest.setRepositories(Booter.newRepositories(system, session));

			try {

				CollectResult collectResult = system.collectDependencies(session, collectRequest);
				collectResult.getRoot().accept(new DependencyVisitor() {
					
					@Override
					public boolean visitLeave(DependencyNode dependencyNode) {
						return true;
					}
					
					@Override
					public boolean visitEnter(DependencyNode dependencyNode) {
						Artifact artifactDependency = dependencyNode.getArtifact();
						String artifactClassifier = artifactDependency.toString();
						if ( !resolvedBundles.containsKey(artifactClassifier) ) {

							try {
								ArtifactRequest artifactRequest = new ArtifactRequest();
								artifactRequest.setArtifact(artifactDependency);
								artifactRequest.setRequestContext("");
								artifactRequest.setRepositories(Booter.newRepositories(system, session));

								ArtifactResult resolvedArtifactResult = system.resolveArtifact(session, artifactRequest);
								File resolvedArtifactFile = resolvedArtifactResult.getArtifact().getFile();
								if ( resolvedArtifactFile != null && resolvedArtifactFile.exists() ) {
									TargetBundle resolvedArtifactBundle = new TargetBundle(resolvedArtifactFile);
									resolvedBundles.put(artifactClassifier, resolvedArtifactBundle);					
								}
							} catch (ArtifactResolutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						return true;
					}
				});
				

			} catch (DependencyCollectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ( monitor.isCanceled() ) {
				break;
			}
		}
		
		this.resolved = !monitor.isCanceled();		/* If the job was canceled: the location was not resolved */
		System.out.println("resolve()");
		return Status.OK_STATUS;
	}

	@Override
	public String serialize() {
		System.out.println("Serialize()");
		String serializedRepresentation = "";
        try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			// root element
			Element locationElement = document.createElement("location");
			locationElement.setAttribute("repository", this.mavenRepository);

			document.appendChild(locationElement);
			Element dependenciesElement = document.createElement("dependencies");
			locationElement.appendChild(dependenciesElement);

			for ( MavenDependency mavenDependency : this.content ) {
				Element dependencyElement = document.createElement("dependency");
				dependenciesElement.appendChild(dependencyElement);
				
				String groupId = mavenDependency.getGroupId();
				if ( groupId != null && groupId.isBlank() == false ) {
					Element groupIdElement = document.createElement("groupId");
					groupIdElement.appendChild(document.createTextNode(groupId.trim()));
					dependencyElement.appendChild(groupIdElement);
				}

				String artifactId = mavenDependency.getArtifactId();
				if ( artifactId != null && artifactId.isBlank() == false ) {
					Element artifactIdElement = document.createElement("artifactId");
					artifactIdElement.appendChild(document.createTextNode(artifactId.trim()));
					dependencyElement.appendChild(artifactIdElement);
				}

				String version = mavenDependency.getVersion();
				if ( version != null && version.isBlank() == false ) {
					Element versionElement = document.createElement("version");
					versionElement.appendChild(document.createTextNode(version.trim()));
					dependencyElement.appendChild(versionElement);
				}

				String scope = mavenDependency.getScope();
				if ( scope != null && scope.isBlank() == false ) {
					Element scopeElement = document.createElement("scope");
					scopeElement.appendChild(document.createTextNode(scope.trim()));
					dependencyElement.appendChild(scopeElement);
				}
				
				String optional = mavenDependency.getOptional();
				if ( optional != null && optional.isBlank() == false ) {
					Element optionalElement = document.createElement("optional");
					optionalElement.appendChild(document.createTextNode(optional.trim()));
					dependencyElement.appendChild(optionalElement);
				}
			}
			StringWriter stringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION,"yes");  

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(stringWriter);
 
            transformer.transform(domSource, streamResult);

            serializedRepresentation = stringWriter.getBuffer().toString();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return serializedRepresentation;
	}

	public boolean update(HashSet hashSet, IProgressMonitor monitor) {

		return false;
	}

	public void addArtifact(MavenDependency dependency) {
		this.content.add(dependency);
	}

	public void removeArtifact(MavenDependency dependency) {
		this.content.remove(dependency);
	}

}
