#include <iostream>
#include "AgentServer.h"

using std::cerr;
using std::endl;

extern void rlSetAgentConnection(int);

void AgentServer::startServer()
{
  char* envptr = 0;
  short port = kDefaultPort;

  envptr = getenv("RLGLUE_PORT");  
  if (envptr != 0) {
    port = strtol(envptr, 0, 10);
    if (port == 0) {
      port = kDefaultPort;
    }
    cerr << "AgentServer is listening for connections on port = " << port << endl;
  }

  serverSocket = rlOpen(port);
  rlListen(serverSocket, port);
}

void AgentServer::acceptConnection()
{
  bool didConnect = false;
  int theClient = 0;
  rlBuffer theBuffer = {0};
  rlBufferCreate(&theBuffer, sizeof(int) * 2);

  while (!didConnect) 
  {
    theClient = rlAcceptConnection(serverSocket);
    didConnect = this->onConnection(theClient, theBuffer);
  }

  rlClose(serverSocket);
  rlBufferDestroy(&theBuffer);
}

bool AgentServer::onConnection(int theClient, rlBuffer &theBuffer)
{
  bool isAgent = false;
  int theClientType = 0;

  rlRecvBufferData(theClient, &theBuffer, &theClientType);
    
  switch(theClientType) {
  case kAgentConnection:
    cerr << "agent connected" << endl; 
    rlSetAgentConnection(theClient);
    isAgent = true;
    break;
      
  default:
    cerr << "RL_network.c: Unknown Connection Type: " << theClientType << endl;
    break;
  };

  return isAgent;
}
