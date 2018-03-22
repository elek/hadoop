*** Settings ***
Documentation       Smoke test to start cluster with docker-compose environments.
Library             OperatingSystem
Suite Setup         Startup Ozone Cluster


*** Variables ***
${COMMON_REST_HEADER}   -H "x-ozone-user: bilbo" -H "x-ozone-version: v1" -H  "Date: Mon, 26 Jun 2017 04:23:30 GMT" -H "Authorization:OZONE root"
${version}

*** Test Cases ***

Daemons are running without error
    Daemon is running without error           ksm
    Daemon is running without error           scm
    Daemon is running without error           namenode
    Daemon is running without error           datanode

Check if datanode is connected to the scm
    Wait Until Keyword Succeeds     2min    5sec    Have healthy datanodes   1

Scale it up to 5 datanodes
    Scale datanodes up  5
    Wait Until Keyword Succeeds     3min    5sec    Have healthy datanodes   5

Test rest interface
    ${result} =     Execute on      datanode     curl -i -X POST ${COMMON_RESTHEADER} "http://localhost:9880/volume1"
    Should contain  ${result}   201 Created
    ${result} =     Execute on      datanode     curl -i -X POST ${COMMON_RESTHEADER} "http://localhost:9880/volume1/bucket1"
    Should contain  ${result}   201 Created

Check webui static resources
    ${result} =			Execute on		scm		curl -s -I http://localhost:9876/static/bootstrap-3.0.2/js/bootstrap.min.js
	 Should contain		${result}		200
    ${result} =			Execute on		ksm		curl -s -I http://localhost:9874/static/bootstrap-3.0.2/js/bootstrap.min.js
	 Should contain		${result}		200

Start freon testing
    ${result} =		Execute on		ksm		oz freon -numOfVolumes 5 -numOfBuckets 5 -numOfKeys 5 -numOfThreads 10
	 Wait Until Keyword Succeeds	3min	10sec		Should contain		${result}		Number of Keys added: 125
	 Should Not Contain		${result}		ERROR

*** Keywords ***

Startup Ozone Cluster
    ${rc}       ${output} =                 Run docker compose          down
    ${rc}       ${output} =                 Run docker compose          up -d
    Should Be Equal As Integers 	        ${rc} 	                    0
    Wait Until Keyword Succeeds             1min    5sec    Is Daemon started   ksm     HTTP server of KSM is listening

Daemon is running without error
    [arguments]             ${name}
    ${result} =             Run                     docker ps
    Should contain          ${result}               _${name}_1
    ${rc}                   ${result} =             Run docker compose      logs ${name}
    Should not contain      ${result}               ERROR

Is Daemon started
    [arguments]     ${name}             ${expression}
    ${rc}           ${result} =         Run docker compose      logs
    Should contain  ${result}           ${expression}

Have healthy datanodes
    [arguments]         ${requirednodes}
    ${result} =         Execute on          scm                 curl -s 'http://localhost:9876/jmx?qry=Hadoop:service=SCMNodeManager,name=SCMNodeManagerInfo' | jq -r '.beans[0].NodeCount[] | select(.key=="HEALTHY") | .value'
    Should Be Equal     ${result}           ${requirednodes}

Scale datanodes up
    [arguments]     ${requirednodes}
    Run docker compose                  scale datanode=${requirednodes}

Execute on
    [arguments]     ${componentname}    ${command}
    ${rc}           ${return} =         Run docker compose          exec ${componentname} ${command}
    [return]        ${return}

Run docker compose
    [arguments]     ${command}
                    Set Environment Variable    HADOOPDIR                              ${basedir}/../../hadoop-dist/target/hadoop-${version}
    ${rc}           ${output} =                 Run And Return Rc And Output           docker-compose -f ${basedir}/target/compose/docker-compose.yaml ${command}
    [return]        ${rc}       ${output}