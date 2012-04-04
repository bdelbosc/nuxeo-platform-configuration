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

import javax.sql.DataSource;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.api.DataSourceHelper;
import org.nuxeo.runtime.api.Framework;
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

    protected static final Map<String, AbstractConfiguration> configurations = new HashMap<String, AbstractConfiguration>();

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
            String type = desc.getType();
            if (ConfigurationConstants.TYPE_SYSTEM.equals(type)) {
                log.debug("Using system properties");
                SystemConfiguration conf = new SystemConfiguration();
                configurations.put(name, conf);
            } else if (ConfigurationConstants.TYPE_PROPERTIES.equals(type)) {
                String path = Framework.expandVars(desc.getFile());
                log.debug("Using properties file: " + path);
                PropertiesConfiguration conf = new PropertiesConfiguration(path);
                conf.setAutoSave(true);
                configurations.put(name, conf);
            } else if (ConfigurationConstants.TYPE_DATABASE.equals(type)) {
                String ns = desc.getDbnamespace();
                log.debug("Using database namespace: " + ns);
                DataSource dataSource = DataSourceHelper.getDataSource(ConfigurationConstants.DATASOURCE);
                DatabaseConfiguration conf = new DatabaseConfiguration(
                        dataSource, ConfigurationConstants.TABLE,
                        ConfigurationConstants.COL_NAMESPACE,
                        ConfigurationConstants.COL_KEY,
                        ConfigurationConstants.COL_VALUE, ns);
                configurations.put(name, conf);
            } else {
                log.warn("Unsuported type: " + type
                        + ", skipping contribution.");
                return;
            }

            if (names.contains(name)) {
                log.warn("Overriding existing configuration: " + name);
            }
            names.add(name);
            log.info("Contribution " + name + " registered.");
        }
    }

    @Override
    public void activate(ComponentContext context) {
        log.debug("Activate component.");
    }

    @Override
    public void deactivate(ComponentContext context) {
        log.debug("Deactivate component.");
        names.clear();
        configurations.clear();
    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        log.debug("Application started");
    }

    // API ---------------------------------------------------

    @Override
    public AbstractConfiguration getConfiguration(String name) {
        return configurations.get(name);
    }

    @Override
    public List<String> getConfigurationNames() {
        return names;
    }

}
