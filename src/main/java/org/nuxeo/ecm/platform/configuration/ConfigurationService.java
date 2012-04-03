package org.nuxeo.ecm.platform.configuration;

import java.util.List;

public interface ConfigurationService {

    ConfigurationDescriptor getConfiguration(String name);

    List<String> getConfigurationNames();

}
