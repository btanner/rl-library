#ifndef AgentServer_h
#define AgentServer_h

#include "RL_network.h"

class AgentServer
{
protected:
  int serverSocket;
  bool onConnection(int client, rlBuffer &buffer);

public:
  void startServer();
  void acceptConnection();
};

#endif
