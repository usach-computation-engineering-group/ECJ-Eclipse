/*
 * Ndirect_interface.c
 *
 * These routines implement the Java native routines specicfied by
 * running javah on Ndirect.java .  Each routine here then calls
 * the functions provided by Nomadics in their Ndirect.c library.
 *
 * AUTHOR: Tucker Balch, tucker@cc.gatech.edu
 *
 */

#include <jni.h>
#include "EDU_gatech_cc_is_nomad150_Ndirect.h"
#include "Nclient.h"

static	int	let_me_live = 99;
static	jclass	static_placeholder = 0;

/*
 * Class:     edu_gatech_cc_is_nomad150_Ndirect
 * Method:    make_resident
 * Signature: ()I
 */
/*
 * Make the code resident.
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_make_1resident(
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
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    make_free
 * Signature: ()I
 */
/*
 * Release the resources (ie become non-resident).
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_make_1free(
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
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    open_robot
 * Signature: (II)I
 */
/*
 * Open and intialize the robot.
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_open_1robot(
	JNIEnv *env, 
	jobject obj, 
	jint serial_port, 
	jint baud)
	{
	int i;

	/* this sonar ordering will turn them off */
	int     sn_order[16] = {255, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

	/*
	 * Open the serial connection.
	 */
	connect_robot(1);
	if (open_serial((unsigned char)serial_port, (unsigned short)baud)==0)
		return(1);/*error!*/

	/*
	 * Zero out the Smask, so we only get the data when we ask.
	 */
	// actually, now we want the data
	for (i = 0; i<=NUM_MASK; i++) Smask[i] = 0;
	for (i = SMASK_SONAR_1; i<=SMASK_SONAR_16; i++) Smask[i] = 1;
	Smask[SMASK_BUMPER] = 1;
	Smask[SMASK_CONF_X] = 1;
	Smask[SMASK_CONF_Y] = 1;
	Smask[SMASK_CONF_STEER] = 1;
	Smask[SMASK_CONF_TURRET] = 1;
	Smask[SMASK_VEL_TRANS] = 1;
	Smask[SMASK_VEL_STEER] = 1;
	Smask[SMASK_VEL_TURRET] = 1;
	if (ct() == 0)
		return(1);/*error!*/

	/*
	 * Set some default speeds, accelerations 
	 * and timeout values.  The default speeds and accells are maximums.
	 */
        if (ac(300, 500, 500)==0)
                {
                fprintf(stderr,"Ndirect.open_robot: ac failed\n");
                return(1);/*error!*/
                }
        if (sp(200, 450, 450)==0)
                {
                fprintf(stderr,"Ndirect.open_robot: sp failed\n");
                return(1);/*error!*/
                }
        if (conf_sn(0, sn_order)==0) /* turns the sonars off */
                {
                fprintf(stderr,"Ndirect.open_robot: conf_sn failed\n");
                return(1);/*error!*/
                }
        if (conf_tm(1)==0) /* sets robot timeout to 1 seconds */
                {
                fprintf(stderr,"Ndirect.open_robot: conf_tm failed\n");
                return(1);/*error!*/
                }
        if (zr()==0) /* zeroes the robot and homes it */
                {
                fprintf(stderr,"Ndirect.open_robot: zr failed\n");
		return(1);/*error!*/
		}

	return(0);/* OK */
	}


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    da
 * Signature: (II)I
 */

/*
 * Set the robot's angles.
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_da
  (JNIEnv *env, jobject obj, 
	jint th, 
	jint tu)
	{
	if (da(th, tu) == 0)
		return(1);/*error!*/
	else
		return(0);/*OK*/
	} 

/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    dp
 * Signature: (II)I
 */

/*
 * Set the robot's position.
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_dp
  (JNIEnv *env, jobject obj, 
	jint x, 
	jint y)
	{
	if (dp((long)x, (long)y) == 0)
		return(1);/*error!*/
	else
		return(0);/*OK*/
	}
 


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    mv
 * Signature: (IIIIII)I
 */

/*
 * Move the robot
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_mv(
	JNIEnv *env, 
	jobject obj, 
	jint t_mode, 
	jint t_mv, 
	jint s_mode, 
	jint s_mv, 
	jint r_mode, 
	jint r_mv)
	{
	if (mv(t_mode,t_mv,s_mode,s_mv,r_mode,r_mv) == 0)
                {
		fprintf(stderr,"Ndirect.mv: mv failed\n");
		return(1);/*error!*/
		}
	return(0);/*OK*/
	}

/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    st
 * Signature: ()I
 */
/*
 * Stop the robot
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_st(
	JNIEnv *env, 
	jobject obj)
	{
        if (st() == 0)
                {
                fprintf(stderr,"Ndirect.st: st failed\n");
                return(1);/*error!*/
                }
        return(0);/*OK*/
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    sn_on
 * Signature: (I)I
 */
/*
 * Turn on the sonars
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_sn_1on(
	JNIEnv *env, 
	jobject obj,
	jint	delay)
	{
	/*
	 * The default sonar firing order
	 */
	int     order[16] = {0,2,15,1,14,3,13,4,12,5,11,6,10,7,9,8};

	delay = delay/4; /* convert to 4 millisecond increments used by
				nomadics */

	if (delay <= 15) delay = 15;

	if (conf_sn(delay, order) == 0)
		{
		fprintf(stderr,"Ndirect.sn_on: conf_sn failed\n");
		return(1);/*error*/
		}

	return(0);/*OK*/
	}


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    sn_off
 * Signature: ()I
 */
/*
 * Turn off the sonars
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_sn_1off(
	JNIEnv *env, 
	jobject obj)
	{
	int     sn_order[16] = {255, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	if (conf_sn(0, sn_order) == 0)
		{
		fprintf(stderr,"Ndirect.sn_off: conf_sn failed\n");
		return(1);/*error*/
		}

	return(0);/*OK*/
	}


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_bp
 * Signature: ()I
 */
/*
 * Read the bumper switches
 */
JNIEXPORT jlong JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1bp(
	JNIEnv *env, 
	jobject obj)
	{
	/*get_bp();  assume a gs() was issued */
	return(State[STATE_BUMPER]);
	}

/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    gs
 * Signature: ()I
 */
/*
 * Read the sensor data
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_gs
  (JNIEnv *env, jobject obj)
	{
	gs();
	return(1);
	}

/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_rc
 * Signature: ()I
 */
/*
 * Update X,Y,turret and steering info.
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1rc(
	JNIEnv *env, 
	jobject obj)
	{
	/*if (get_rc() == 0)*/
	if (gs() == 0)
		{
		fprintf(stderr,"Ndirect.get_rc: gs failed\n");
		return(1);/*error*/
		}

	return(0);/*OK*/
	}


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_x
 * Signature: ()I
 */
/*
 * Get robot X
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1x(
	JNIEnv *env, 
	jobject obj)
	{
	return(State[STATE_CONF_X]);
	}


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_y
 * Signature: ()I
 */
/*
 * Get robot Y
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1y(
        JNIEnv *env,
        jobject obj)
        {
        return(State[STATE_CONF_Y]);
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_steering
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1steering(
        JNIEnv *env,
        jobject obj)
        {
        return(State[STATE_CONF_STEER]);
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_turret
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1turret(
        JNIEnv *env,
        jobject obj)
        {
        return(State[STATE_CONF_TURRET]);
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_rv
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1rv(
        JNIEnv *env,
        jobject obj)
        {
        /*if (get_rv() == 0)*/
        if (gs() == 0)
                {
                fprintf(stderr,"Ndirect.get_rv: get_rv failed\n");
                return(1);/*error*/
                }

        return(0);/*OK*/
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_vtranslation
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1vtranslation(
        JNIEnv *env,
        jobject obj)
        {
	return(State[STATE_VEL_TRANS]);
	}


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_vturret
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1vturret(
        JNIEnv *env,
        jobject obj)
        {
        return(State[STATE_VEL_TURRET]);
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_vsteering
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1vsteering(
        JNIEnv *env,
        jobject obj)
        {
        return(State[STATE_VEL_STEER]);
        }


/*
 * Class:     EDU_gatech_cc_is_nomad150_Ndirect
 * Method:    get_sn
 * Signature: ([I)I
 */
/*
 * Read the sonars
 */
JNIEXPORT jint JNICALL Java_EDU_gatech_cc_is_nomad150_Ndirect_get_1sn(
	JNIEnv *env, 
	jobject obj, 
	jintArray readings)
	{
	int i;
	jint *readings_body;
	jsize len;

	/*
         * get ready to access the java array "readings"
	 */
	len = (*env)->GetArrayLength(env, readings); /* how long is it?*/
	if (len<16)
		{
		fprintf(stderr,"Ndirect.get_sn: readings array too small (%d"
			" elements), must be at least 16.\n",len);
		return(1);/*error*/
		}
	/* get pointer to the array, and reserve it. must release it later */
	readings_body = (*env)->GetIntArrayElements(env, readings, 0);

	/*
	 * get the sonar readings from the robot
	 */
	// Skip this now.  Assume the data comes from an earlier
	// gs()
	//fprintf(stderr,"Ndirect.get_sn: calling get_sn\n");
	//if (get_sn()==0)
		//{
		//fprintf(stderr,"Ndirect.get_sn: get_sn failed\n");
		//(*env)->ReleaseIntArrayElements(env, readings, readings_body, 0);
		//return(1);/*error*/
		//}
	//fprintf(stderr,"Ndirect.get_sn: after get_sn\n");

	/*
	 * move the readings into the array
	 */
	for (i = 0; i<16; i++)
		readings_body[i] = State[i+STATE_SONAR_0];

	/*
	 * release the array
	 */
	(*env)->ReleaseIntArrayElements(env, readings, readings_body, 0);

	return(0);/*OK*/
	}
