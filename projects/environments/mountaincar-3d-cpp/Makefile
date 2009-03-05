#If RL-Glue Core or the C/C++ Codecs are not installed to default locations
#You may need to use set the CFLAGS and LDFLAGS. 

#If, for example, you installed the C/C++ Codec with:
# ./configure --prefix=/Users/btanner/rl-glue

#Then, you should uncomment the next two lines
#EXTRA_LDFLAGS = -L/Users/btanner/rl-glue/lib
#EXTRA_CFLAGS = -I/Users/btanner/rl-glue/include/rlglue

LDFLAGS := $(EXTRA_LDFLAGS) -lrlutils -lrlenvironment -lrlgluenetdev
CFLAGS := $(EXTRA_CFLAGS) -Wall -pedantic

all: mountaincar-3d

mountaincar-3d: mountaincar-3d.o 
	$(CXX) mountaincar-3d.o $(LDFLAGS) -o mountaincar-3d      

mountaincar-3d.o: mountaincar-3d.cc
	$(CXX)  $(CFLAGS) -c mountaincar-3d.cc -o mountaincar-3d.o 

clean:
	rm -rf mountaincar-3d mountaincar-3d.exe *.o









