package Helicopter;

import rlglue.types.Action;
import rlglue.types.Observation;

public class HelicopterState {

	/* some finalants indexing into the helicopter's state */

	final  int ndot_idx = 0; // north velocity
	final  int edot_idx = 1; // east velocity
	final  int ddot_idx = 2; // down velocity
	final  int n_idx = 3; // north
	final  int e_idx = 4; // east
	final  int d_idx = 5; // down
	final  int p_idx = 6; // angular rate around forward axis
	final  int q_idx = 7; // angular rate around sideways (to the right) axis
	final  int r_idx = 8; // angular rate around vertical (downward) axis
	final  int qx_idx = 9; // quaternion entries, x,y,z,w   q = [ sin(theta/2) * axis; cos(theta/2)]
	final  int qy_idx = 10; // where axis = axis of rotation; theta is amount of rotation around that axis
	final  int qz_idx = 11;  // [recall: any rotation can be represented by a single rotation around some axis]
	final static  int qw_idx = 12;
	final  int state_size = 13;

	// note: observation returned is not the state itself, but the "error state" expressed in the helicopter's frame (which allows for a simpler mapping from observation to inputs)
	// observation consists of:
	// u, v, w  : velocities in helicopter frame
	// xerr, yerr, zerr: position error expressed in frame attached to helicopter [xyz correspond to ned when helicopter is in "neutral" orientation, i.e., level and facing north]
	// p, q, r
	// qx, qy, qz

	double state[]=new double[state_size];
	boolean env_terminal=false;
	int num_sim_steps;
	


	// very crude helicopter model, okay around hover:
	final double heli_model_u_drag = 0.00018;
	final double heli_model_v_drag = 0.00043;
	final double heli_model_w_drag = 0.49;
	final double heli_model_p_drag = 12.78;
	final double heli_model_q_drag = 10.12;
	final double heli_model_r_drag = 8.16;
	final double heli_model_u0_p = 33.04;
	final double heli_model_u1_q = -33.32;
	final double heli_model_u2_r =  70.54;
	final double heli_model_u3_w = -42.15;
	final double heli_model_tail_rotor_side_thrust = -0.54;

	final double DT = .1; // simulation time scale  [time scale for control --- internally we integrate at 100Hz for simulating the dynamics]

	final int NUM_SIM_STEPS_PER_EPISODE = 6000; // after 6000 steps we automatically enter the terminal state


	public void reset(){
		for(int i=0;i<state.length;i++)
			state[i]=0.0d;
	}

	double[] smallArrayCopy(double bigArray[], int startIndex, int size){
		double returnArray[]=new double[size];
		for(int i=startIndex;i<startIndex+size;i++){
			returnArray[i-startIndex]=bigArray[i];
		}
		return returnArray;
	}
	public Observation makeObservation(){
		Observation o= new Observation(0,state_size);
		//observation is the error state in the helicopter's coordinate system (that way errors/observations can be mapped more directly to actions)
		double ned_error_in_heli_frame[]=new double[3];


		double n_idexArray[] = smallArrayCopy(state,n_idx,3);
		double qx_idxArray[] = smallArrayCopy(state,qx_idx,4);

		express_vector_in_quat_frame(n_idexArray,qx_idxArray, ned_error_in_heli_frame);


		double uvw[] = new double[3];

		double ndot_idxArray[] = smallArrayCopy(state,ndot_idx,3);
		qx_idxArray = smallArrayCopy(state,qx_idx,4);



		express_vector_in_quat_frame(ndot_idxArray,qx_idxArray, uvw);

		o.doubleArray[0] = uvw[0];
		o.doubleArray[1] = uvw[1];
		o.doubleArray[2] = uvw[2];

		o.doubleArray[n_idx] = ned_error_in_heli_frame[0];
		o.doubleArray[e_idx] = ned_error_in_heli_frame[1];
		o.doubleArray[d_idx] = ned_error_in_heli_frame[2];
		o.doubleArray[p_idx] = state[p_idx];
		o.doubleArray[q_idx] = state[q_idx];
		o.doubleArray[r_idx] = state[r_idx];

		// the error quaternion gets negated, b/c we consider the rotation required to bring the helicopter back to target in the helicopter's frame
		o.doubleArray[qx_idx] = state[qx_idx];
		o.doubleArray[qy_idx] = state[qy_idx];
		o.doubleArray[qz_idx] = state[qz_idx];


		return o;
	}


///////////////////////////
//	quaternion functions ///
///////////////////////////

	void quaternion_from_axis_rotation(final double axis_rotation[], double quat[])
	{
		assert(axis_rotation.length==3);
		assert(quat.length==4);

		double rotation_angle = Math.sqrt( axis_rotation[0]*axis_rotation[0]
		                                                                  + axis_rotation[1]*axis_rotation[1]
		                                                                                                   + axis_rotation[2]*axis_rotation[2] );

		if(rotation_angle < 1e-4){  // avoid division by zero -- also: can use simpler computation in this case, since for small angles sin(x) = x is a good approximation
			quat[0] = axis_rotation[0]/2;
			quat[1] = axis_rotation[1]/2;
			quat[2] = axis_rotation[2]/2;
			quat[3] = Math.sqrt( 1 - (quat[0]*quat[0]+quat[1]*quat[1]+quat[2]*quat[2]) );
		} else { 
			double normalized_axis[]=new double[3];
			normalized_axis[0] = axis_rotation[0] / rotation_angle;
			normalized_axis[1] = axis_rotation[1] / rotation_angle;
			normalized_axis[2] = axis_rotation[2] / rotation_angle;

			quat[0] = Math.sin(rotation_angle/2) * normalized_axis[0];
			quat[1] = Math.sin(rotation_angle/2) * normalized_axis[1];
			quat[2] = Math.sin(rotation_angle/2) * normalized_axis[2];
			quat[3] = Math.cos(rotation_angle/2);
		}
	}

	void quat_multiply(final double lq[], final double rq[], double ans[])
//	quaternion entries in order: x, y, z, w
	{
		assert(lq.length==4);
		assert(rq.length==4);
		assert(ans.length==4);

		ans[0] = lq[3]*rq[0] + lq[0]*rq[3] + lq[1]*rq[2] - lq[2]*rq[1];
		ans[1] = lq[3]*rq[1] - lq[0]*rq[2] + lq[1]*rq[3] + lq[2]*rq[0];
		ans[2] = lq[3]*rq[2] + lq[0]*rq[1] - lq[1]*rq[0] + lq[2]*rq[3];
		ans[3] = lq[3]*rq[3] - lq[0]*rq[0] - lq[1]*rq[1] - lq[2]*rq[2];

	}

	void rotate_vector(final double vin[], final double q[], double vout[])
	{
		assert(vin.length==3);
		assert(q.length==4);
		assert(vout.length==3);

		double q_conj[]=new double[4]; 

//		return   ( ( q * quaternion(vin) ) * q_conj ) .complex_part
		double q_tmp[]=new double[4]; 
		double q_vin[]=new double[4]; 
		double q_tmp2[]=new double[4]; 

		q_conj[0] = -q[0]; q_conj[1] = -q[1]; q_conj[2] = -q[2]; q_conj[3] = q[3];
		q_vin[0] = vin[0]; q_vin[1] = vin[1]; q_vin[2] = vin[2]; q_vin[3] = 0;
		quat_multiply(q, q_vin, q_tmp);
		quat_multiply(q_tmp, q_conj, q_tmp2);
		vout[0] = q_tmp2[0]; vout[1] = q_tmp2[1]; vout[2] = q_tmp2[2];
	}

	void express_vector_in_quat_frame(double vin[], double q[],double vout[])
	{
		assert(vin.length==3);
		assert(q.length==4);
		assert(vout.length==3);

		double q_conj[]=new double[4];
		q_conj[0] = -q[0]; q_conj[1] = -q[1]; q_conj[2] = -q[2]; q_conj[3] = q[3];
		rotate_vector(vin, q_conj, vout);
	}


	double MyMin(double x, double y){
		return (x < y ? x : y);
	}
	double MyMax(double x, double y){
		return (x > y ? x : y);
	}

	double box_mull() {
		double x1 = Math.random();
		double x2 = Math.random();
		return Math.sqrt(-2*Math.log(x1))*Math.cos(2*Math.PI*x2);
	}

	public void stateUpdate(Action a) {
		// saturate all the actions, b/c the actuators are limited: 
		//[real helicopter's saturation is of course somewhat different, depends on swash plate mixing etc ... ]
		for (int i=0; i<4; ++i)
			a.doubleArray[i] = MyMin(MyMax(a.doubleArray[i], -1.0),+1.0);


		double noise[]=new double[6];
		final double noise_mult = 2.0;
		final double noise_std[]= {  0.1941, 0.2975, 0.6058, 0.1508, 0.2492, 0.0734}; // u, v, w, p, q, r
		//generate Gaussian random numbers

		for (int i=0; i<6; ++i)
			noise[i] = box_mull() * noise_std[i] * noise_mult;

		double dt = .01;  //integrate at 100Hz [control at 10Hz]
		for (int t=0; t<10; ++t){

			// Euler integration:

			// *** position ***
			state[n_idx] += dt * state[ndot_idx];
			state[e_idx] += dt * state[edot_idx];
			state[d_idx] += dt * state[ddot_idx];

			// *** velocity ***
			double uvw[]=new double[3];
			double ndot_idxArray[] = smallArrayCopy(state,ndot_idx,3);
			double qx_idxArray[] = smallArrayCopy(state,qx_idx,4);

			express_vector_in_quat_frame(ndot_idxArray, qx_idxArray, uvw);

			double uvw_force_from_heli_over_m[]=new double[3];
			uvw_force_from_heli_over_m[0] = - heli_model_u_drag * uvw[0]  + noise[0];
			uvw_force_from_heli_over_m[1] = - heli_model_v_drag * uvw[1] + heli_model_tail_rotor_side_thrust + noise[1];
			uvw_force_from_heli_over_m[2] = - heli_model_w_drag * uvw[2] + heli_model_u3_w * a.doubleArray[3] + noise[2];

			double ned_force_from_heli_over_m[]=new double[3];
			qx_idxArray = smallArrayCopy(state,qx_idx,4);
			rotate_vector(uvw_force_from_heli_over_m,qx_idxArray, ned_force_from_heli_over_m);

			state[ndot_idx] += dt * ned_force_from_heli_over_m[0];
			state[edot_idx] += dt * ned_force_from_heli_over_m[1];
			state[ddot_idx] += dt * (ned_force_from_heli_over_m[2] + 9.81);

			// *** orientation ***
			double axis_rotation[]=new double[3]; 
			axis_rotation[0] = state[p_idx]*dt; 
			axis_rotation[1] = state[q_idx]*dt; 
			axis_rotation[2] = state[r_idx]*dt; 
			double rot_quat[]=new double[4];
			quaternion_from_axis_rotation(axis_rotation, rot_quat);

			double tmp_result[]=new double[4];
			qx_idxArray = smallArrayCopy(state,qx_idx,4);
			quat_multiply(qx_idxArray, rot_quat, tmp_result);
			
			state[qx_idx+0]=tmp_result[0];
			state[qx_idx+1]=tmp_result[1];
			state[qx_idx+2]=tmp_result[2];
			
			
			state[qx_idx+3]=tmp_result[3];

			// *** angular rate ***


			double p_dot = -heli_model_p_drag * state[p_idx] + heli_model_u0_p * a.doubleArray[0] + noise[3];
			double q_dot = -heli_model_q_drag * state[q_idx] + heli_model_u1_q * a.doubleArray[1] + noise[4];
			double r_dot = -heli_model_r_drag * state[r_idx] + heli_model_u2_r * a.doubleArray[2] + noise[5];

			state[p_idx] += dt* p_dot;
			state[q_idx] += dt* q_dot;
			state[r_idx] += dt* r_dot;

			env_terminal = env_terminal ||  (  ( state[n_idx]*state[n_idx] + state[e_idx]*state[e_idx] + state[d_idx]*state[d_idx] ) > 100  )
			|| ( ( state[ndot_idx]*state[ndot_idx] + state[edot_idx]*state[edot_idx] + state[ddot_idx]*state[ddot_idx] ) > 100 )
			|| ( ( state[p_idx]*state[p_idx] + state[q_idx]*state[q_idx] + state[r_idx]*state[r_idx] ) > 100 )
			|| ( state[qw_idx]*state[qw_idx] < .5 );
		}

		for (int i=0; i< 13; ++i)
			System.out.printf("%.2f\t",state[i]);
		System.out.println();

	}


}
