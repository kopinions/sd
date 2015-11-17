package com.thoughtworks.sd.api;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class SdResourceConfig extends ResourceConfig {
    public SdResourceConfig(ServiceLocator locator, Injector injector) {
        packages("com.thoughtworks.sd")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                    }
                });
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
        GuiceIntoHK2Bridge bridge = locator.getService(GuiceIntoHK2Bridge.class);
        bridge.bridgeGuiceInjector(injector);
    }
}
