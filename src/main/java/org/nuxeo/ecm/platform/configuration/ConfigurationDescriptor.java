package org.nuxeo.ecm.platform.configuration;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("configuration")
public class ConfigurationDescriptor {
    @XNode("@name")
    protected String name;

    public String getName() {
        return name;
    }

}
