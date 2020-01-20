package org.eclipse.m2e.pde.ui;

import java.util.HashSet;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.m2e.pde.MavenLocation;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.ui.target.ITargetLocationEditor;
import org.eclipse.pde.ui.target.ITargetLocationUpdater;

public class MavenLocationAdapterFactory implements IAdapterFactory, ITargetLocationEditor, ITargetLocationUpdater {

	private ILabelProvider fLabelProvider;
	private ITreeContentProvider fContentProvider;

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] {ILabelProvider.class, ITreeContentProvider.class, ITargetLocationEditor.class, ITargetLocationUpdater.class};
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof MavenLocation) {
			if (adapterType == ILabelProvider.class) {
				return (T) getLabelProvider();
			} else if (adapterType == ITreeContentProvider.class) {
				return (T) getContentProvider();
			} else if (adapterType == ITargetLocationEditor.class) {
				return (T) this;
			} else if (adapterType == ITargetLocationUpdater.class) {
				return (T) this;
			}
		}
		return null;
	}

	@Override
	public boolean canEdit(ITargetDefinition target, ITargetLocation targetLocation) {
		return targetLocation instanceof MavenLocation;
	}

	@Override
	public IWizard getEditWizard(ITargetDefinition target, ITargetLocation targetLocation) {
		return new EditMavenLocationWizard(target, targetLocation);
	}

	@Override
	public boolean canUpdate(ITargetDefinition target, ITargetLocation targetLocation) {
		return targetLocation instanceof MavenLocation;
	}

	@Override
	public IStatus update(ITargetDefinition target, ITargetLocation targetLocation, IProgressMonitor monitor) {
		// This method has to run synchronously, so we do the update ourselves instead of using UpdateTargetJob
		if (targetLocation instanceof MavenLocation) {
			boolean result = ((MavenLocation) targetLocation).update(new HashSet<>(0), monitor);
			if (result) {
				return Status.OK_STATUS;
			}
			return Status.OK_STATUS;

		}
		return Status.CANCEL_STATUS;
	}

	private ILabelProvider getLabelProvider() {
		if (fLabelProvider == null) {
			fLabelProvider = new MavenLocationLabelProvider();
		}
		return fLabelProvider;
	}

	private ITreeContentProvider getContentProvider() {
		if (fContentProvider == null) {
			fContentProvider = null; /* Show children */
		}
		return fContentProvider;
	}

}
