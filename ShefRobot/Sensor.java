package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;

public class Sensor extends PortManager {

    public enum Port {
        S1, S2, S3, S4
    }

    public enum Type {
        ULTRASOUND, TOUCH
    }

    private RMISampleProvider sensor;
    private Port port;
    private Type type;
    private float[] lastsamples;
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
    public Sensor(Robot robot, Port port, Type type) {
        super(Thread.currentThread());
        this.parentRobot = robot;
        this.port = port;
        this.type = type;
        makeSensor();
    }

    private void makeSensor() {
        try {
            switch (this.type) {
                case TOUCH:
                    this.sensor = this.parentRobot.getEV3().createSampleProvider(this.port.name(), "lejos.hardware.sensor.EV3TouchSensor", "Touch");
                    break;
                case ULTRASOUND:
                    this.sensor = this.parentRobot.getEV3().createSampleProvider(this.port.name(), "lejos.hardware.sensor.EV3UltrasonicSensor", "Distance");
                    break;
            }
        } catch (lejos.hardware.DeviceException e) {
            System.err.println("Failed to open the sensor port. The most likely reason is that the previous program failed to shut down correctly and free the port. You will have to restart the EV3. Sorry :(");
            throw new RuntimeException("Failed to open Sensor port " + this.port.name());
        }

    }

    /** Closes and cleans up the connection. 
     */
    public void close() {
        this.kill();
        if (this.sensor != null) {
            try {
                this.sensor.close();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            this.sensor = null;
        }
        this.parentRobot.closeSensor(this.port);
    }

    /** Get a value from the Sensor.

    The value is always a floating point number. For Ultrasound sensors this is a distance in meters; for
    touch sensors this will be 0 if not touched and 1 if touched.

    @return A floating point value representing the current sensor reading.
    
     */
    public float getValue() {
        this.lastsamples = null;
        this.addAction("GETVAL");
        while (this.lastsamples == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return this.lastsamples[0];
    }

    protected void action(String act) {
        if (this.sensor == null) {
            makeSensor();
        }

        try {
            switch (act) {
                case "GETVAL":
                    this.lastsamples = this.sensor.fetchSample();
                    break;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

}