package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;
import ShefRobot.util.*;
import java.rmi.RemoteException;
//import java.awt.Color;
import lejos.hardware.sensor.EV3ColorSensor;
/**
 * Represents a color sensor, that can be used for detecting 8 discrete colours or ambient and reflected light levels
 * Each EV3 should contain 1 color sensor
 * If the sensor seems insensitive, please check that the EV3s battery is charged.
 * If you are switching between sensor modes without a delay, you may receive incorrect or out of range values (e.g. Calling {@link ColorSensor.Mode#getAmbient()} directly after {@link ColorSensor.Mode#getColor()} is likely to cause {@link ColorSensor.Mode#getAmbient()} to return a bad value. This can be fixed by waiting for around 300ms after the first call to {@link ColorSensor.Mode#getAmbient()} so the sensor has time to adjust.)
 * @see Sensor
**/
enum ColorSensorAction{
    GET_VALUE, GET_COLOR, GET_AMBIENT, GET_RED, GET_RGB, GET_FLOODLIGHT_STATE, SET_FLOODLIGHT_STATE;
}
/**
 * This class represents an EV3 ColorSensor which has four modes of operation {@link ColorSensor.Mode#COLOR}, {@link ColorSensor.Mode#RED}, {@link ColorSensor.Mode#RGB} and {@link ColorSensor.Mode#AMBIENT}
 * By default the sensor starts in {@link ColorSensor.Mode#RED} mode.
**/
public class ColorSensor extends Sensor<ColorSensorAction>
{
    public enum FloodlightState {
        OFF(-1), RED(0), BLUE(2), WHITE(6);
        protected final int internalId;
        FloodlightState(int internalId)
        {
            this.internalId=internalId;
        }
        protected static FloodlightState getFromID(int id)
        {
            for(FloodlightState fs:FloodlightState.values())
                if(fs.internalId==id)
                    return fs;
            return FloodlightState.OFF;
        }
    }
    /**
     * Modes that this sensor can be used in
    **/
    public enum Mode {
        /**
         * Measures the color ID of a surface.
        **/
        COLOR("ColorID", 0, FloodlightState.WHITE),
        /**
         * Measures the level of reflected light from the sensors RED LED.
        **/
        RED("Red", 1, FloodlightState.RED),
        /**
         * Measures the level of red, green and blue light when illuminated by a white light source.
        **/
        RGB("RGB", 2, FloodlightState.WHITE),
        /**
         * Measures the level of ambient light while the sensors light is blue.
        **/
        AMBIENT("Ambient", 3, FloodlightState.OFF);
        protected final String internalString;
        protected final int internalId;
        protected final FloodlightState lightState;
        Mode(String internalString, int internalId, FloodlightState lightState)
        {
            this.internalString=internalString;
            this.internalId=internalId;
            this.lightState=lightState;
        }
    }
    protected ColorSensor(Robot robot, Port port)
    {
        super(robot, port, Sensor.Type.COLOR);
    }
    /**
     * Switches the sensor between modes
     * @param newMode The desired mode to switch to
    **/
    public void setMode(Mode newMode)
    {
        this.sensor.setCurrentMode(newMode.internalId);
        //Switching mode doesn't do this automatically until a sample is requested.
        setFloodlightState(newMode.lightState);
        try
        {
            Thread.sleep(300);
        }
        catch(Exception e){}
    }
    /**
     * Changes the state of the floodlight to a colour or off
     * Calls to read the sensor will change the floodlight back to the required colour
     * @param newState The desired state to switch to
    **/
    public void setFloodlightState(FloodlightState newState)
    {
        Pair<ColorSensorAction,float[]> action = new Pair<ColorSensorAction,float[]>(ColorSensorAction.SET_FLOODLIGHT_STATE, new float[]{(float)newState.internalId});
        this.addAction(action);       
        while (action.getValue() != null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
    }
    /**
     * Reads the current state of the floodlight
     * @return The current state of the floodlight
    **/
    public FloodlightState getFloodlightState()
    {
        Pair<ColorSensorAction,float[]> action = new Pair<ColorSensorAction,float[]>(ColorSensorAction.GET_FLOODLIGHT_STATE, null);
        this.addAction(action);
        while (action.getValue() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return FloodlightState.getFromID((int)action.getValue()[0]);
    }
    
    /**
     * Enums used to represent the possible colours returned by the Color sensor
    **/
    public enum Color{
        RED(lejos.robotics.Color.RED),
        GREEN(lejos.robotics.Color.GREEN),
        BLUE(lejos.robotics.Color.BLUE),
        YELLOW(lejos.robotics.Color.YELLOW),
        //MAGENTA(lejos.robotics.Color.MAGENTA),
        //ORANGE(lejos.robotics.Color.ORANGE),
        WHITE(lejos.robotics.Color.WHITE),
        BLACK(lejos.robotics.Color.BLACK),
        //PINK(lejos.robotics.Color.PINK),
        //GRAY(lejos.robotics.Color.GRAY),
        //LIGHT_GRAY(lejos.robotics.Color.LIGHT_GRAY),
        //DARK_GRAY(lejos.robotics.Color.DARK_GRAY),
        //CYAN(lejos.robotics.Color.CYAN),
        BROWN(lejos.robotics.Color.BROWN),
        NONE(lejos.robotics.Color.NONE);
        protected final int internalId;
        Color(int internalId)
        {
            this.internalId=internalId;
        }
    }
    /**
     * Returns the colour detected by the sensor as one of the {@link ColorSensor.Color} values.
     * If not already in color mode, this will switch the sensor into color mode (enabling the white [rgb] light).
     * @return Returns a {@link ColorSensor.Color} enum
    **/
    public Color getColor()
    {
        int result = (int)(sendAction(ColorSensorAction.GET_COLOR)[0]);
        //Parse returned int into the correct color;
        for(Color c:Color.values())
            if(c.internalId == result)
                return c;
        return Color.NONE;
    }
    /**
     * Returns a value representative of the light intensity detected.
     * If not already in ambient mode, this will switch the sensor into ambient mode (enabling the blue light).
     * @return Returns a value in the range 0.0-1.0
    **/
    public float getAmbient()
    {
        setFloodlightState(FloodlightState.BLUE);
        float[] sample = getRawSample();//sendAction(ColorSensorAction.GET_AMBIENT);
        //Parse returned int into the ambient light;
        return sample[0]*5;
    }
    /**
     * Returns the level of red reflected into the sensor
     * If not already in red mode, this will switch the sensor into red mode (enabling the red light).
     * @return Returns a value in the range 0.0-0.5
    **/
    public float getRed()
    {
        float[] result = sendAction(ColorSensorAction.GET_RED);
        //Parse returned red level
        return result[0];
    }
    /**
     * Returns the colour detected by the sensor
     * ~untested~
     * @return The RGB value detected by the sensor as a java Color object.
    **/
    public java.awt.Color getRGB()
    {
        float[] result = sendAction(ColorSensorAction.GET_RGB);
        //Parse returned int into the ambient light;
        return new java.awt.Color(result[0],result[1],result[2]);
    }
    /**
     * Convenience method  for sending an action and waiting for a response
     * @param act The action to be sent
     * @return The response recieved from the sensor
    **/ 
    private float[] sendAction(ColorSensorAction act)
    {
        Pair<ColorSensorAction,float[]> action = new Pair<ColorSensorAction,float[]>(act, null);
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
    protected void subAction(Pair<ColorSensorAction,float[]> act) throws RemoteException
    {
        float[] samples;
        switch (act.key) {
            case GET_VALUE:
                samples = new float[this.sensor.sampleSize()];
                this.sensor.fetchSample(samples, 0);
                act.setValue(samples);
                break;
            case SET_FLOODLIGHT_STATE:
                ((EV3ColorSensor)this.sensor).setFloodlight((int)act.getValue()[0]);
                act.setValue(null);
                break;
            case GET_FLOODLIGHT_STATE:
                samples = new float[1];
                samples[0] = (int)((EV3ColorSensor)this.sensor).getFloodlight();
                act.setValue(samples);
                break;
            case GET_COLOR:
                samples = new float[((EV3ColorSensor)this.sensor).getColorIDMode().sampleSize()];
                ((EV3ColorSensor)this.sensor).getColorIDMode().fetchSample(samples,0);
                act.setValue(samples);
                break;
            case GET_AMBIENT:
                samples = new float[((EV3ColorSensor)this.sensor).getAmbientMode().sampleSize()];
                ((EV3ColorSensor)this.sensor).getAmbientMode().fetchSample(samples,0);
                ((EV3ColorSensor)this.sensor).getAmbientMode().fetchSample(samples,0);
                act.setValue(samples);
                break;
            case GET_RED:
                samples = new float[((EV3ColorSensor)this.sensor).getRedMode().sampleSize()];
                ((EV3ColorSensor)this.sensor).getRedMode().fetchSample(samples,0);
                act.setValue(samples);
                break;
            case GET_RGB:
                samples = new float[((EV3ColorSensor)this.sensor).getRGBMode().sampleSize()];
                ((EV3ColorSensor)this.sensor).getRGBMode().fetchSample(samples,0);
                act.setValue(samples);
                break;
            default: 
                System.err.println("[" + this.port.name() + "] Asked for Action: " + act.key + " on a ColorSensor...");  
        }
    }
}