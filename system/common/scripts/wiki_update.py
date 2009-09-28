
#!/usr/bin/env python

"""MediaWiki Bot Update script.

This script is supposed to accept some commandline details about a project and then
use the MediaWiki API to update the RL-Library wiki page."""

__author__ = 'brian@tannerpages.com (Brian Tanner)'

import httplib
import os.path
import optparse
import getpass
import base64
import sys
import mwclient


def main():
  parser = optparse.OptionParser(usage='wiki-update.py ')
  parser.add_option('--wikipasswordfile', dest='passwordfile',
                    help='Path to a file with your RL-Library wiki bot username and password on a single line separated by a space. No newline at end of file.')
  parser.add_option('--filename', dest='downloadfilename',
                    help='Name of the file as it will appear in the Google Code download page.  Something like MountainCarR440.tar.gz')
  parser.add_option('--jarname', dest='jarname',
                    help='Name of the main jar that runs this project. Something like MountainCar.jar')
  parser.add_option('--projectdir', dest='projectdir',
                    help='Name of the folder that your project will unzip to.  Something like MountainCar.')
  parser.add_option('--wikipage', dest='wikipage',
                    help='Name (in wiki style) of the page to update.  Something like Mountain_Car_(Java)')
#  parser.add_option('-l', '--labels', dest='labels',
#                    help='An optional list of labels to attach to the file')

  options, args = parser.parse_args()

  if not options.downloadfilename:
    parser.error('Filename is missing.')
  elif not options.passwordfile:
    parser.error('Wiki password file is missing.')
  elif not options.projectdir:
    parser.error('ProjectDir is missing.')
  elif not options.jarname:
    parser.error('Jarname is missing.')
  elif not options.wikipage:
    parser.error('Destination wiki page to edit is missing.')

  f = file(options.passwordfile)
  for line in f:
    theSplit=line.split(" ");
    bot_name=theSplit[0]
    bot_password=theSplit[1]

#  print 'Going to update the page with jarname='+options.jarname+' and filename='+options.downloadfilename+' and wikipage='+options.wikipage

  site = mwclient.Site('beta.library.rl-community.org',path='/')
  site.login(bot_name, bot_password)
  page = site.Pages[options.wikipage]

  text = page.edit() 
  text=  text.encode('utf-8')
  #print 'Text in page:', text.encode('utf-8')
  startToken=text.find('SAR=1|')
  endToken=text.find('|EAR=1')
  if endToken<0 or startToken<0:
    print 'Could not find SAR=1 and EAR=1 which we need to do WIKI updating.'
    sys.exit(1)

  startToken=startToken+6
  beforeText=text[0:startToken]
  afterText=text[endToken:]
  newText=beforeText+'Filename='+options.downloadfilename+'|Jarname='+options.jarname+'|Projectdir='+options.projectdir+afterText
  page.save(newText, summary = 'Test edit')
  print 'Changes have been saved.'



if __name__ == '__main__':
  sys.exit(main())
