#If RL-Glue Core or the C/C++ Codecs are not installed to default locations
#You may need to use set the CFLAGS and LDFLAGS. 

#If, for example, you installed the C/C++ Codec with:
# ./configure --prefix=/Users/btanner/rl-glue

#Then, you should uncomment the next two lines
#EXTRA_LDFLAGS = -L/Users/btanner/rl-glue/lib
#EXTRA_CFLAGS = -I/Users/btanner/rl-glue/include/rlglue

LDFLAGS := $(EXTRA_LDFLAGS) -lrlutils -lrlenvironment -lrlgluenetdev
CFLAGS := $(EXTRA_CFLAGS) -Wall -pedantic

all: sarsa-lambda-tilecoding-cpp

mountaincar-3d: sarsa-lambda-tilecoding-cpp.o 
        $(CXX) sarsa-lambda-tilecoding-cpp.o $(LDFLAGS) -o sarsa-lambda-tilecoding-cpp      

mountaincar-3d.o: sarsa-lambda-tilecoding-cpp.cpp
        $(CXX)  $(CFLAGS) -c sarsa-lambda-tilecoding-cpp.cpp -o sarsa-lambda-tilecoding-cpp.o 

clean:
        rm -rf sarsa-lambda-tilecoding-cpp sarsa-lambda-tilecoding-cpp.exe *.o
