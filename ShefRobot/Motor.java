package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;
import ShefRobot.util.*;
import ShefRobot.*;

enum MotorAction {
    FORWARD, BACKWARD, STOP, SET_SPEED, ROTATE, ROTATE_ASYNC, ROTATE_TO, ROTATE_TO_ASYNC, RESET_TACHO, GET_SPEED, GET_MAX_SPEED, GET_TACHO_COUNT, GET_IS_STALLED, GET_IS_MOVING;
}
/**
 * This class represents a generic motor, 
 * it is must be instantiated as a subclass of the correct motor model (Large or Medium).
 * This allows the motor objects to be calibrated correctly.
 * 
 * Any methods called on the same Motor will occur in order unless a method is otherwise stated as being asynchronous
 * @see Robot#getLargeMotor()
 * @see Robot#getMediumMotor()
**/
public abstract class Motor extends PortManager<Pair<MotorAction, Integer>>
{
    /**
     * These represent the physical ports on the robot which Motors can be connected to
    **/
    public enum Port {
        A, B, C, D
    }
    /**
     * Internal representation of Motor types
    **/
    enum Type {
        M, L
    }
    
    private RMIRegulatedMotor motor;
    private Port port;
    private Type type;
    Robot parentRobot;

    /** 
     * This creates a Motor object that is attached to the specified {@link Robot}, of the specified type on the specified port.
     *
     * {@link Robot#getLargeMotor} or {@link Robot#getMediumMotor} should be used rather than creating these objects directly.
     * This allows multiple motors to be tracked, since that will keep track of multiple attempts to accessthe same motor port.
     * 
     * @param robot The Robot to which the motor is attached.
     * @param port The port to which the motor is attached.
     * @param type The type of motor.
     */
    protected Motor(Robot robot, Port port, Type type) {
        super(Thread.currentThread());
        this.parentRobot = robot;
        this.port = port;
        this.type = type;
        makeMotor();
    } 
    /** Closes and cleans up the connection if persistent. 
     * <span style="font-weight: bold;">If you created this motor with {@link Robot#getMotor}, 
     * then you should use the {@link Robot#closeMotor} method instead, 
     * otherwise the {@link Robot} object could become inconsistent. </span>
     */
    @Override
    protected void close() {
        this.kill();
        if (this.motor != null) {
            try {
                //Wait for motor to be finish it's current action
                //This prevents motor.rotate() from causing motors to fail to DC
                this.motor.stop(true);
                while(this.motor.isMoving())
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
                this.motor.close();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            this.motor = null;
        }
        // Clean up the Robot object's list, but this *shouldn't* cause an endless loop
        // since the parent will only call this function if this isn't in the list...
        // (it will go round twice, but that's the best way to be sure both objects agree!)
        this.parentRobot.closeMotor(this.port);
    }
    /**
     * Tells the motor to rotate forwards
     * The motor will continue rotating until {@link Motor#stop()} is called.
    **/
    public void forward() {
        this.addAction(new Pair<MotorAction, Integer>(MotorAction.FORWARD, 0));
    }
    /**
     * Tells the motor to rotate backwards
     * The motor will continue rotating until {@link Motor#stop()} is called.
    **/
    public void backward() {
        this.addAction(new Pair<MotorAction, Integer>(MotorAction.BACKWARD, 0));
    }
    /**
     * Tells the motor to stop moving
    **/
    public void stop() {
        this.addAction(new Pair<MotorAction, Integer>(MotorAction.STOP, 0));
    }
    /**
     * Resets the value returned by {@link Motor#getTachoCount()} to 0
    **/
    public void resetTachoCount()
    {
        this.addAction(new Pair<MotorAction, Integer>(MotorAction.RESET_TACHO, 0));
    }
    /**
     * Returns the cumulative number of degrees the motor has turned since {@link Motor#resetTachoCount()} was last called.
     * @return The number of degrees the motor has turned
    **/
    public int getTachoCount()
    {
        Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.GET_TACHO_COUNT, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue();
    }
    /**
     * Sets the speed of the Motor
     * The lowest accepted value is 0
     * The motors maximum speed can be found using {@link Robot#getMaxSpeed()}
     * @param newSpeed The speed of the motor, this value must not be negative
     * @throwsd IllegalArgumentException When newSpeed is negative
     **/
    public void setSpeed(final int newSpeed)
    {
        //Catch this because negative speeds are Math.abs by the internal robot
        if(newSpeed<0)
            throw new IllegalArgumentException("Invalid speed argument: "+newSpeed+"\n Speeds should not be negative (try using the backward() method).");
        this.addAction(new Pair<MotorAction, Integer>(MotorAction.SET_SPEED, newSpeed));
    }
    /**
     * Returns the speed of the Motor
     * @param newSpeed The speed of the motor
    **/
    public int getSpeed()
    {
        Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.GET_SPEED, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue();
    }
    /**
     * Returns the maximum speed, this value is based off the Robots battery voltage (it decreases as the Robots battery drains).
     * 
     * A charged robot should be able to reach a speed of atleast 700
     * @return The current maximum speed of the robot
    **/
    public int getMaxSpeed()
    {
        Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.GET_MAX_SPEED, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue();
    }
    /**
     * Returns whether the motor is currently moving.
     * @return True if the motor is moving, else false
    **/
    public boolean isMoving()
    {
        Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.GET_IS_MOVING, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue()!=0;
    }
    /**
     * Returns whether the motor has stalled.
     * @return True if the motor has stalled, else false
    **/
    public boolean isStalled()
    {
        Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.GET_IS_STALLED, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue()!=0;
    }
    /**
     * Rotates the motor the specified number of degrees.
     *
     * Negative values will cause the motor to rotate backwards.
     * @see Motor#rotate(int, boolean)
     * @param degrees The number of degrees that the motor should rotate
    **/
    public void rotate(int degrees)
    {
        rotate(degrees, false);
    }
    /**
     * Rotates the motor the specified number of degrees.
     *
     * Negative values will cause the motor to rotate backwards.
     *
     * When {@code async} is set to true, this method will return instantly,
     * this can be used if you need to call rotate multiple motors simultaneously.
     * @param degrees The number of degrees that the motor should rotate
     * @param async When true this method will not wait for the action to complete before returning control
    **/
    public void rotate(int degrees, boolean async)
    {  
        if(async) {
            this.addAction(new Pair<MotorAction, Integer>(MotorAction.ROTATE_TO_ASYNC, degrees));
        }
        else {
            Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.ROTATE_TO, degrees);
            this.addAction(action);
            while (action.getValue() != null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        }
    }
    /**
     * Rotates the motor to the specified tacho count.
     *
     * After this action has completed, {@link Motor#getTachoCount()} should return the specified value within 2 degrees.
     *
     * @see Motor#rotateTo(int, boolean)
     * @param degrees The tacho count to rotate the motor until
    **/
    public void rotateTo(int degrees)
    {
        rotateTo(degrees, false);
    }
    /**
     * Rotates the motor to the specified tacho count.
     *
     * After this action has completed, {@link Motor#getTachoCount()} should return the specified value within 2 degrees.
     *
     * When {@code async} is set to true, this method will return instantly,
     * this can be used if you need to call rotate multiple motors simultaneously.
     * @param degrees The tacho count to rotate the motor until
     * @param async When true this method will not wait for the action to complete before returning control
    **/
    public void rotateTo(int degrees, boolean async)
    {
        if(async) {
            this.addAction(new Pair<MotorAction, Integer>(MotorAction.ROTATE_TO_ASYNC, degrees));
        }
        else {
            Pair<MotorAction, Integer> action = new Pair<MotorAction, Integer>(MotorAction.ROTATE_TO, degrees);
            this.addAction(action);
            while (action.getValue() != null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        }
    }
    /**
     * Internal method used to initialise the motor
    **/
    private void makeMotor() {
        try {
            this.motor = this.parentRobot.getEV3().createRegulatedMotor(this.port.name(), this.type.name().charAt(0));
        } catch (lejos.hardware.DeviceException e) {
            System.err.println("Failed to open the motor port. The most likely reason is that the previous program failed to shut down correctly and free the port. You will have to restart the EV3. Sorry :(");
            throw new RuntimeException("Failed to open Motor port " + this.port.name());
        }
    }
    /**
     * Carrys out the parameterised action
     * This method is called by the PortManager superclass
     * @param act {@code act.key} is read-only and specifies the {@code Action} to be carried out
     * @param act {@code act.value} can specify an argument for the {@code Action} or be used for returning values.
    **/
    protected void action(Pair<MotorAction, Integer> act) {
        if (this.motor == null) {
            makeMotor();
        }
        try {
            switch (act.key) {
                case FORWARD:
                    this.motor.forward();
                    break;
                case BACKWARD:
                    this.motor.backward();
                    break;
                case STOP:
                    this.motor.stop(true);
                    break;
                case SET_SPEED:
                    this.motor.setSpeed(act.getValue());
                    break;
                case ROTATE:
                    this.motor.rotate(act.getValue(), false);
                    act.setValue(null);
                    break;
                case ROTATE_ASYNC:
                    this.motor.rotateTo(act.getValue(), true);
                    break;
                case ROTATE_TO:
                    this.motor.rotateTo(act.getValue(), false);
                    act.setValue(null);
                    break;
                case ROTATE_TO_ASYNC:
                    this.motor.rotateTo(act.getValue(), true);
                    break;
                case RESET_TACHO:
                    this.motor.resetTachoCount();
                    break;
                case GET_SPEED:
                    act.setValue(this.motor.getSpeed());
                    break;
                case GET_MAX_SPEED:
                    act.setValue((int)this.motor.getMaxSpeed());
                    break;
                case GET_TACHO_COUNT:
                    act.setValue(this.motor.getTachoCount());
                    break;
                case GET_IS_MOVING:
                    act.setValue(this.motor.isMoving()?1:0);
                    break;
                case GET_IS_STALLED:
                    act.setValue(this.motor.isStalled()?1:0);
                    break;
                default:
                    System.err.println("[" + this.port.name() + "] Asked for Action: " + act + " on a Motor...");
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}