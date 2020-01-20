package org.eclipse.m2e.pde.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MavenLocationLabelProvider extends LabelProvider {

	private ResourceManager resourceManager = null;
	
	public MavenLocationLabelProvider() {
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources());
	}
	
	@Override
	public String getText(Object element) {

		return "The shit target";
	}
	
	@Override
	public Image getImage(Object element) {
		return (Image) this.resourceManager.get(Resources.MAVEN_TARGET_IMAGE_DESCRIPTOR);
	}
	
}
