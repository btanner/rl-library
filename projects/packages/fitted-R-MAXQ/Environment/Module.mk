ENVIRONMENT-SRCS := $(patsubst %, Environment/%.cc, gridworld)
ENVIRONMENT-OBJS := $(patsubst %.cc, %.o, $(ENVIRONMENT-SRCS))
ENVIRONMENT-LDFLAGS := $(EXTRA-LDFLAGS) -lrlutils -lrlenvironment -lrlgluenetdev

PRODUCT-SRCS += $(patsubst %, Environment/%.cc, $(ENVIRONMENTS))
PRODUCT-OBJS += $(patsubst %, Environment/%.o, $(ENVIRONMENTS))

%.env: $(ENVIRONMENT-OBJS) Environment/%.o
	$(CXX) $(CXXFLAGS) $(ENVIRONMENT-LDFLAGS) -o $@ $^
