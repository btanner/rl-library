/*
 
  FILE: exteamname.c 
  
  AUTHOR: Marc Butler 

  DATE: 2000-08-24
 
  DESC:
 
  This program was written for Robocup2000 in Melbourne.
 
  It reads the record.log created by the server and extracts the two
  team names and the final(?) score. Printing the out in the format:

  <team 1> <team1 goals> <team2> <team2 goals>

  Compile with:
  gcc -Wall -O2 exteamname.c -o exteamname

 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <netinet/in.h>

#define LOGFILE_MAGIC "ULG"
#define MAGIC_SIZE 4

/* Order is important. */
#include "param.h"
#include "types.h"

int main(int argc, char *argv[])
{
  char magic[4];
  team_t team1;
  team_t team2;
  FILE*  log_fp = NULL;
  int found = 0;

  if (argc != 2) {
    printf("\nUsage: %s logfile\n", argv[0]);
    exit(1);
  }

  /* Open the log. */

  log_fp = fopen(argv[1], "r");
  if (log_fp == NULL) {
    char tmp[128];
    sprintf(tmp, "Error open log file %s: ", argv[1]);
    exit(1);
  }

  /* Check the log file magic entry. */

  if (fread(magic, sizeof (char), MAGIC_SIZE, log_fp) != MAGIC_SIZE) {
    printf("\nError reading log file magic.\n");
    exit(1);
  }

  if (strncmp(magic, LOGFILE_MAGIC, 3) != 0) {
    printf("\nError invalid log file magic: %s\n", magic);
    exit(1);
  }

  if (magic[3] != 2) {
    printf("\nError can't handle log files of version: %d\n", magic[3]);
    exit(1);
  }

  /* Main processing loop for the log file. */
  while (1) {

    short mode = 0;
    showinfo_t showinfo;

    /* MSG_MODE data. */
    short board = 0;
    short length = 0;
    char msg[4096];

    if (fread(&mode, sizeof (short), 1, log_fp) != 1) {
      if (!found) {
	printf("\nError exhausted log file before finished.\n");
	exit(1);
      }
      else {
	break;
      }
    }

    switch (ntohs(mode)) {

    case SHOW_MODE:
      fread(&showinfo, sizeof (showinfo), 1, log_fp);

      /* This is necessary as the last cycle is sometimes absent. */
      if (ntohs(showinfo.time) >= 5999) {
	memcpy(&team1, &showinfo.team[0], sizeof (team_t));
	memcpy(&team2, &showinfo.team[1], sizeof (team_t));
	found = 1;
      }
      break;

    case MSG_MODE:
      /* Read the message and ignore. */
      fread(&board, sizeof (short), 1, log_fp);
      fread(&length, sizeof (short), 1, log_fp);
      fread(&msg, sizeof (char), ntohs(length), log_fp);
      break;

    }
  }
  
  if (found)
    printf("%s %d %s %d\n", 
	   team1.name, ntohs(team1.score), 
	   team2.name, ntohs(team2.score));
  else 
    exit(1);

  return (0);
}
