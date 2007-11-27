
/* 
 * A simple test to see if Boost is properly 
 * installed. 
 */

#include <iostream>
#include <string>
#include <vector>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

/*
#include <fstream>
#include <iostream>
#include <boost/iostreams/filtering_streambuf.hpp>
#include <boost/iostreams/copy.hpp>
#include <boost/iostreams/filter/bzip2.hpp>
*/

using namespace std; 

void test1()
{
  string str("This is a test");

  vector<string> tokens;
  boost::split(tokens, str, boost::is_any_of(" "));

  for (vector<string>::iterator iter = tokens.begin(); 
       iter != tokens.end();
       iter++)
  {
    cout << "Token: " << (*iter) << endl;
  }
}

/*
void test2()
{
  ifstream file("hello.bz2", ios_base::in | ios_base::binary);
  filtering_streambuf<input> in;
  in.push(bzip2_decompressor());
  in.push(file);
  boost::iostreams::copy(in, cout);
}
*/

int main()
{
  test1();
  //test2();
}

