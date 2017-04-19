/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.discovery;

import org.wildfly.discovery.spi.DiscoveryProvider;
import org.wildfly.discovery.spi.DiscoveryRequest;
import org.wildfly.discovery.spi.DiscoveryResult;
import org.wildfly.discovery.spi.RegistryProvider;

final class ConfiguredProvider implements DiscoveryProvider, RegistryProvider {

    private final DiscoveryProvider delegateDiscoveryProvider;
    private final RegistryProvider delegateRegistryProvider;

    ConfiguredProvider(final DiscoveryProvider delegateDiscoveryProvider, final RegistryProvider delegateRegistryProvider) {
        System.out.println("ConfiguredProvider: constructor called");
        this.delegateDiscoveryProvider = delegateDiscoveryProvider;
        this.delegateRegistryProvider = delegateRegistryProvider;
    }

    public ServiceRegistration registerServices(final ServiceURL... serviceURLs) {
        System.out.println("ConfiguredProvider: registerService: serviceURLs = " + serviceURLs.toString());
        return delegateRegistryProvider.registerServices(serviceURLs);
    }

    public ServiceRegistration registerService(final ServiceURL serviceURL) {
        System.out.println("ConfiguredProvider: registerService: serviceURL = " + serviceURL);
        return delegateRegistryProvider.registerService(serviceURL);
    }

    public DiscoveryRequest discover(final ServiceType serviceType, final FilterSpec filterSpec, final DiscoveryResult result) {
        System.out.println("ConfiguredProvider: calling discover() with delegate discover provider: " + delegateDiscoveryProvider.getClass().getName());
        return delegateDiscoveryProvider.discover(serviceType, filterSpec, result);
    }

    static final ConfiguredProvider INSTANCE = DiscoveryXmlParser.getConfiguredProvider();
}
