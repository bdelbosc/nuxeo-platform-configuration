package org.nuxeo.ecm.platform.configuration;

import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;

public interface ConfigurationService {

    /**
     * Get a registered configuration.
     * 
     * @param name
     * @return
     */
    AbstractConfiguration getConfiguration(String name);

    /**
     * List the registered configurations names
     * 
     * @return Always a list.
     */
    List<String> getConfigurationNames();

}
