/* ****************************************************************************
 *  newton_parse.c
 *
 * Parse the Newton's serial data stream into a useful format.
 *
 * David Huggins
 * July 1997
 *
 * Copyright (c) 1997 Georgia Tech Research Corporation
 * All Rights Reserved
 *
 * ************************************************************************** */

#include <stdio.h>
#include <string.h>
#include <sys/ioctl.h>

#include "newton.h"

#define START_OF_FRAME_CHAR 36 /* ASCII for $ */ 
#define CHANNEL_A_CHAR      35 /* ASCII for # */
#define CHANNEL_B_CHAR      38 /* ASCII for & */
#define CHANNEL_C_CHAR      94 /* ASCII for ^ */
#define MAX_OBJS            4 

void NewtonParse()
{
 static char lastchar = '0';
 char thischar;
 char dummy;
 int  chan, num;
 int  i, j, k;
 int  frame;
 int  ret_count;
 int  a, x, y, ax;
 int  otype;

#ifdef NEWTONDEBUG
   printf( "NewtonParse: looking for a frame.." );
#endif
   i = j = k = 0;

   /* flush everything first */
   /* fpurge(newton_file);  doesn't seem to work on linux*/
   if ( ioctl(newton_channel, TCFLSH, (void *)TCIOFLUSH) < 0) {
     fprintf(stderr,"NewtonParse: error flushing port\n");
   }

   /* look for the start of a data frame */
   thischar = lastchar;
   while (thischar != START_OF_FRAME_CHAR)
	thischar = (char)fgetc(newton_file);

   /* discard the frame number */
   fscanf( newton_file, "%2x", &frame );

#ifdef NEWTONDEBUG
   printf( "NewtonParse: found frame number: %d\n", frame );
#endif

   /* look for the channel indicator */
   thischar = (char)fgetc(newton_file);
   while (thischar != START_OF_FRAME_CHAR) {
     ret_count = fscanf( newton_file, "%2x:%2x:%2x:%2x", 
			 &a, &x, &y, &ax);
     /* make sure we got all the data */
     if (ret_count == 4) {
       switch( thischar ) {
       case CHANNEL_A_CHAR :
	 num = i++;
	 chan = CHANNEL_A;
	 break;
       case CHANNEL_B_CHAR :
	 num = j++;
	 chan = CHANNEL_B;
	 break;
       case CHANNEL_C_CHAR :
	 num = k++;
	 chan = CHANNEL_C;
	 break;
       default: chan = -1;
       }
       if ((chan>=0)&&(chan<NUMBER_OF_TYPES)&&(num<MAX_OBJECTS)) {
#if NEWTONDEBUG
	 printf( "parsed chan %d %d (x %d, y %d) - area %d axis %d\n", 
		 chan, num,       x,    y,         a,      ax);
#endif
	 LocalScreenObjects[chan][num].area = a;
	 LocalScreenObjects[chan][num].y    = y;
	 LocalScreenObjects[chan][num].x    = x;
	 LocalScreenObjects[chan][num].axis = ax;
       }

       if (num >= MAX_OBJS) break;
     }
     else {
       /*-- printf("Not 4 - fscanf failed: %d\n",ret_count); --*/

       /* look for next frame */

       thischar = (char)fgetc(newton_file);
       while (thischar != START_OF_FRAME_CHAR)
	 thischar = (char)fgetc(newton_file);
       if ( fscanf( newton_file, "%2x", &frame )!=1 )
	 printf("did not get next frame\n");
     }
     thischar = (char)fgetc(newton_file);
   }
   lastchar = thischar;

   /* update the counter */
   NumberFound[CHANNEL_A] = i;
   NumberFound[CHANNEL_B] = j;
   NumberFound[CHANNEL_C] = k;

#ifdef NEWTONDEBUG
   printf("ParseNewton: number of items %d %d %d\n",i,j,k);
#endif

#ifdef NEWTONDEBUG
   printf( "ParseNewton completed a frame\n" );
   for (otype=0; otype<NUMBER_OF_TYPES; otype++) {
     for (i=0; i<NumberFound[otype]; i++) {
       printf( "channel %d - (x %d,y %d ) axis %d area %d\n",
	       otype,
	       LocalScreenObjects[otype][i].x,
	       LocalScreenObjects[otype][i].y,
	       LocalScreenObjects[otype][i].axis,
	       LocalScreenObjects[otype][i].area );
     }
   }
#endif

} /* ***** NEWTON_PARSE.C ***** */
