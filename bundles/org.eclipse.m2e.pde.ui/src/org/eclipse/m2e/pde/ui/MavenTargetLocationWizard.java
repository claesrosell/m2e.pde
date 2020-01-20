package org.eclipse.m2e.pde.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.m2e.pde.MavenLocation;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.ui.target.ITargetLocationWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MavenTargetLocationWizard extends Wizard implements ITargetLocationWizard {

	private ITargetDefinition targetDefinition = null;
	
	public MavenTargetLocationWizard()
	{
		this.addPage(new SelectRepositoryWizardPage());
		this.addPage(new AddDependenciesWizardPage());
	}
	
	@Override
	public IWizardPage[] getPages() {
		return super.getPages();
	}
	
	@Override
	public ITargetLocation[] getLocations() {

		MavenLocation exampleLocation = new MavenLocation();
		ITargetLocation[] targetLocations = new ITargetLocation[] {
				exampleLocation
		};
		
		return targetLocations;
	}

	@Override
	public void setTarget(ITargetDefinition target) {
		this.targetDefinition = target;
		
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	class SelectRepositoryWizardPage extends WizardPage {

		private Composite pageControl = null;
		
		protected SelectRepositoryWizardPage() {
			super("Select repository");
		}

		@Override
		public void createControl(Composite parent) {
			this.pageControl = new Composite(parent, SWT.NONE);
			this.pageControl.setLayout(new GridLayout(2, false));

			Label repositoryUrlLabel = new Label(this.pageControl, SWT.NONE );
			repositoryUrlLabel.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
			repositoryUrlLabel.setText("Respository URL:");

			Text repositoryUrlText = new Text(this.pageControl, SWT.BORDER);
			repositoryUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			this.setControl(this.pageControl);
		}

	}
	
	class AddDependenciesWizardPage extends WizardPage {

		private Composite pageControl;

		protected AddDependenciesWizardPage() {
			super("Add dependencies");
		}
		
		@Override
		public void createControl(Composite parent) {
			this.pageControl = new Composite(parent, SWT.NONE);
			
			this.setControl(this.pageControl);
		}
		
	}
	
}
