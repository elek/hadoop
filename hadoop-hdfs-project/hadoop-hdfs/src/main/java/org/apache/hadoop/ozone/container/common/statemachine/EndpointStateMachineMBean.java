package org.apache.hadoop.ozone.container.common.statemachine;

import org.apache.hadoop.ozone.protocol.VersionResponse;

public interface EndpointStateMachineMBean {

  long getMissedCount();

  String getAddressString();

  EndpointStateMachine.EndPointStates getState();

  int getVersionNumber();
}
