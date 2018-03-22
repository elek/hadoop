
# Acceptance test suite for Ozone/Hdsl

This project contains acceptance tests for ozone/hdsl using docker-compose and [robot framework](http://robotframework.org/).

## Run

To run the acceptance tests, please

 1. Do a full build (you need a finalized hadoop distribution in your hadoop-dist/target)
 2. Execute the test suite with `mvn integration-test` from robot-test dir.
 3. Check the results at target/robotframework-reports/log.html.
 
## Development

You can run manually the robot tests with `robot` cli. (See robotframework docs to install it.)

 1. Go to the `src/test/robotframework`
 2. Execute `robot -v basedir:${PWD}/../../.. -v VERSION:3.2.0-SNAPSHOT .`
 
You can also use `-exclude` and `-include` command line parameters to filter by the tags.