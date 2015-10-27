package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;
import java.util.*;

/** Objects of this class represent particular EV3 based Lego robots. 


*/
public class Robot {

    private RemoteEV3 ev3;

    private HashMap <Motor.Port, Motor> motors;
    private HashMap <Sensor.Port, Sensor> sensors;

    /** Create a new Robot object.

    This will find the first available EV3 on the local network or Bluetooth. 
     */
    public Robot() {
        try {
            BrickInfo[] bricks = BrickFinder.discover();
            if (bricks.length > 0) {
                for (BrickInfo info: bricks) {
                    System.out.println("EV3 found on ip: " + info.getIPAddress());
                }
                String ip = bricks[0].getIPAddress();
                setup(ip);
            } else {
                System.out.println("No EV3s found - check your network/bluetooth connections!");
                throw new RuntimeException("No EV3 found.");
            }
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Failed to find a, EV3 - have you checked your network/bluetooth connection??");
            throw new RuntimeException("Failed to find a Robot");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Create a new Robot object with a specific IP address.

    @param ip This defines the IP address of the Robot.
     */
    public Robot(String ip) {
        setup(ip);
    }

    /** Get a Motor object attached to the specified port.

    If a Motor is already attached to this port then this function will retern a reference
    to that Motor object, otherwise a new object is created.

    @param port The port to which the motor is connected. Must be from {@link Motor.Port}.
    @param type The type of motor. Must be from {@link Motor.Type}.
    @return The Motor object. 
     */
    public Motor getMotor(Motor.Port port, Motor.Type type) {
        Motor m;
        m = this.motors.get(port);
        if (m == null) {
            m = new Motor(this, port, type);
            this.motors.put(port, m);
        }
        return m;
    }

    /** Get a Sensor object attached to the specified port.

     If a Sensor is already attached to this port then this function will retern a reference
     to that Sensor object, otherwise a new object is created.

    @param port The port to which the motor is connected. Must be from {@link Sensor.Port}.
    @param type The type of sensor. Must be from {@link Sensor.Type}.
    @return The Sensor object.
     */

    public Sensor getSensor(Sensor.Port port, Sensor.Type type) {
        Sensor s;
        s = this.sensors.get(port);
        if (s == null) {
            s = new Sensor(this, port, type);
            this.sensors.put(port, s);
        }
        return s;
    }

    /** Close a Robot's connections.    
     */
    public void close() {
        // Clone the maps to avoid concurrent modification problems.
        HashMap < Motor.Port, Motor > ms = new HashMap < Motor.Port, Motor > (this.motors);
        for (Motor.Port p: ms.keySet()) {
            this.closeMotor(p);
            Motor m = ms.get(p);
            m.close();
            try {
                m.getThread().join();
            } catch (InterruptedException e) {}
        }
        HashMap < Sensor.Port, Sensor > ss = new HashMap < Sensor.Port, Sensor > (this.sensors);
        for (Sensor.Port p: ss.keySet()) {
            Sensor s = this.sensors.get(p);
            s.close();
            try {
                s.getThread().join();
            } catch (InterruptedException e) {}
        }
        this.sensors = new HashMap < Sensor.Port, Sensor > ();
    }

    /** This method allows you to close one Motor connection. 

    @param port The port to which the motor is connected.
    */
    public void closeMotor(Motor.Port port) {
        Motor m = this.motors.get(port);
        if (m != null) {
            this.motors.remove(port);
            m.close();
        }
    }

    /** This method allows you to close one Sensor connection. 

    @param port The port to which the sensor is connected.
     */
    public void closeSensor(Sensor.Port port) {
        Sensor s = this.sensors.get(port);
        if (s != null) {
            this.sensors.remove(port);
            s.close();
        }
    }

    /** This is a utility method that is used by the {@link Motor Motors} and {@link Sensor Sensors} to 
     access the EV3 and make connections.

     @return The RemoteEV3 object from the LeJOS package.
    */
    protected RemoteEV3 getEV3() {
        return this.ev3;
    }

    // Centralised setup method that is called by the constructors after finding IPs etc.
    private void setup(String ip) {
        this.motors = new HashMap <Motor.Port, Motor> ();
        this.sensors = new HashMap <Sensor.Port, Sensor> ();
        try {
            this.ev3 = new RemoteEV3(ip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}