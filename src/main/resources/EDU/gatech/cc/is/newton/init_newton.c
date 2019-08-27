/* ****************************************************************************
 *  init_newton.c
 * 
 * Initialize Newton Labs vision board and setup the serial port
 * 
 * David Huggins
 * June 1997
 *
 * Copyright (c) 1997 Georgia Tech Research Corporation
 * All Rights Reserved
 *
 * ************************************************************************** */

#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "newton.h"

/* global variables */

int              NumberFound[NUMBER_OF_TYPES];
NewtonLocation_t FoundObjects[NUMBER_OF_TYPES][MAX_OBJECTS];


int _NewtonInitialized_ = FALSE; 
float center_x, center_y;

int InitNewton( int port )
{

 char  *tmp_ptr;
 char  *init_file_name = "~demo/newton/newton.cfg.ammadeus";
 int   pix_x, pix_y;
 int   j;
 float real_x, real_y;
 FILE  *init_file;
 int   otype;

   if (_NewtonInitialized_)
      return( NEWTON_SUCCESS );

#if 0
   /* Check for the NewtonInitFile environment variable */
   tmp_ptr = getenv( "NewtonInitFile" );
   if ( (tmp_ptr != NULL) && (strlen( tmp_ptr ) > 0) )
   {
      init_file_name = (char *)malloc(strlen( tmp_ptr ));
      strcpy( init_file_name, tmp_ptr );
   }
   printf("got env var: %s\n", init_file_name);
   init_file = fopen( init_file_name, "r" );
   printf("opened file \n");
   if (init_file == NULL) 
      return( NEWTON_FAILURE );
   printf("file is not null--OK\n");

   /* read the training file to set up the bilinear interpolation routines */
   fscanf( init_file, "%d %d %f %f\n", pix_x, pix_y, real_x, real_y );
#endif

   center_x = 128;
   center_y = 100;
  
   /* initialize the global data arrays */
   for (otype=0; otype<NUMBER_OF_TYPES; otype++)
   {
       NumberFound[otype] = 0;
       for (j=0; j<MAX_OBJECTS; j++)
       {
           FoundObjects[otype][j].x = 0.0;
           FoundObjects[otype][j].y = 0.0;
           FoundObjects[otype][j].axis = 0.0;
           FoundObjects[otype][j].area = 0.0;
       }
   }
   /* configure and set up the serial port */
   if (newton_open_port( port ) != NEWTON_SUCCESS)
	{
	printf("InitNewton: error in opening port\n");
	return( NEWTON_IO_FAILURE );
	}
   printf("serial port opened \n");

   _NewtonInitialized_ = TRUE;

#if 0
   fclose( init_file );
#endif
   return( NEWTON_SUCCESS );

} /* ***** INIT_NEWTON.C ***** */
