

clangparser: main.C Makefile /usr/local/lib/rcssserver/librcssclangparser.so.2.0.0
	g++ -g main.C -L/usr/local/lib/rcssserver/ \
-Wl,--rpath -Wl,/usr/local/lib/rcssserver -lrcssclangparser \
-o clangparser -DDEBUG

clean:
	rm -rf clangparser
