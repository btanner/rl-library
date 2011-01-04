EXPERIMENT-SRCS := $(patsubst %, Experiment/%.cc, )
EXPERIMENT-OBJS := $(patsubst %.cc, %.o, $(EXPERIMENT-SRCS))
EXPERIMENT-LDFLAGS := -lrlexperiment -lrlutils -lrlgluenetdev

PRODUCT-SRCS += $(patsubst %, Experiment/%.cc, $(EXPERIMENTS))
PRODUCT-OBJS += $(patsubst %, Experiment/%.o, $(EXPERIMENTS))

%.exp: $(EXPERIMENT-OBJS) Experiment/%.o
	$(CXX) $(CXXFLAGS) $(EXPERIMENT-LDFLAGS) -o $@ $^
