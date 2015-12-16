package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;
import ShefRobot.util.*;
import java.rmi.RemoteException;
import lejos.hardware.sensor.EV3GyroSensor;
enum GyroSensorAction{
    GET_VALUE, RESET, GET_RATE, GET_ANGLE, GET_RATE_AND_ANGLE;
}
/**
 * This class represents an EV3 GyroSensor which can detect its angle and the rate of change
 * Each EV3 should contain 1 gyro sensor
 * If the sensor seems insensitive, please check that the EV3s battery is charged.
 * ~When tested, these sensors only ever returned 0.~
 * @see Sensor
**/
public class GyroSensor extends Sensor<GyroSensorAction>
{

    protected GyroSensor(Robot robot, Port port)
    {
        super(robot, port, Sensor.Type.GYRO);
    }


    /**
     * Resets the Gyro sensor
     * The sensor must be still when this method is called, else it may become mis-calibrated
    **/
    public void reset()
    {
        sendAction(GyroSensorAction.RESET);
    }
    /**
     * Measures the orientation of the sensor in respect to its start orientation.
     * @return Returns a value
    **/
    public float getAngle()
    {
        float[] result = sendAction(GyroSensorAction.GET_ANGLE);
        return result[0];
    }
    /**
     * Measures angular velocity of the sensor.
     * @return Returns a value
    **/
    public float getRate()
    {
        float[] result = sendAction(GyroSensorAction.GET_RATE);
        return result[0];
    }
    /**
     * Measures orientation and angular velocity of the sensor.
     * @return Returns a value
    **/
    public float[] getRateAndAngle()
    {
        float[] result = sendAction(GyroSensorAction.GET_RATE_AND_ANGLE);
        return result;
    }
    /**
     * Convenience method  for sending an action and waiting for a response
     * @param act The action to be sent
     * @return The response received from the sensor
    **/ 
    private float[] sendAction(GyroSensorAction act)
    {
        Pair<GyroSensorAction,float[]> action = new Pair<GyroSensorAction,float[]>(act, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue();
    }
    /**
     * Called by the superclass to forward subclass specific actions
    **/
    protected void subAction(Pair<GyroSensorAction,float[]> act) throws RemoteException
    {
        float[] samples;
        switch (act.key) {
            case GET_VALUE:
                samples = new float[this.sensor.sampleSize()];
                this.sensor.fetchSample(samples, 0);
                act.setValue(samples);
                break;
            case RESET:
                ((EV3GyroSensor)this.sensor).reset();
                act.setValue(new float[1]);//None null value, so thread continues
                break;
            case GET_RATE:
                samples = new float[((EV3GyroSensor)this.sensor).getRateMode().sampleSize()];
                ((EV3GyroSensor)this.sensor).getRateMode().fetchSample(samples, 0);
                act.setValue(samples);
            case GET_ANGLE:
                samples = new float[((EV3GyroSensor)this.sensor).getAngleMode().sampleSize()];
                ((EV3GyroSensor)this.sensor).getAngleMode().fetchSample(samples, 0);
                act.setValue(samples);
                break;
            case GET_RATE_AND_ANGLE:
                samples = new float[((EV3GyroSensor)this.sensor).getAngleAndRateMode().sampleSize()];
                ((EV3GyroSensor)this.sensor).getAngleAndRateMode().fetchSample(samples, 0);
                act.setValue(samples);
                break;
            default: 
                System.err.println("[" + this.port.name() + "] Asked for Action: " + act.key + " on a GyroSensor...");  
        }
    }
}