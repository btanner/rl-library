
CPPFLAGS=-I../../../system/boost/include -I$(HOME)/include -I/usr/local/include

boost_test: boost_test.o
	g++ -o boost_test boost_test.o

boost_test.o: boost_test.cpp
	g++ $(CPPFLAGS) -c boost_test.cpp

clean:
	rm -f *.o boost_test
