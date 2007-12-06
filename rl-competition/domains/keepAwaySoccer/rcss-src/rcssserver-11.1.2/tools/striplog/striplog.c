/*
 
  FILE: striplog.c 
  
  AUTHOR: Tom Howard 
  
          Modifed from exteamname.c by
          Marc Butler  

  DATE: 2000-08-24
 
  DESC:
 
  This program was written for Robocup2000 in Melbourne.
 
  It strips message info from logs

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
  FILE*  log_fp = NULL;
  FILE* save_fp = NULL;
  
  if (argc != 3) {
    printf("\nUsage: %s oldlogfile newlogfile\n", argv[0]);
    exit(1);
  }

  /* Open the log. */

  log_fp = fopen(argv[1], "r");
  if (log_fp == NULL) {
    char tmp[128];
    sprintf(tmp, "Error open log file \"%s\" for reading ", argv[1]);
    exit(1);
  }
  save_fp = fopen (argv[2], "w");
  if (save_fp == NULL) {
    char tmp[128];
    sprintf(tmp, "Error open log file \"%s\" for writing ", argv[1]);
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

  fwrite(magic, sizeof (char), MAGIC_SIZE, save_fp);
  
  /* Main processing loop for the log file. */
  while (1)
    {
        short mode = 0;
    showinfo_t showinfo;

    /* MSG_MODE data. */
    short board = 0;
    short length = 0;
    char msg[4096];

    if (fread(&mode, sizeof (short), 1, log_fp) != 1) {
      //if (!found) {
      //printf("\nError exhausted log file before finished.\n");
      //exit(1);
      //}
      //else {
	break;
	//}
    }

    switch (ntohs(mode)) {

    case SHOW_MODE:
      fread(&showinfo, sizeof (showinfo), 1, log_fp);
      fwrite(&mode, sizeof (short), 1, save_fp);
      fwrite(&showinfo, sizeof (showinfo), 1, save_fp);
      printf("cycle = %d\n", ntohs(showinfo.time));
     
      break;

    case MSG_MODE:
      /* Read the message and ignore. */
      fread(&board, sizeof (short), 1, log_fp);
      fread(&length, sizeof (short), 1, log_fp);
      fread(&msg, sizeof (char), ntohs(length), log_fp);
      break;

    }
    
 
    }

  fclose (save_fp);
  fclose (log_fp);
  return (0);
}
