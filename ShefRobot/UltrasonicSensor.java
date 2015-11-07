package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;
import ShefRobot.util.*;
import java.rmi.RemoteException;
import lejos.hardware.sensor.EV3UltrasonicSensor;
/**
 * Represents a touch sensor
 * Each EV3 should contain 1 touch sensor
 * Some {@code LargeMotor} methods are common to {@code Motor}
 * @see Sensor
**/
enum UltrasonicSensorAction{
    GET_VALUE, GET_DISTANCE, GET_LISTEN, ENABLE, DISABLE, GET_STATE;
}
/**
 * This class represents an EV3 UltrasonicSensor which has two modes of operation {@link UltrasonicSensor#Mode.Distance} and {@link UltrasonicSensor#Mode.Listen}
 * By default the sensor starts in {@link UltrasonicSensor#Mode.Distance} mode.
**/
public class UltrasonicSensor extends Sensor<UltrasonicSensorAction>
{
    /**
     * Modes that this sensor can be used in
    **/
    public enum Mode {
        /**
         * Distance mode causes the sensor to continously send out pings which allow it to calculate distances
        **/
        DISTANCE("Distance", 0),
        /**
         * Listen mode causes the sensor to only listen for pings
        **/
        LISTEN("Listen", 1);
        protected final String internalString;
        protected final int internalId;
        Mode(String internalString, int internalId)
        {
            this.internalString=internalString;
            this.internalId=internalId;
        }
    }
    protected UltrasonicSensor(Robot robot, Port port)
    {
        super(robot, port, Sensor.Type.TOUCH, new EV3UltrasonicSensor(robot.getEV3().getPort(port.name())));
        setMode(Mode.DISTANCE);
    }
    /**
     * Switches the sensor between modes
     * This is automatically done when {@link UltrasonicSensor#getDistance()} and {@link UltrasonicSensor#listen()} are called
    **/
    private void setMode(Mode newMode)
    {//Switching to listen mode doesn't seem to put it in the same mode as used by listen()
        this.sensor.setCurrentMode(newMode.internalId);
        try
        {
            Thread.sleep(300);
        }
        catch(Exception e){}
    }
    /**
     * Returns the distance detected by the sensor in metres
     * When in distance mode, the Ultrasonic sensors light will be solid and it will continuously emit pings
     * Values outside of the approximate range ~0.03-1.7m are returned as {@code Float.POSITIVE_INFINITY}
     * @return The distance detected by the ultrasonic sensor
    **/
    public float getDistance()
    {
        Pair<UltrasonicSensorAction,float[]> action = new Pair<UltrasonicSensorAction,float[]>(UltrasonicSensorAction.GET_DISTANCE, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue()[0];
    }
    /**
     * Enables the UltrasonicSensors light
    **/
    private void enable()
    {//Why bother, calling listen() or getDistance() both override this
        this.addAction(new Pair<UltrasonicSensorAction,float[]>(UltrasonicSensorAction.ENABLE, null));
    }
    /**
     * Disables the UltrasonicSensors light
    **/
    private void disable()     
    {//Why bother, calling listen() or getDistance() both override this
        this.addAction(new Pair<UltrasonicSensorAction,float[]>(UltrasonicSensorAction.DISABLE, null));
    }
    /**
     * Returns the state of the UltrasonicSensors light
    **/
    private boolean isEnabled()
    {        
        Pair<UltrasonicSensorAction,float[]> action = new Pair<UltrasonicSensorAction,float[]>(UltrasonicSensorAction.GET_LISTEN, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue()[0]!=0.0f;
    }
    /**
     * When in Listen mode the Ultrasonic sensor will not emit ultrasound, it will only listen for other robots ultrasound.
     * When in Listen mode the Ultrasonic sensors light will blink
     * It may take upto 300ms, after the first call to {@code listen()} for the sensor to stop reading it's own pings
     * @return Whether the ultrasonic sensor detects any ultrasonic pings
    **/
    public boolean listen()
    {
        Pair<UltrasonicSensorAction,float[]> action = new Pair<UltrasonicSensorAction,float[]>(UltrasonicSensorAction.GET_LISTEN, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue()[0]!=0.0f;
    }
    /**
     * Called by the superclass to forward subclass specific actions
    **/
    protected void subAction(Pair<UltrasonicSensorAction,float[]> act) throws RemoteException
    {
        float[] samples;
        switch (act.key) {
            case GET_VALUE:
                samples = new float[this.sensor.sampleSize()];
                this.sensor.fetchSample(samples, 0);
                act.setValue(samples);
                break;
            case GET_DISTANCE:
                samples = new float[((EV3UltrasonicSensor)this.sensor).sampleSize()];
                ((EV3UltrasonicSensor)this.sensor).getDistanceMode().fetchSample(samples, 0);
                act.setValue(samples);
                break;
            case GET_LISTEN:
                samples = new float[((EV3UltrasonicSensor)this.sensor).sampleSize()];
                ((EV3UltrasonicSensor)this.sensor).getListenMode().fetchSample(samples, 0);
                act.setValue(samples);
                break;
            case ENABLE:
                ((EV3UltrasonicSensor)this.sensor).enable();
                break;
            case DISABLE:
                ((EV3UltrasonicSensor)this.sensor).disable();
                break;
            case GET_STATE:
                samples = new float[1];
                samples[0] =((EV3UltrasonicSensor)this.sensor).isEnabled()?1.0f:0.0f;
                act.setValue(samples);
                break;
            default: 
                System.err.println("[" + this.port.name() + "] Asked for Action: " + act.key + " on a UltrasonicSensor...");  
        }
    }
}