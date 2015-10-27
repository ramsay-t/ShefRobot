package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;

public class Motor extends PortManager {

    public enum Port {
	A,B,C,D
    }
    public enum Type {
	M,L
    }

    private RMIRegulatedMotor motor;
    private Port port;
    private Type type;
    Robot parentRobot;
    
    /** This creates a Motor object that is attached to the specified {@link Robot}, of the specified type
	on the specified port.
	
	Using {@link Robot#getMotor} is preferred, since that will keep track of multiple attempts to access
	the same motor port.

	@param robot The Robot to which the motor is attached.
	@param port The port to which the motor is attached.
	@param type The type of motor.
     */
    public Motor(Robot robot,Port port,Type type) {
	super(Thread.currentThread());
	this.parentRobot = robot;
	this.port = port;
	this.type = type;
	makeMotor();
    }

    /** Closes and cleans up the connection if persistent. 

	<span style="font-weight: bold;">If you created this sensor with {@link Robot#getMotor}, 
	then you should use the {@link Robot#closeMotor} method instead, 
	otherwise the {@link Robot} object could become inconsistent. </span>
     */
    public void close() {
	this.kill();
	if(this.motor != null) {
	    try {
		this.motor.close();
	    } catch(RemoteException e) {
		throw new RuntimeException(e);
	    }
	    this.motor = null;
	}
	// Clean up the Robot object's list, but this *shouldn't* cause an endless loop
	// since the parent will only call this function if this isn't in the list...
	// (it will go round twice, but that's the best way to be sure both objects agree!)
	this.parentRobot.closeMotor(this.port);
    }

    /** Starts the motor rotating forwards. */
    public void forward() {
	this.addAction("FORWARD");
    }

    /** Starts the motor rotating backwards. */
    public void backward() {
	this.addAction("BACKWARD");
    }

    /** Stops the motor. */
    public void stop() {
	this.addAction("STOP");
    }

    private void makeMotor() {
	try {
	    this.motor = this.parentRobot.getEV3().createRegulatedMotor(this.port.name(),this.type.name().charAt(0));
	} catch (lejos.hardware.DeviceException e) {
	    System.err.println("Failed to open the motor port. The most likely reason is that the previous program failed to shut down correctly and free the port. You will have to restart the EV3. Sorry :(");
	    throw new RuntimeException("Failed to open Motor port " + this.port.name());
	}
    }

    protected void action(String act) {
	if(this.motor == null) {
	    makeMotor();
	}
	try {
	    switch(act) {
	    case "FORWARD":
		this.motor.forward();
		break;
	    case "BACKWARD":
		this.motor.backward();
		break;
	    case "STOP":
		this.motor.stop(true);
		break;
	    default:
		System.err.println("[" + this.port.name() + "] Asked for Action: " + act + " on a Motor...");
	    }
	} catch(RemoteException e) {
	    throw new RuntimeException(e);
	}
    }

}
