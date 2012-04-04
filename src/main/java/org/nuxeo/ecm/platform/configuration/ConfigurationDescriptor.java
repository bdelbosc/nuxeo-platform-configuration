package org.nuxeo.ecm.platform.configuration;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("configuration")
public class ConfigurationDescriptor {
    @XNode("@name")
    protected String name;

    @XNode("@type")
    protected String type;
    
    @XNode("@file")
    protected String file;
    
    @XNode("@dbnamespace")
    protected String dbnamespace;
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }

    public String getFile() {
        return file;
    }
    
    public String getDbnamespace() {
        if (dbnamespace == null || dbnamespace.isEmpty()) {
            dbnamespace = ConfigurationConstants.DEFAULT_NAMESPACE;
        }
        return dbnamespace;
    }

}
