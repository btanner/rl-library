# The agent code uses a subset of the Boost library, found here.
BOOSTDIR := .

# Compiler flags
CXXFLAGS := -ansi -pedantic -Wall -Werror -I$(BOOSTDIR)

# __gnu_cxx::hash_set and hash_map should be replaced by unordered_set and _map
CXXFLAGS += -Wno-deprecated

# For installations of RL Glue and the C++ Codec outside the default path
#CXXFLAGS += -I/path/to/include
#EXTRA-LDFLAGS := -L/path/to/lib

# For optimized code
CXXFLAGS += -DNDEBUG -O3

# For debugging
# CXXFLAGS += -g

# All available RL Glue components, which each produce an executable
AGENTS := FittedRmax FittedRmaxq-FPWorld Rmaxq-Taxi Rmax FittedRmax-FPWorld Rmax-FPWorld
ENVIRONMENTS := PuddleWorld FlagPuddleWorld Taxi MountainCar
EXPERIMENTS := SingleRun PolicyStep

# Define binary executables (located at top level)
AGENT-PRODUCTS := $(addsuffix .agent, $(AGENTS))
ENVIRONMENT-PRODUCTS := $(addsuffix .env, $(ENVIRONMENTS))
EXPERIMENT-PRODUCTS := $(addsuffix .exp, $(EXPERIMENTS))
PRODUCTS := $(AGENT-PRODUCTS) $(ENVIRONMENT-PRODUCTS) $(EXPERIMENT-PRODUCTS)
PRODUCT-SRCS := # Defined recursively
PRODUCT-OBJS := # Defined recursively

# Specify subdirectories containing Module.mk files to include
# These files will append to PRODUCT-SRCS and PRODUCT-OBJS
MODULES := Agent Environment Experiment

.PHONY: all clean run

.PRECIOUS: %.o

# By default, build all RL Glue components.
all: $(PRODUCTS)

# Include recursive make definitions.
include $(addsuffix /Module.mk, $(MODULES))

clean:
	rm -f $(AGENT-OBJS) $(ENVIRONMENT-OBJS) $(EXPERIMENT-OBJS) $(PRODUCT-OBJS) $(PRODUCTS)

# Run the RL Glue experiment with the first listed agent, environment, and experiment.
AGENT := $(firstword $(AGENT-PRODUCTS))
ENVIRONMENT := $(firstword $(ENVIRONMENT-PRODUCTS))
EXPERIMENT := $(firstword $(EXPERIMENT-PRODUCTS))
run: $(AGENT) $(ENVIRONMENT) $(EXPERIMENT)
	./$(AGENT) &
	./$(ENVIRONMENT) &
	./$(EXPERIMENT) &
	rl_glue
