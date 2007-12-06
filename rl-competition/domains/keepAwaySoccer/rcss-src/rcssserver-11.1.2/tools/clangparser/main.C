/* -*- Mode: C++ -*- */
/*
 *Copyright:

    Copyright (C) 2001 RoboCup Soccer Server Maintainance Group.
    	Patrick Riley, Tom Howard, Itsuki Noda,	Mikhail Prokopenko, Jan Wendler 

    This file is a part of SoccerServer.

    This code is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 *EndCopyright:
 */
/* NOTE: This program is still under developement and is intended only to as a
   pre-release version provided to allow people to preview the coach language and
   semantics a bit.
*/

//#define DEBUG

#include <iostream>
#include <rcssserver/clangmsg.h>
#include <rcssserver/clangparser.h>
#include <rcssserver/coach_lang_comp.h>
#include <rcssserver/clangmsgbuilder.h>

const int max_mess_len = 10000;
char current_coach_message[max_mess_len];

int main (int argc, char** argv) 
{
  //#define SET_TEST
#ifdef SET_TEST
  UnumSet s;
  int x;
  
  while (cin >> x) {
    if (x >= 0)
      s.addNum(x);
    else
      s.removeNum(-x);
    cout << s << endl;
  }
  
#endif	
#define PARSE_TEST
#ifdef PARSE_TEST
  int ret;

  while (cin)
    {
      rcss::clang::MsgBuilder builder;
      rcss::clang::Parser parser( builder );
      //cout << "reading line" << endl;
      cin.getline( current_coach_message, max_mess_len );
      
      if( strlen( current_coach_message ) > 0 )
        {
          try
            {
              ret = parser.parse( current_coach_message );
              //cout << "Parsing finished\n";
            }
          catch( const rcss::clang::BuilderErr& e )
            {
              cerr << e << endl;
              ret = 1;
            }
          catch( const rcss::util::NullErr& e)
            {
              cerr << e.what() << endl;
              ret = 1;
            }
          cout << "return value: " << ret << endl;
          if( ret != 0 )
            cout << "Error parsing: " << current_coach_message << endl;
          if( builder.getMsg() == NULL ) 
            {
              cout << " *No message read" << endl;
            }
          else
            {
              cout << " *" << *(builder.getMsg()) << endl;
              int min = builder.getMsg()->getMinVer();
              int max = builder.getMsg()->getMaxVer();
              cout << "   - ver: ";
              if( min == max )
                cout << "only " << min << endl;
              else if ( min < max )
                cout << "all from " << min << " ~ " << max << endl;
              else 
                cout << "any from " << max << " ~ " << min << endl;

//                cout << "(7 7) Supported = " 
//                     << builder.getMsg()->isSupported( 7, 7 ) << endl;
//                cout << "(7 8) Supported = " 
//                     << builder.getMsg()->isSupported( 7, 8 ) << endl;
//                cout << "(8 8) Supported = " 
//                     << builder.getMsg()->isSupported( 8, 8 ) << endl;
            }
        }
      else
        cout << "Blank line\n";
    }      
  return ret;
#else

#endif
}
