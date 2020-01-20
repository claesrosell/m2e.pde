package org.eclipse.m2e.pde;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetLocationFactory;

public class MavenLocationFactory implements ITargetLocationFactory {

	public MavenLocationFactory() {
		System.out.println("drsre");
	}

	@Override
	public ITargetLocation getTargetLocation(String type, String serializedXML) throws CoreException {

		return new MavenLocation(serializedXML);
	}

}
