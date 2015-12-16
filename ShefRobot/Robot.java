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

    private HashMap<Motor.Port,Motor> motors;
    private HashMap<Sensor.Port,Sensor> sensors;

    /** Thread is used to gracefully exit when program shuts down
     * Instantiated and linked in setup()
     * Unlinked during close() if unused
    */
    private GracefulExiter shutdownHook;
    
    /*
     * Internal speaker member returned by getSpeaker()
    **/
    private Speaker speaker;
    /*
     * Internal buttons member returned by getButtons()
    **/
    private Buttons buttons;
    /** Create a new Robot object.

    This will find the first available EV3 on the local network or Bluetooth. 
     */
    public Robot() {
    int errors = 0;
        do
        {
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
                //Reached the end of setup, exit the loop
                break;
            } catch (java.net.SocketTimeoutException e) {
                System.err.println("Failed to find a, EV3 - have you checked your network/bluetooth connection??");
                throw new RuntimeException("Failed to find a Robot");
            } catch (Exception e) {
                errors++;
                if(errors<5)
                {
                    System.err.println("Failed to connect to EV3: "+errors+"/5 attempts.");
                    System.err.println("Retrying in 5 seconds.");
                    try
                    {
                        Thread.sleep(5000);
                    }catch(Exception ioe){}
                }
                else
                {
                    System.err.println("Failed to connect to EV3 after 5 attempts.");
                    System.err.println("Please check the machines bluetooth is enabled and connected to the EV3.");
                    throw new RuntimeException(e);
                }
            }
        }while(true);//We exit the loop via a break or a thrown exception
        speaker = new Speaker(ev3);
        buttons = new Buttons(ev3);
    }

    /** Create a new Robot object with a specific IP address.

    @param ip This defines the IP address of the Robot.
     */
    public Robot(String ip) {
        try
        {
            setup(ip);
            speaker = new Speaker(ev3);
            buttons = new Buttons(ev3);
        } catch (Exception e) {
            System.err.println("Failed to find a, EV3 - have you checked your network/bluetooth connection??");
            throw new RuntimeException("Failed to find a Robot");
        }
    }

    /** Get a Motor object attached to the specified port.

    If a LargeMotor is already attached to this port, then this function will retern a reference
    to that Motor object, otherwise a new object is (re)created.

    @param port The port to which the motor is connected. Must be from {@link Motor.Port}.
    @return The LargeMotor object.
    @see Motor
     */
    public LargeMotor getLargeMotor(Motor.Port port) {
        Motor m;
        m = this.motors.get(port);
        if (m == null) {
            m = new LargeMotor(this, port);
            this.motors.put(port, m);
        }
        if(m instanceof LargeMotor)
        {
            return (LargeMotor)m;
        }
        else
        {//Requesting motor of different type on same port, so recreate
            closeMotor(port);
            return getLargeMotor(port);
        }
    }
    /** Get a Motor object attached to the specified port.

    If a MediumMotor is already attached to this port, then this function will retern a reference
    to that Motor object, otherwise a new object is (re)created.

    @param port The port to which the motor is connected. Must be from {@link Motor.Port}.
    @return The MediumMotor object.
    @see Motor 
     */
    public MediumMotor getMediumMotor(Motor.Port port) {
        Motor m;
        m = this.motors.get(port);
        if (m == null) {
            m = new MediumMotor(this, port);
            this.motors.put(port, m);
        }
        if(m instanceof MediumMotor)
        {
            return (MediumMotor)m;
        }
        else
        {//Requesting motor of different type on same port, so recreate
            closeMotor(port);
            return getMediumMotor(port);
        }
    }
    /** 
     * Get a Speaker object
     * You can use a Speaker for playing tones through the Robot
     * @return The Speaker object. 
     * @see Speaker
    **/
    public Speaker getSpeaker() {
      return speaker;
    }
    /** 
     * Get a Buttons object
     * You can use Buttons for creating ButtonListeners and waiting for ButtonPresses
     * @return The Buttons object. 
     * @see Buttons
    **/
    public Buttons getButtons() {
      return buttons;
    }
    /** Get an UltrasonicSensor object attached to the specified port.

     If an UltrasonicSensor is already attached to this port, then this function will retern a reference
     to that Sensor object, otherwise a new object is created.

    @param port The port to which the sensor is connected. Must be from {@link Sensor.Port}.
    @return The UltrasonicSensor object.
     */
    public UltrasonicSensor getUltrasonicSensor(Sensor.Port port) {     
        Sensor s;
         s = this.sensors.get(port);
        if (s == null) {
            s = new UltrasonicSensor(this, port);
            this.sensors.put(port, s);
        }
        if(s instanceof UltrasonicSensor)
        {
            return (UltrasonicSensor)s;
        }
        else
        {//Requesting sensor of different type on same port, so recreate
            closeSensor(port);
            return getUltrasonicSensor(port);
        }
    }
    /** Get an TouchSensor object attached to the specified port.

     If an TouchSensor is already attached to this port, then this function will retern a reference
     to that Sensor object, otherwise a new object is created.

    @param port The port to which the sensor is connected. Must be from {@link Sensor.Port}.
    @return The TouchSensor object.
     */
    public TouchSensor getTouchSensor(Sensor.Port port) {     
        Sensor s;
         s = this.sensors.get(port);
        if (s == null) {
            s = new TouchSensor(this, port);
            this.sensors.put(port, s);
        }
        if(s instanceof TouchSensor)
        {
            return (TouchSensor)s;
        }
        else
        {//Requesting sensor of different type on same port, so recreate
            closeSensor(port);
            return getTouchSensor(port);
        }
    }
    /** Get a ColorSensor object attached to the specified port.

     If a ColorSensor is already attached to this port, then this function will retern a reference
     to that Sensor object, otherwise a new object is created.

    @param port The port to which the sensor is connected. Must be from {@link Sensor.Port}.
    @return The ColorSensor object.
     */
    public ColorSensor getColorSensor(Sensor.Port port) {     
        Sensor s;
         s = this.sensors.get(port);
        if (s == null) {
            s = new ColorSensor(this, port);
            this.sensors.put(port, s);
        }
        if(s instanceof ColorSensor)
        {
            return (ColorSensor)s;
        }
        else
        {//Requesting sensor of different type on same port, so recreate
            closeSensor(port);
            return getColorSensor(port);
        }
    }
    /** Get a GyroSensor object attached to the specified port.

     If a GyroSensor is already attached to this port, then this function will retern a reference
     to that Sensor object, otherwise a new object is created.

    @param port The port to which the sensor is connected. Must be from {@link Sensor.Port}.
    @return The GyroSensor object.
     */
    public GyroSensor getGyroSensor(Sensor.Port port) {     
        Sensor s;
         s = this.sensors.get(port);
        if (s == null) {
            s = new GyroSensor(this, port);
            this.sensors.put(port, s);
        }
        if(s instanceof GyroSensor)
        {
            return (GyroSensor)s;
        }
        else
        {//Requesting sensor of different type on same port, so recreate
            closeSensor(port);
            return getGyroSensor(port);
        }
    }
    //This javadoc comment is a direct rip from the Java source with unnecessary details removed.
    /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds, subject to
     * the precision and accuracy of system timers and schedulers.
     *
     * @param  millis
     *         the length of time to sleep in milliseconds
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative
     */
    public static void sleep(long millis){
        try{
            Thread.sleep(millis);
        }catch(InterruptedException ie){}
    }
    /** Close a Robot's connections.    
     */
    public void close() {
        // Clone the maps to avoid concurrent modification problems.
        HashMap<Motor.Port,Motor> ms = new HashMap<Motor.Port,Motor>(this.motors);
        for (Motor.Port p: ms.keySet()) {
            this.closeMotor(p);
            Motor m = ms.get(p);
            m.close();
            try {
                m.getThread().join();
            } catch (InterruptedException e) {}
        }
        HashMap<Sensor.Port,Sensor> ss = new HashMap<Sensor.Port,Sensor>(this.sensors);
        for (Sensor.Port p: ss.keySet()) {
            this.closeSensor(p);
            Sensor s = ss.get(p);
            s.close();
            try {
                s.getThread().join();
            } catch (InterruptedException e) {}
        }
        this.sensors = new HashMap < Sensor.Port, Sensor > ();
        
        //Remove shutdown hook to prevent weird behaviour if user manually shuts down robot
        try {
            if (shutdownHook!=null)
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            shutdownHook=null;
        } catch (IllegalStateException e) {
            //Do nothing, IllegalStateException will occur if JVM is process of shutting down
            //The alternative is alot of code to detect that JVM is shutting down.
        } catch (Exception e) {
            //Other errors are likely harmless to
        }
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
    private void setup(String ip) throws Exception{
        this.motors = new HashMap<Motor.Port,Motor>();
        this.sensors = new HashMap<Sensor.Port,Sensor>();
        this.ev3 = new RemoteEV3(ip);
        try {
            shutdownHook = new GracefulExiter(this);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } catch (SecurityException e) {
            System.err.println("Oops, it appears we aren't allowed to create a shutdown hook!");
            System.err.println("Please report this error to Ramsay Taylor (r.g.taylor@shef...)");
        }
    }
    
    //Inner class for gracefully ending communication with robot to prevent the need for manual reboot
    class GracefulExiter extends Thread
    {
        private Robot parent;
        public GracefulExiter(Robot parent)
        {
            this.parent=parent;
        }
        public void run() 
        {
            parent.close();
        }
    }
}