package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.hardware.sensor.BaseSensor;
import lejos.robotics.*;
import java.rmi.*;
import ShefRobot.util.*;

public abstract class Sensor<T> extends PortManager<Pair<T,float[]>> {
    /**
     * These represent the physical ports on the robot which Sensors can be connected to
    **/
    public enum Port {
        S1, S2, S3, S4
    }
    /**
     * Internal representation of Motor types
    **/
    enum Type {
        TOUCH("lejos.hardware.sensor.EV3TouchSensor", new String[]{"Touch"}), 
        ULTRASOUND("lejos.hardware.sensor.EV3UltrasonicSensor", new String[]{"Distance", "Listen"}),
        GYRO("lejos.hardware.sensor.EV3GyroSensor", new String[]{"Angle and Rate", "Angle", "Rate"}), 
        COLOR("lejos.hardware.sensor.EV3ColorSensor", new String[]{"ColorID", "Red", "RGB", "Ambient"});
        //INFRARED("lejos.hardware.sensor.EV3IRSensor", new String[]{"Seek", "Distance"});
        public final String absoluteClass;
        public final String[] modes;
        Type(String absoluteClass, String[] modes)
        {
            this.absoluteClass=absoluteClass;
            this.modes=modes;
        }
    }

    protected SampleProvider provider;
    protected BaseSensor sensor;
    protected Port port;
    private Type type;
    Robot parentRobot;

    /** Create a new Sensor object.

    This creates a Sensor object that is attached to the specified {@link Robot}, of the specified type
    on the specified port.
    
    Using {@link Robot#getSensor} is preferred, since that will keep track of multiple attempts to access
    the same sensor port.

    @param robot The Robot to which the sensor is attached.
    @param port The port to which the sensor is attached.
    @param type The type of sensor.

     */
    protected Sensor(Robot robot, Port port, Type type, BaseSensor sensor) {
        super(Thread.currentThread());
        this.parentRobot = robot;
        this.port = port;
        this.type = type;
        this.sensor = sensor;
        makeSensor();
    }

    private void makeSensor() {
        try {
            //Passing null as mode, returns the BaseSensor object
            //this.sensor = (BaseSensor)this.parentRobot.getEV3().createSampleProvider(this.port.name(), this.type.absoluteClass, null);
            //this.sensor = new EV3IRSensor(p); ;
            this.provider = this.sensor;
        } catch (Exception e) {//lejos.hardware.DeviceException
            System.err.println("Failed to open the sensor port. The most likely reason is that the previous program failed to shut down correctly and free the port. You will have to restart the EV3. Sorry :(");
            throw new RuntimeException("Failed to open Sensor port " + this.port.name());
        }

    }

    /** Closes and cleans up the connection. 
     */
    @Override
    protected void close() {
        this.kill();
        if (this.sensor != null) {
            //try {
                this.sensor.close();
            //} catch (RemoteException e) {
               // throw new RuntimeException(e);
            //}
            this.sensor = null;
        }
        this.parentRobot.closeSensor(this.port);
    }

    /** Get a value from the Sensor.

    @return A floating point value representing the current sensor reading.
    
     */
    public float[] getRawSample() {
        Pair<T,float[]> action = new Pair<T,float[]>(null, null);
        //this.lastsamples = null;
        //this.addAction("GETVAL");
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return action.getValue();
    }

    protected void action(Pair<T,float[]> act) {
        if (this.sensor == null) {
            makeSensor();
        }
        try {
            //We can't extend enums and using ints would be inconsistent
            //Instead we treat a null action as the single common action fetchSample()/GET_VALUE
            //If the action isn't null, we pass it off to the subclass to handle
            if(act.key==null)
            {
                float[] samples = new float[this.sensor.sampleSize()];
                this.provider.fetchSample(samples, 0);
                act.setValue(samples);
            }
            else
            {
                subAction(act);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
    protected abstract void subAction(Pair<T,float[]> act) throws RemoteException;

}