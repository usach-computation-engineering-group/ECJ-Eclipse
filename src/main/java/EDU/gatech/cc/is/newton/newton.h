/* ****************************************************************************
 *  newton.h
 * 
 * API to access vision functions of the Newton Labs vision board
 * 
 * David Huggins
 * June 1997
 *
 * Copyright (c) 1997 Georgia Tech Research Corporation
 * All Rights Reserved
 *
 * ************************************************************************** */

#ifndef NEWTON_H
#define NEWTON_H


#include <stdlib.h>
#include <stdio.h>


#ifndef TRUE
#define TRUE  1
#endif
#ifndef FALSE
#define FALSE 0
#endif


/* Vision routine return codes */
#define NEWTON_SUCCESS         1
#define NEWTON_FAILURE        -1
#define NEWTON_IO_FAILURE     -2
#define NEWTON_UNINITIALIZED  -3


/* Predefined (and trained) search objects */
#define CHANNEL_A 0            /* Vision Channel A */
#define CHANNEL_B 1            /* Vision Channel B */
#define CHANNEL_C 2            /* Vision Channel C */
#define NUMBER_OF_TYPES 3


/* Which serial port to use for vision? */
#define PORT_A  1              /* maps to /dev/ttya or /dev/ttyS0 */
#define PORT_B  2              /* maps to /dev/ttyb or /dev/ttyS1 */
#define PORT_C  3              /* maps to /dev/ttyc or /dev/ttyS2 */


/* Global status variable */
extern int _NewtonInitialized_;


/* Global screen center variable */
extern float center_x, center_y;


/* Newton return structure */
typedef struct _NewtonLocationStruct_ {
     float x;       /* object's x-coordinate in meters */
     float y;       /* object's y-coordinate in meters */
     float axis;    /* object's major axis in radians  */
     float area;    /* object's area in square meters  */
} NewtonLocation_t;


/* The Newton vision system maintains an array of objects and updates the
   number of each type of object in the NumberFound vector.  In order to
   check on the objects seen by the camera, the user checks 
   NumberFound[object-type-of-interest] for a non-zero number.  Finding
   there are objects to look at, the user steps through the location
   structure(s) at FoundObjects[object-type-of-interest, 0..n].
 */



/* define the maximum number of objects of one type we'll look for */
#define MAX_OBJECTS 50

/* holds the number of each type of object just observed */
extern int NumberFound[NUMBER_OF_TYPES];

/* holds the location structure of each viewed object */
extern NewtonLocation_t FoundObjects[NUMBER_OF_TYPES][MAX_OBJECTS];

/* 
 *
 * real coordinate origin is the center of the robot
 * x increase along a radius directly in front of the robot
 *  as determined by the initialized state of the turret
 * y increases to the left 
 * z is upwardly normal to the floor
 *
 * screen coordinate [0,0] is at the upper left
 * rows increase to 255 towards the bottom
 * columns increase to 200 towards the right
 * UL = [0,0] LR=[255,200]
 *
 * temporary solution
 * assume each pixel is 1 cm in real space
 * set center pixel in locate_objects.c (center_x and center_y)
 * compute real_x = 1.00 - (0.01*(pix_x - center_x))
 * and     real_y = 0.01*(cneter_y - pix_x)
 *
 */


/* Newton screen coordinates return structure */
typedef struct ScreenLocationStruct {
     int x;       /* object's x-coordinate in pixels */
     int y;       /* object's y-coordinate in pixels */
     int axis;    /* object's major axis in radians  */
     int area;    /* object's area in square pixels  */
} ScreenLocation_t;

/* holds the location structure of each viewed object */
ScreenLocation_t LocalScreenObjects[NUMBER_OF_TYPES][MAX_OBJECTS];

extern int   newton_channel;
extern FILE *newton_file;

/* prototypes */

int newton_open_port( int port );
int newton_close_port(void);
void newton_exit(void);
void newton_debug_off(void);
void newton_debug_on(void);
void newton_start_tracking(void);
void newton_stop_tracking(void);
char newton_read_board(void);

int InitNewton( int port );
void NewtonParse(void);

#endif /* NEWTON_H */
