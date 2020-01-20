package org.eclipse.m2e.pde.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

public class Resources {

	public static ImageDescriptor MAVEN_TARGET_IMAGE_DESCRIPTOR;
	
	static {
		try {
			MAVEN_TARGET_IMAGE_DESCRIPTOR = ImageDescriptor.createFromURL(new URL ("platform:/plugin/org.eclipse.m2e.pde.ui/resources/maven16x16.png") );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
