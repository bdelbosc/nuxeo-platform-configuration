package org.nuxeo.ecm.platform.configuration.test;

import org.nuxeo.ecm.platform.configuration.ConfigurationService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

public class ConfigurationServiceTest extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.schema");

        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.platform.configuration");
        deployBundle("org.nuxeo.ecm.platform.configuration.test");
    }


    public void testContrib() throws Exception {
        deployContrib("org.nuxeo.ecm.platform.configuration.test",
                "OSGI-INF/test-configuration-contrib.xml");
        ConfigurationService service = Framework.getService(ConfigurationService.class);
        assertNotNull(service);
        assertFalse(service.getConfigurationNames().isEmpty());
    }

    
    
}
