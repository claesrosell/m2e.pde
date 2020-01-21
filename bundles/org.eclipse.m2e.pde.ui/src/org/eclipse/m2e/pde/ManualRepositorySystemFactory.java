package org.eclipse.m2e.pde;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.wagon.WagonProvider;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;

/**
 * A factory for repository system instances that employs Aether's built-in service locator infrastructure to wire up
 * the system's components.
 */
public class ManualRepositorySystemFactory
{

    public static RepositorySystem newRepositorySystem()
    {
        /*
         * Aether's components implement org.eclipse.aether.spi.locator.Service to ease manual wiring and using the
         * prepopulated DefaultServiceLocator, we only need to register the repository connector and transporter
         * factories.
         */

        /* 
         * Det verkar som om man måste skapa en egen WagonProvider och lägga till den någonstans 
         * 
         * Här kan det finnas info:
         * 
         * https://github.com/jenkinsci/repository-connector-plugin/tree/master/src/main/java/org/jvnet/hudson/plugins/repositoryconnector
         * https://github.com/jenkinsci/repository-connector-plugin/blob/master/src/main/java/org/jvnet/hudson/plugins/repositoryconnector/aether/Aether.java
         * 
         */
    	
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        locator.addService( TransporterFactory.class, WagonTransporterFactory.class );


        
        
        locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()
        {
            @Override
            public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
            {
                exception.printStackTrace();
            }
        } );

        return locator.getService( RepositorySystem.class );
    }

}