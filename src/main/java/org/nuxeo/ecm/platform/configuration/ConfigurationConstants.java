package org.nuxeo.ecm.platform.configuration;

public class ConfigurationConstants {
    private ConfigurationConstants() {
        // prevent instantiating utility class.
    }

    // database
    public static final String DATASOURCE = "jdbc/configuration";

    public static final String TABLE = "configuration";

    public static final String COL_NAMESPACE = "ns";

    public static final String COL_KEY = "key";

    public static final String COL_VALUE = "value";

    public static final String DEFAULT_NAMESPACE = "default";
    
    // configuration types
    public static final String TYPE_SYSTEM = "system";

    public static final String TYPE_PROPERTIES = "properties";

    public static final String TYPE_DATABASE = "database";

    public static final String[] TYPES = { TYPE_SYSTEM, TYPE_PROPERTIES,
            TYPE_DATABASE };

}
