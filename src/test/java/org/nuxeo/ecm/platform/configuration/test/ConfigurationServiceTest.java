package org.nuxeo.ecm.platform.configuration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.storage.sql.DatabaseH2;
import org.nuxeo.ecm.core.storage.sql.DatabaseHelper;
import org.nuxeo.ecm.platform.configuration.ConfigurationConstants;
import org.nuxeo.ecm.platform.configuration.ConfigurationService;
import org.nuxeo.runtime.api.DataSourceHelper;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.jtajca.NuxeoContainer;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

public class ConfigurationServiceTest extends NXRuntimeTestCase {

    private static final Log log = LogFactory.getLog(ConfigurationServiceTest.class);

    private static final String H2_DIR = "target/test/h2-ds";

    /** Property used in the datasource URL. */
    private static final String H2_URL_PROP = "nuxeo.test.ds.url";

    public DatabaseHelper database = DatabaseHelper.DATABASE;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        NuxeoContainer.installNaming();
        String db = database.getClass().getSimpleName().toLowerCase();
        if (db.startsWith("database")) {
            db = db.substring("database".length());
        }
        if (database instanceof DatabaseH2) {
            File h2dir = new File(H2_DIR);
            FileUtils.deleteTree(h2dir);
            h2dir.mkdirs();
            File h2db = new File(h2dir, "binaries");
            String h2Url = "jdbc:h2:" + h2db.getAbsolutePath();
            Framework.getProperties().put(H2_URL_PROP, h2Url);
        }
        deployBundle("org.nuxeo.ecm.platform.configuration");
        deployBundle("org.nuxeo.ecm.platform.configuration.test");
        deployBundle("org.nuxeo.runtime.datasource");
        deployContrib("org.nuxeo.ecm.platform.configuration.test",
                String.format("OSGI-INF/datasource-%s-contrib.xml", db));
        // create table in database
        DataSource dataSource = DataSourceHelper.getDataSource(ConfigurationConstants.DATASOURCE);
        Connection connection = dataSource.getConnection();
        Statement st = connection.createStatement();
        String sql = String.format(
                " CREATE TABLE %s (%s VARCHAR(32) NOT NULL, %s VARCHAR(256) NOT NULL, "
                        + " %s VARCHAR(2048), CONSTRAINT %s_pkey PRIMARY KEY (%s, %s));",
                ConfigurationConstants.TABLE,
                ConfigurationConstants.COL_NAMESPACE,
                ConfigurationConstants.COL_KEY,
                ConfigurationConstants.COL_VALUE, ConfigurationConstants.TABLE,
                ConfigurationConstants.COL_KEY,
                ConfigurationConstants.COL_VALUE);
        st.execute(sql);
        connection.close();

        // Set framework properties to be able to load the configuration
        // properties files
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String resourceDir = loader.getResource(".").getFile();
        Framework.getProperties().put("package.resource.dir", resourceDir);
    }

    @After
    public void tearDown() throws Exception {
        DataSource dataSource = DataSourceHelper.getDataSource(ConfigurationConstants.DATASOURCE);
        Connection connection = dataSource.getConnection();
        Statement st = connection.createStatement();
        String sql;
        if (database instanceof DatabaseH2) {
            sql = "SHUTDOWN";
        } else {
            sql = String.format("DROP TABLE %s;", ConfigurationConstants.TABLE);
        }
        st.execute(sql);
        st.close();
        if (!connection.isClosed()) {
            connection.close();
        }
        wipeRuntime(); // this will unregister component while jndi context
                       // exists.
        NuxeoContainer.uninstallNaming();
        super.tearDown();
    }

    @Test
    public void testContrib() throws Exception {
        deployContrib("org.nuxeo.ecm.platform.configuration.test",
                "OSGI-INF/test-configuration-contrib.xml");
        ConfigurationService service = Framework.getService(ConfigurationService.class);
        assertNotNull(service);
        assertFalse(service.getConfigurationNames().isEmpty());
    }

    @Test
    public void testSystemConfiguration() throws Exception {
        deployContrib("org.nuxeo.ecm.platform.configuration.test",
                "OSGI-INF/test-configuration-contrib.xml");
        ConfigurationService service = Framework.getService(ConfigurationService.class);
        assertNotNull(service);

        List<String> names = service.getConfigurationNames();
        assertTrue(names.contains("system"));
        AbstractConfiguration conf = service.getConfiguration("system");
        assertTrue(conf.containsKey("java.home"));
    }

    @Test
    public void testPropertyConfiguration() throws Exception {
        deployContrib("org.nuxeo.ecm.platform.configuration.test",
                "OSGI-INF/test-configuration-contrib.xml");
        ConfigurationService service = Framework.getService(ConfigurationService.class);
        assertNotNull(service);

        List<String> names = service.getConfigurationNames();
        assertTrue(names.contains("main"));
        AbstractConfiguration conf = service.getConfiguration("main");
        assertTrue(conf.containsKey("foo"));
        assertFalse(conf.containsKey("unknown"));
    }

    @Test
    public void testDatabaseConfiguration() throws Exception {
        deployContrib("org.nuxeo.ecm.platform.configuration.test",
                "OSGI-INF/test-configuration-contrib.xml");
        ConfigurationService service = Framework.getService(ConfigurationService.class);
        assertNotNull(service);

        List<String> names = service.getConfigurationNames();
        assertTrue(names.contains("db"));
        AbstractConfiguration conf = service.getConfiguration("db");
        assertFalse(conf.containsKey("unknown"));
        int value = 42;
        String key = "mykey";
        conf.setProperty(key, value);
        String prefix = "8f84d54708693fd2ab6dd80e532e75b8";
        conf.setProperty(prefix + ".name", "name");
        conf.setProperty(prefix + ".size", 42);
        for (Iterator<String> it = conf.getKeys(prefix); it.hasNext();) {
            log.info(it.next());
        }
        assertEquals(value, conf.getInt(key));

    }

}
