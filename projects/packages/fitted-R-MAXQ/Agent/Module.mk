AGENT-SRCS := $(patsubst %, Agent/%.cc, state averager action primitive mdp planner predictor composite FittedRmaxq)
AGENT-OBJS := $(patsubst %.cc, %.o, $(AGENT-SRCS))
AGENT-LDFLAGS := -lrlutils -lrlagent -lrlgluenetdev

PRODUCT-SRCS += $(patsubst %, Agent/%.cc, $(AGENTS))
PRODUCT-OBJS += $(patsubst %, Agent/%.o, $(AGENTS))

%.agent : $(AGENT-OBJS) Agent/%.o
	$(CXX) $(CXXFLAGS) $(AGENT-LDFLAGS) -o $@ $^
