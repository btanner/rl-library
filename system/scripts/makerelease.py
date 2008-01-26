#!/Library/Frameworks/Python.framework/Versions/Current/bin/python
#This file isn't in use yet, we have to fix it up to do uploads later
import os
import sys
import random
import optparse
import httplib
import getpass
import base64


def upload(file, project_name, user_name, password, summary, labels=None):
  """Upload a file to a Google Code project's file server.

  Args:
    file: The local path to the file.
    project_name: The name of your project on Google Code.
    user_name: Your Google account name.
    password: The googlecode.com password for your account.
              Note that this is NOT your global Google Account password!
    summary: A small description for the file.
    labels: an optional list of label strings with which to tag the file.

  Returns: a tuple:
    http_status: 201 if the upload succeeded, something else if an
                 error occured.
    http_reason: The human-readable string associated with http_status
    file_url: If the upload succeeded, the URL of the file on Google
              Code, None otherwise.
  """
  # The login is the user part of user@gmail.com. If the login provided
  # is in the full user@domain form, strip it down.
#Btanner: commenting this out because they now allow me@mydomain.com 
#  if '@' in user_name:
 #   user_name = user_name[:user_name.index('@')]

  form_fields = [('summary', summary)]
  if labels is not None:
    form_fields.extend([('label', l.strip()) for l in labels])

  content_type, body = encode_upload_request(form_fields, file)

  upload_host = '%s.googlecode.com' % project_name

  upload_uri = '/files'
  auth_token = base64.b64encode('%s:%s'% (user_name, password))

  headers = {
    'Authorization': 'Basic %s' % auth_token,
    'User-Agent': 'Googlecode.com uploader v0.9.4',
    'Content-Type': content_type,
    }

  server = httplib.HTTPSConnection(upload_host)
  server.request('POST', upload_uri, body, headers)
  resp = server.getresponse()
  server.close()

  if resp.status == 201:
    location = resp.getheader('Location', None)
  else:
    location = None
  return resp.status, resp.reason, location


def encode_upload_request(fields, file_path):
  """Encode the given fields and file into a multipart form body.

  fields is a sequence of (name, value) pairs. file is the path of
  the file to upload. The file will be uploaded to Google Code with
  the same file name.

  Returns: (content_type, body) ready for httplib.HTTP instance
  """
  BOUNDARY = '----------Googlecode_boundary_reindeer_flotilla'
  CRLF = '\r\n'

  body = []

  # Add the metadata about the upload first
  for key, value in fields:
    body.extend(
      ['--' + BOUNDARY,
       'Content-Disposition: form-data; name="%s"' % key,
       '',
       value,
       ])

  # Now add the file itself
  file_name = os.path.basename(file_path)
  f = open(file_path)
  file_content = f.read()
  f.close()

  body.extend(
    ['--' + BOUNDARY,
     'Content-Disposition: form-data; name="filename"; filename="%s"'
     % file_name,
     # The upload server determines the mime-type, no need to set it.
     'Content-Type: application/octet-stream',
     '',
     file_content,
     ])

  # Finalize the form body
  body.extend(['--' + BOUNDARY + '--', ''])

  return 'multipart/form-data; boundary=%s' % BOUNDARY, CRLF.join(body)

def releaseFile(projectName, baseRepoURL,releaseNumber,releaseType):
	trunkDir="trunk/";
	tagsDir="tags/versions/";
	branchDir="branches/";

	releaseDescription=releaseNumber+"-"+releaseType+"-"+projectName;
	
	tagURL=baseRepoURL+tagsDir+releaseDescription;
	branchURL=baseRepoURL+branchDir+releaseDescription;
	trunkURL=baseRepoURL+trunkDir;

	randFileSuffix=random.randint(0,5000);
	tmpDirName="tmpExportDir_"+str(randFileSuffix);


#adding code to delete if it's there in case we try and release because of a problem uploading
	rmExistingBranchCommand="svn rm "+branchURL+" -m 'removing dupe branch if exists'";
	rmExistingTagCommand="svn rm "+tagURL+" -m 'removing dupe tag if exists'";
	branchCommand="svn cp "+trunkURL+" "+branchURL+" -m 'Creating a release branch "+projectName+" "+releaseDescription+"'";
	tagCommand="svn cp "+branchURL+" "+tagURL+" -m 'Creating a tag for "+projectName+" "+releaseDescription+"'";
	mkDirCommand="mkdir "+tmpDirName;
	exportCommand="svn export "+tagURL+" "+tmpDirName+"/"+projectName;

	tarFileName=projectName+"_"+releaseType+"-"+releaseNumber+".tar";
	gzipFileName=tarFileName+".gz";

	archiveTarCommand="cd "+tmpDirName+";tar -cf "+tarFileName+" "+projectName+";cd ..";
	archiveGZIPCommand="gzip "+tmpDirName+"/"+tarFileName;
	gzipFile=tmpDirName+"/"+gzipFileName;

	cleanUpCommand="rm -Rf "+tmpDirName;

	Commands=[rmExistingBranchCommand, rmExistingTagCommand, branchCommand, tagCommand,mkDirCommand,exportCommand,archiveTarCommand,archiveGZIPCommand];
	
	

	print("\n-------------------------------\Executing the following :\n");
	for c in Commands:
		status=os.system(c);
		print "Status: "+str(status)+" : "+c;
		#256 is what subversion gives if we try to delete something not there, not worth dying over
		if(status and status !=256):
			print("Something bad happened, aborting!");
			sys.exit();
		
	return gzipFile,cleanUpCommand;
	

def main():
	projectName="rl-library";
	baseRepoURL="https://rl-library.googlecode.com/svn/";
	
	parser = optparse.OptionParser(usage='makeRelease.py -n NAME -t TYPE');
	parser.add_option('-n', '--name', dest='releaseNumber', help='Number of release, something like .1 or 5 for 1.0');
	parser.add_option('-t', '--type', dest='releaseType', help='Type of release, either something like ALPHA, BETA, or Official');
	parser.add_option('-u', '--username', dest='username', help='Your GoogleCode username that is allowed to upload files');
	parser.add_option('-p', '--password', dest='password', help='Your GoogleCode password');

	options, args = parser.parse_args()

	if not options.releaseNumber:
		parser.error('No release number provided. Use the -n option.')
	else:
		options.releaseNumber=str(options.releaseNumber);
	if not options.username:
		parser.error('No Username provided. Use the -u option.')
	if not options.password:
		parser.error('No Password provided. Use the -p option.')
	
	labels=["Type-Archive", "Featured", "OpSys-All"];
	 
	gzipFile, cleanUpCommand=releaseFile(projectName,baseRepoURL,options.releaseNumber,options.releaseType);
	
	summary="This is the archived version of "+projectName+" release "+options.releaseType+" "+options.releaseNumber;

	status, reason, url = upload(gzipFile, projectName,options.username, options.password,summary, labels);
	
	if url:
		print 'The file was uploaded successfully.'
		print 'URL: %s' % url
		status=os.system(cleanUpCommand);
		print "Status: "+str(status)+" : "+cleanUpCommand;
		if(~status):
			print("Temporary files all cleaned up... \n\n Code Released Successfully.");
		else:
			print("Problem cleaning up the file.")
	else:
		print 'An error occurred. Your file was not uploaded.'
		print 'Google Code upload server said: %s (%s)' % (reason, status)
	   

if __name__ == '__main__':
  main()