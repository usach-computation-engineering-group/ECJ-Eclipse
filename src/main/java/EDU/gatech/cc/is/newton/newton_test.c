/* ****************************************************************************
 *  newton_test.c
 * 
 * test the newton interface 
 * 
 * David Huggins
 * June 1997
 *
 * Copyright (c) 1997 Georgia Tech Research Corporation
 * All Rights Reserved
 *
 * ************************************************************************** */

#include "newton.h"

#include <stdio.h>

int main( int argc, char* argv[] )
{
 int i, status;
 int    otype;

   status = InitNewton( 3 );
   if (status == NEWTON_SUCCESS)
      printf( "newton_test:InitNewton succeeded\n" );
   else
   {
      printf( "newton_test:InitNewton failed %d\n", status );
      exit( NEWTON_FAILURE );
   }

   while( TRUE )
   {
//      printf( "main: calling NewtonParse\n");
      NewtonParse();

      for (otype=0; otype<NUMBER_OF_TYPES; otype++)
	{
            printf( "Found %d of type %d\n" , NumberFound[otype], otype);

            for (i=0; i<NumberFound[otype]; i++)
            {
               printf( "channel %d - (x %d,y %d ) axis %d area %d\n", 
			otype,
                        LocalScreenObjects[otype][i].x, 
			LocalScreenObjects[otype][i].y,
                        LocalScreenObjects[otype][i].axis,
                        LocalScreenObjects[otype][i].area );
            }
	}
   }

} /* newton_test() */

