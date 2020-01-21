package org.eclipse.m2e.pde;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.eclipse.aether.transport.wagon.WagonProvider;

import io.takari.aether.wagon.OkHttpWagon;

public class ManualWagonProvider implements WagonProvider {

    public Wagon lookup(String roleHint) throws Exception {
        if ("file".equals(roleHint)) {
            return new FileWagon();
        } else if ("http".equals(roleHint)) {
            return new OkHttpWagon();
        } else if ("https".equals(roleHint)) {
            return new OkHttpWagon();
        }
        throw new IllegalArgumentException("No wagon provider registered for protocol " + roleHint);
    }

    public void release(Wagon wagon) {
    }
}
