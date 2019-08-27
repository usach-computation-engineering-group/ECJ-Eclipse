/*
 * Newton_interface.c
 *
 * These routines implement the Java native routines specicfied by
 * running javah on Ndirect.java .
 *
 * AUTHOR: Tucker Balch, tucker@cc.gatech.edu
 *
 */

#include <jni.h>
#include "EDU_gatech_cc_is_newton_Newton.h"
#include "newton.h"

static  int     let_me_live = 99;
static  jclass  static_placeholder = 0;

/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    make_resident
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_newton_Newton_make_1resident(
        JNIEnv *env,
        jobject obj)
        {
        /*
         * This code makes sure our static variables stay around.
         * Lifted from the javasoft page.
         */
        if (static_placeholder == 0)
                {
                jclass dummy = (*env)->GetObjectClass(env, obj);
                if (dummy == 0)
                        {
                        fprintf(stderr, "Ndirect_interface: error "
                                "trying to be static\n");
                        return(1);/* throw an exception */
                        }
                static_placeholder = (*env)->NewGlobalRef(env, dummy);
                if (static_placeholder == 0)
                        {
                        fprintf(stderr, "Ndirect_interface: error "
                                "trying to be static\n");
                        }
                return(0);/* OK */
                }
        }



/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    make_free
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_newton_Newton_make_1free(
        JNIEnv *env,
        jobject obj)
        {
        /*
         * This code releases the global reference so gc can get rid
         * of us.
         */
        if (static_placeholder != 0)
                {
                (*env)->DeleteGlobalRef(env, static_placeholder);
                }
        fprintf(stderr, "Ndirect_interface: releasing\n");
        return(0);
        }


/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    open_newton
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_newton_Newton_open_1newton
  (
        JNIEnv *env,
        jobject obj,
        jint serial_port,
        jint baud)
        {
        /*
         * Open the serial connection.
         */
	//baud is ignored now
        if (InitNewton(serial_port)!=NEWTON_SUCCESS)
                return(1);/*error!*/
	}


/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    read_frame
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_EDU_gatech_cc_is_newton_Newton_read_1frame
  (
        JNIEnv *env,
        jobject obj)
        {
	NewtonParse();
	}

/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    getNumVis
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_newton_Newton_getNumVis
  (JNIEnv *env, jobject obj, jint chan)
	{
	return(NumberFound[chan]);
	}
	

/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    getX
 * Signature: (I[I)V
 */
JNIEXPORT void JNICALL Java_EDU_gatech_cc_is_newton_Newton_getX
  (JNIEnv *env, jobject obj, jint chan, jintArray readings)
	{
        int i;
        jint *readings_body;
	jsize len;
	/* check arguments */
	if ((chan > 2)||(chan<0))
		{
                fprintf(stderr,"Newton.getX: illegal channel number: %d\n",
                        chan);
		return;
		}
        /* get ready to access the java array "readings" */
        len = (*env)->GetArrayLength(env, readings); /* how long is it?*/
        if ((len<NumberFound[chan])||(len>NumberFound[chan]))
                {
                fprintf(stderr,"Newton.getX: array wrong size: %d",len);
		return;
                }
        /* get pointer to the array, and reserve it. must release it later */
        readings_body = (*env)->GetIntArrayElements(env, readings, 0);
        /* move the readings into the array */
        for (i = 0; i<len; i++)
                readings_body[i] = LocalScreenObjects[chan][i].x;
        /* release the array */
        (*env)->ReleaseIntArrayElements(env, readings, readings_body, 0);
	}


/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    getY
 * Signature: (I[I)V
 */
JNIEXPORT void JNICALL Java_EDU_gatech_cc_is_newton_Newton_getY
  (JNIEnv *env, jobject obj, jint chan, jintArray readings)
	{
        int i;
        jint *readings_body;
	jsize len;
	/* check arguments */
	if ((chan > 2)||(chan<0))
		{
                fprintf(stderr,"Newton.getX: illegal channel number: %d\n",
                        chan);
		return;
		}
        /* get ready to access the java array "readings" */
        len = (*env)->GetArrayLength(env, readings); /* how long is it?*/
        if ((len<NumberFound[chan])||(len>NumberFound[chan]))
                {
                fprintf(stderr,"Newton.getY: array wrong size: %d",len);
		return;
                }
        /* get pointer to the array, and reserve it. must release it later */
        readings_body = (*env)->GetIntArrayElements(env, readings, 0);
        /* move the readings into the array */
        for (i = 0; i<len; i++)
                readings_body[i] = LocalScreenObjects[chan][i].y;
        /* release the array */
        (*env)->ReleaseIntArrayElements(env, readings, readings_body, 0);
	}

/*
 * Class:     EDU_gatech_cc_is_newton_Newton
 * Method:    getArea
 * Signature: (I[I)V
 */
JNIEXPORT void JNICALL Java_EDU_gatech_cc_is_newton_Newton_getArea
  (JNIEnv *env, jobject obj, jint chan, jintArray readings)
	{
        int i;
        jint *readings_body;
	jsize len;
	/* check arguments */
	if ((chan > 2)||(chan<0))
		{
                fprintf(stderr,"Newton.getArea: illegal channel number: %d\n",
                        chan);
		return;
		}
        /* get ready to access the java array "readings" */
        len = (*env)->GetArrayLength(env, readings); /* how long is it?*/
        if ((len<NumberFound[chan])||(len>NumberFound[chan]))
                {
                fprintf(stderr,"Newton.getX: array wrong size: %d",len);
		return;
                }
        /* get pointer to the array, and reserve it. must release it later */
        readings_body = (*env)->GetIntArrayElements(env, readings, 0);
        /* move the readings into the array */
        for (i = 0; i<len; i++)
                readings_body[i] = LocalScreenObjects[chan][i].area;
        /* release the array */
        (*env)->ReleaseIntArrayElements(env, readings, readings_body, 0);
	}

