# About the Nuxeo Configuration Service

This service enable to define
[apache common configuration](http://commons.apache.org/configuration/)
sources.

Supported configuration types are:

* system: for OS environment variables.
* properties: for Java properties file.
* database: for a database backend.

The database configuration expects a jdbc datasource named:

    jdbc/configuration


## Testing with different database

It follows the same procedure than VCS:

1. Setup the env

    source ~/bin/test_db_local_setup.sh

2. Setup the database from nuxeo root src:

    mvn initialize -Pcustomdb,pgsql -N

3. Run test in the module

    mvn test -Pcustomdb,pgsql

In eclipse use Run properties VM arguments using same values than in
~/nuxeo-test-vcs.properties :

    -Dnuxeo.test.vcs.server=localhost  \
    -Dnuxeo.test.vcs.db=PostgreSQL -Dnuxeo.test.vcs.port=5432 \
    -Dnuxeo.test.vcs.database=XXX -Dnuxeo.test.vcs.user=XXX \
    -Dnuxeo.test.vcs.password=XXX

