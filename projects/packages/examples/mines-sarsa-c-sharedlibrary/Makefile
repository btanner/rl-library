#If RL-Glue Core or the C/C++ Codecs are not installed to default locations
#You may need to use set the CFLAGS and LDFLAGS. 

#Current values of these two vars is supposed to be illustrative, not correct for you.
#This is what you would use if you installed the C/C++ Codec with:
# ./configure --prefix=/Users/btanner/rl-glue
# LDFLAGS := $(LDFLAGS) -L/Users/btanner/rl-glue/lib
# CFLAGS := $(CFLAGS) -I/Users/btanner/rl-glue/include/rlglue

all: OSX

OSX: 
	CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" make -f Makefile.agent OSX
	CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" make -f Makefile.environment OSX 

Linux:
	CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" make -f Makefile.agent Linux
	CFLAGS="$(CFLAGS)" LDFLAGS="$(LDFLAGS)" make -f Makefile.environment Linux

clean:
	make -f Makefile.agent  clean
	make -f Makefile.environment clean

