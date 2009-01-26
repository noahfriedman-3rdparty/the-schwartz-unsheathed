/**
 * 
 */
package com.android.app.schwarz;

/**
 * @author lithium
 *
 */
public class PhoneOrientation {
	private static final String TAG = "PhoneOrienation";
	// FACE_UP and FACE_DOWN z-axis perpendicular to gravity
	public static final int	ORIENTATION_FACE_UP = 0;
	public static final int ORIENTATION_FACE_DOWN = 1;
	// FACE_LEFT and FACE_RIGHT x-axis perpendicular to gravity
	public static final int ORIENTATION_FACE_LEFT = 2;
	public static final int ORIENTATION_FACE_RIGHT = 3;
	// FACE_FORWARD and FACE_BACKWARD y-axis perpendicular to gravity
	public static final int ORIENTATION_FACE_FORWARD = 4;
	public static final int ORIENTATION_FACE_BACKWARD = 5;
	// anything else will result in an invalid orientation
	public static final int ORIENTATION_INVALID = 6;
	private float	mTolerance = 0.0f;
	
	public int getOrientation(float roll, float pitch) {
		int orientation = ORIENTATION_INVALID;
		
		if(roll >= -90 && roll <= -(90-mTolerance))
			orientation = ORIENTATION_FACE_LEFT;
		else if(roll <= 90 && roll >= (90-mTolerance))
			orientation = ORIENTATION_FACE_RIGHT;
		
		if(pitch >= (90-mTolerance) && pitch <= (90+mTolerance)) {
			if(orientation != ORIENTATION_INVALID)
				orientation = ORIENTATION_INVALID;
			else
				orientation = ORIENTATION_FACE_FORWARD;
		} else if(pitch <= -(90-mTolerance) && pitch >= -(90+mTolerance)) {
			if(orientation != ORIENTATION_INVALID)
				orientation = ORIENTATION_INVALID;
			else
				orientation = ORIENTATION_FACE_BACKWARD;
		}
		
		if((roll >= -mTolerance && roll <= mTolerance)) {
			if((pitch >= -mTolerance && pitch <= mTolerance)) {
				if(orientation != ORIENTATION_INVALID)
					orientation = ORIENTATION_INVALID;
				else
					orientation = ORIENTATION_FACE_UP;
			} else if((pitch <= -(180-mTolerance) && pitch >= -180) ||
					(pitch >= (180-mTolerance) && pitch <= 180)) {
				if(orientation != ORIENTATION_INVALID)
					orientation = ORIENTATION_INVALID;
				else
					orientation = ORIENTATION_FACE_DOWN;
			}
		}
		
		return orientation;
	}
	
	public float getTolerance() {
		return mTolerance;
	}
	
	public void setTolerance(float tolerance) {
		mTolerance = tolerance;
	}
}
