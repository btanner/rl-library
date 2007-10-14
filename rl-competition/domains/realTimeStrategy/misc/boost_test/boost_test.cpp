
/* 
 * A simple test to see if Boost is properly 
 * installed. 
 */

#include <iostream>
#include <string>
#include <vector>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

using namespace std; 

int main()
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

