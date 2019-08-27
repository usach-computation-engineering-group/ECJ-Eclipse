/*---------------------------------------------------------------------------- 

	newton.c

	(c) 1992 Tucker Balch

	modifications added by Darrin Bentivigna and Gary Boone 
        more modifications by David Huggins

	Library of serial routines for the newton board. 


----------------------------------------------------------------------------*/ 

#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <sys/termio.h>
#include <sys/ioctl.h>
#include <ctype.h>
#include <math.h>

#include "newton.h"

#define TTYA "/dev/ttya"
#define TTYB "/dev/ttyb"
#define TTYC "/dev/ttyc"
#define TTY_NUM 0
#define NO_ANSWER -999
#define MAX_RETRY2	4
#define MAX_RETRY1	10

/*  global varibales */

int   newton_channel;
FILE *newton_file;

/* private global variables */

static int	NUM_RETRIES = 2;
static int	NUM_RESYNCS = 2;
static char	newton_mesg[80];
static FILE	*tty_log;
static int	newton_debug;
static float	oldx, oldy, oldz, olda, oldt; 

FILE *output_file;
/*---------------------------------------------------------------------------- 

	newton_open_port()

	open_port: open and configure the serial port
  	9600 baud, 7 bits, odd parity, 1 stop, XON/XOFF enabled 

----------------------------------------------------------------------------*/ 

int newton_open_port( int port )
{
 struct termio termset;
 char	tty[80];
 char	msg[80];
 int	done;
 unsigned char rsp[80];
 unsigned char rsp_len;
 unsigned short status;

   switch( port )
   {
      case PORT_A :
         strcpy(tty, TTYA);
         break;
      case PORT_B :
         strcpy( tty, TTYB );
         break;
      case PORT_C :
         strcpy( tty, TTYC );
         break;
      default :
	 printf("illegal port number \n");
         return( NEWTON_IO_FAILURE );
   }
//   printf("using tty%s\n",tty);
   /*--- open tty line to the newton board ---*/
#ifndef NEWTONDEBUG
   if ((newton_channel = open(tty, O_NOCTTY | O_RDWR)) < 0) 
   {
      sprintf(msg, "newton:open_port - Error opening serial port >%s<", tty);
      perror(msg);
      return ( NEWTON_IO_FAILURE );
   }
#endif

#ifdef NEWTONDEBUG
   newton_file = fopen( "newton.data", "r" );
   if (newton_file == NULL)
   {
      sprintf(msg, "newton_open_port: unable to open data file" );
      perror( msg );
      return( NEWTON_IO_FAILURE );
   }
#else
   newton_file = fdopen(newton_channel, "r+"); 
#endif

#ifndef NEWTONDEBUG
   memset((char *) &termset, 0, sizeof(struct termio)); 

   /*--- don't try this at home!!!!! ---*/
   termset.c_iflag = IGNBRK | INPCK | IXON | IXOFF;
   termset.c_oflag = 0;
   termset.c_cflag = (unsigned short)(B38400 | CS8 | CLOCAL | CREAD );
   termset.c_cc[VMIN] = 20;
   termset.c_cc[VTIME] = 0;	/* 1/10 second timeout on reads */ 
   

   /*--- do the ioctl call to set the line up ---*/
    if (ioctl(newton_channel, TCSETA, (char *) &termset) < 0) 
    {
       perror("newton_open_port: open_port - Error setting serial port for newton"); 
       return ( NEWTON_IO_FAILURE );
    }
#endif

   printf("newton_open_port: port opened\n"); 
   return( NEWTON_SUCCESS );

}/* newton_open_port() */

/*---------------------------------------------------------------------------- 

	newton_close_port()
        close the serial link to the port

----------------------------------------------------------------------------*/ 

int newton_close_port(void)
{

   /* make sure port was opened */
   if (newton_channel == 0)
      return( NEWTON_FAILURE );

   close(newton_channel);

   /* mark as closed */
   newton_channel = 0;

   return( NEWTON_SUCCESS );

} /* newton_close_port() */

/*---------------------------------------------------------------------------- 

	newton_exit()

----------------------------------------------------------------------------*/ 

void newton_exit(void)
{
   newton_close_port();
}

/*---------------------------------------------------------------------------- 

	newton_debug_off()
	
----------------------------------------------------------------------------*/ 

void newton_debug_off(void)
{
   newton_debug = 0;
}

/*---------------------------------------------------------------------------- 

	newton_debug_on()
	
----------------------------------------------------------------------------*/ 

void newton_debug_on(void)
{
   newton_debug = 1;
}

/*---------------------------------------------------------------------------- 

       newton_start_tracking()


----------------------------------------------------------------------------*/ 

void newton_start_tracking(void)
{
   fprintf( newton_file, "r" );

}

/*---------------------------------------------------------------------------- 

       newton_stop_tracking()


----------------------------------------------------------------------------*/ 

void newton_stop_tracking(void)
{
   fprintf( newton_file, "q" );

}

/*---------------------------------------------------------------------------- 

       char newton_read_board()


----------------------------------------------------------------------------*/ 

char newton_read_board(void)
{

char dummy;

   dummy = (char)fgetc(newton_file);
   return(dummy);

} /* newton_read_board() */


/* ***** NEWTON_SERIAL.C ***** */
