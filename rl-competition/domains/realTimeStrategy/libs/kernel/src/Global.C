// (c) Michael Buro, licensed under GPLv3

#include "Global.H"
#include <sstream>
#include <ctype.h>

using namespace std;

// fixme: no way of reporting an error condition!

int to_int(const string & str)
{
  istringstream is(str);
  int x; 
  is >> x;
  return x;
}

bool to_bool(const std::string & str)
{
  std::istringstream is(str);
  bool x; 
  is >> x;
  return x;  
}

#if 0
std::string trim(const std::string & str)
{
  site_t index1, index2; 
  
  FORU(i, str.length()) {
    if (!isspace(str[i])) {
      index1 = i; break;
    }      
  }
  
  for (int i = str.length()-1; i >= 0; i--) {
    if (!isspace(str[i])) {
      index2 = i; break;
    }
  }
  
  return str.substr(index1, index2-index1+1);
}
#endif

std::string join(const std::vector<std::string> & pieces, 
                 std::string delimiter, 
                 int start, 
                 int end)
{
  std::ostringstream oss; 
  
  int i = -1;
  
  for (i = start; i <= end; i++)
    oss << pieces[i] << delimiter; 
  
  std::string str = oss.str();
  if (i == start) return "";
  else return str.substr(0, str.length()-delimiter.length());
}

std::string join(const std::vector<std::string> & pieces, std::string delimiter)
{
  return join(pieces, delimiter, 0, pieces.size()-1);
}

std::string join(const std::vector<std::string> & pieces, 
                 std::string delimiter, 
                 int start)
{
  return join(pieces, delimiter, start, pieces.size()-1);
}

// btw, i found out there's a trim in boost :) 
string trim(const string & str)
{
  string::size_type pos1 = str.find_first_not_of(' ');
  string::size_type pos2 = str.find_last_not_of(' ');
  return str.substr(pos1 == string::npos ? 0 : pos1,
                    pos2 == string::npos ? str.length() - 1 : pos2 - pos1 + 1);
}

// I had to write this because boost::split is giving me memory corruptions 
void splitup(std::vector<std::string> & vec, const std::string & str, const std::string & delimiter)
{
  size_t i = 0; 
  
  FOREVER {
    size_t j = str.find(delimiter, i);
    
    if (j == std::string::npos) {
      if (i >= str.length()) 
        vec.push_back("");
      else
        vec.push_back(str.substr(i));
      
      break;
    }
      
    vec.push_back(str.substr(i, j-i));
    i = j + delimiter.length(); 
  }
}

double distance(int x1, int y1, int x2, int y2)
{
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
}


#if 0
char *alloc_sprintf(const char *fmt, ...) {
  va_list ap;
  va_start(ap, fmt);
  char *msg = alloc_sprintf(fmt, ap);
  va_end(ap);
  return msg;
}

char *alloc_sprintf(const char *fmt, va_list ap) {

  sint4 size = 100;
  char *p;
  va_list ap2;

  if ((p = (char*)malloc (size)) == NULL) return NULL;

  for (;;) {
    /* Try to print in the allocated space. */
    va_copy(ap2, ap);
    sint4 n = vsnprintf (p, size, fmt, ap2);
    va_end(ap2);
    /* If that worked, return the string. */
    if (n > -1 && n < size)
      return p;
    /* Else try again with more space. */
    if (n > -1)    /* glibc 2.1 */
      size = n+1; /* precisely what is needed */
    else           /* glibc 2.0 */
      size *= 2;  /* twice the old size */

    if ((p = (char*)realloc (p, size)) == NULL) return NULL;
  }
}

std::ostream &form(std::ostream &os, const char *fmt, ...)
{
  va_list ap;
  va_start(ap, fmt);
  form(os, fmt, ap);
  va_end(ap);
  return os;
}

std::ostream &form(std::ostream &os, const char *fmt, va_list ap)
{
  char *msg = alloc_sprintf(fmt, ap);
  if (msg == 0) ERR("out of memory");
  os << msg;
  free(msg);
  return os;
}

#endif


