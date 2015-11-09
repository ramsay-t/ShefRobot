package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;
import ShefRobot.util.*;
import java.rmi.RemoteException;
import lejos.hardware.sensor.EV3TouchSensor;
/**
 * Represents a touch sensor
 * Each EV3 should contain 1 touch sensor
 * Some {@code LargeMotor} methods are common to {@code Motor}
 * @see Sensor
**/
enum TouchSensorAction{
    GET_VALUE;
}
public class TouchSensor extends Sensor<TouchSensorAction>
{
    public enum Mode {
        TOUCH("Touch", 0);
        protected final String internalString;
        protected final int internalId;
        Mode(String internalString, int internalId)
        {
            this.internalString=internalString;
            this.internalId=internalId;
        }
    }
    protected TouchSensor(Robot robot, Port port)
    {
        super(robot, port, Sensor.Type.TOUCH);
    }
    public boolean isTouched()
    {
        float[] sample = getRawSample();
        return sample[0]==1;
    }
    protected void subAction(Pair<TouchSensorAction,float[]> act) throws RemoteException
    {
        switch (act.key) {
            case GET_VALUE:
                float[] samples = new float[this.sensor.sampleSize()];
                this.sensor.fetchSample(samples, 0);
                act.setValue(samples);
                break;
            default: 
                System.err.println("[" + this.port.name() + "] Asked for Action: " + act.key + " on a TouchSensor...");  
        }
    }
}