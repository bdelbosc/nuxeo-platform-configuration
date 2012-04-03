/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     ben
 */

package org.nuxeo.ecm.platform.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author ben
 */
public class ConfigurationServiceImpl extends DefaultComponent implements
        ConfigurationService {
    private static final Log log = LogFactory.getLog(ConfigurationServiceImpl.class);

    public static final String CONFIGURATION_EP = "configuration";

    protected static final Map<String, ConfigurationDescriptor> configurations = new HashMap<String, ConfigurationDescriptor>();

    protected static final List<String> names = new ArrayList<String>();

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (CONFIGURATION_EP.equals(extensionPoint)
                && contribution instanceof ConfigurationDescriptor) {
            log.debug("Registering contribution...");
            ConfigurationDescriptor desc = (ConfigurationDescriptor) contribution;
            String name = desc.getName();
            configurations.put(name, desc);
            if (names.contains(name)) {
                log.warn("Overriding existing configuration: " + name);
            }
            names.add(name);
            log.info("Contribution " + name + " registered.");
        }
    }

    @Override
    public void activate(ComponentContext context) {
        log.info("Activate component.");
    }

    @Override
    public void deactivate(ComponentContext context) {
        log.info("Deactivate component.");
    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        log.info("Application started");
    }

    // API ---------------------------------------------------

    @Override
    public ConfigurationDescriptor getConfiguration(String name) {
        return configurations.get(name);
    }

    @Override
    public List<String> getConfigurationNames() {
        return names;
    }

}
