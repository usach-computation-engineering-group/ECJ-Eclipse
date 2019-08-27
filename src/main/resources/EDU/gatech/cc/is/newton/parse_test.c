#include <stdio.h>
#include <string.h>

#define START_OF_FRAME 36 /* ASCII for $ */ 
#define CHANNEL_A      35 /* ASCII for # */
#define CHANNEL_B      38 /* ASCII for & */
#define CHANNEL_C      94 /* ASCII for ^ */

main()
{
 int i, j, k;
 int area, angle, row, column;
 char thischar;
 FILE *inputfile;

   inputfile = fopen("newton.data", "r");
   if (inputfile==NULL)
   {
      printf("Can't open input file\n");
      exit(1);
   }

   while (!feof(inputfile))
   {
      while (thischar = ((char)fgetc(inputfile) != START_OF_FRAME) &&
              !(feof(inputfile)) );
      fscanf( inputfile, "%2x", &i );
      printf( "Found start of a data frame: %d\n", i );
      thischar = (char)fgetc(inputfile);
      switch( thischar )
      {
        case CHANNEL_A :
             printf("Found channel A\n");
             fscanf( inputfile, "%2x:%2x:%2x:%2x", 
                     &area, &column, &row, &angle );
             printf(" (x:%3d, y:%3d)\n", column, row ); 
             break;
 
        case CHANNEL_B :
             printf("Found channel B\n");
             fscanf( inputfile, "%2x:%2x:%2x:%2x", 
                     &area, &column, &row, &angle );
             printf(" (x:%3d, y:%3d)\n", column, row ); 
             break;

        case CHANNEL_C :
             printf("Found channel C\n");
             fscanf( inputfile, "%2x:%2x:%2x:%2x", 
                     &area, &column, &row, &angle );
             printf(" (x:%3d, y:%3d)\n", column, row ); 
             break;

        default:
            printf(" ***** Found this: %c *****\n", thischar);
      }
   
   }

   fclose( inputfile );

}
