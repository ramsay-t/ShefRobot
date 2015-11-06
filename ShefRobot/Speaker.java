package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Objects of this class represent the audio interface to the {@link Robot} which it was created from
 * @see Robot#getSpeaker()
**/
public class Speaker
{
    /**
     * The maximum integer that can be passed to {@link #setVolume(int) setVolume} or returned from {@link #getVolume() getVolume}
    **/
    public static final int VOLUME_MAX = 100;
    /**
     * The minimum integer that can be passed to {@link #setVolume(int) setVolume} or returned from {@link #getVolume() getVolume}
    **/    
    public static final int VOLUME_MIN = 0;
    /**
     * Holds the ev3 audio interface
    **/
    private Audio speaker;
  
    /**
     * This object should not be constructed directly, it should be created using the {@link Robot#getSpeaker() Robot.getSpeaker}
     * method present in {@link Robot Robot}
     * @param ev3 The ev3 object with which the speaker belongs to.
     * @see Robot#getSpeaker()
    **/
    protected Speaker(RemoteEV3 ev3)
    {
        speaker = ev3.getAudio();
    }
    /**
     * Returns the current volume of the robots speaker
     * @return The current volume of the robots speaker, the value will be in the range of {@link #VOLUME_MIN VOLUME_MIN}-{@link #VOLUME_MAX VOLUME_MAX}.
    **/
    public int getVolume()
    {
        return speaker.getVolume();
    }
    /**
     * Sets the volume of the robots speaker
     * @param volume The volume the robot should play sounds at, value must be in the range {@link #VOLUME_MIN VOLUME_MIN}-{@link #VOLUME_MAX VOLUME_MAX}.
     * @throws IllegalArgumentException When {@code volume} argument is outside of the range {@link #VOLUME_MIN VOLUME_MIN}-{@link #VOLUME_MAX VOLUME_MAX}.
    **/
    public void setVolume(final int volume)
    {
        if(volume>=VOLUME_MIN&&volume<=VOLUME_MAX) {
            speaker.setVolume(volume);
        }
        else {
            throw new IllegalArgumentException("Invalid volume argument: "+volume+"\n Volume values should be within the range 0-100.");
        }
    }
    /**
     * Synchronously plays a tone of the specified frequency and duration through the robots speaker
     * (The current thread will be stalled whilst tone is playing)
     * @param freq The frequency of the tone in Hertz (Hz).
     * @param duration The duration of the tone, in milliseconds (ms).
     * @throws IllegalArgumentException When {@code freq} or {@code duration} arguments are below 1.
    **/
    public void playTone(final int freq, final int duration)
    {
        if(freq <= 0) {
            throw new IllegalArgumentException("Invalid freq argument: "+freq+"\n Frequencies below 1Hz are invalid.");
        }
        else if(duration <= 0) {
            throw new IllegalArgumentException("Invalid duration argument: "+duration+"\n Durations below 1ms are invalid.");
        }
        else {
            speaker.playTone(freq, duration);
        }
    }
    // /**
     // * Plays an 8-bit PCM (pulse-code modulated) .wav file through the robots speaker
     // * @param file A file object that points to the .wav file to be played.
     // * @return The number of milliseconds the sample will play for, or &lt; 0 if there if an error occurs.
     // * @throws IllegalArgumentException When {@code file} is not a wav file.
     // * @throws FileNotFoundException When {@code file} does not exist.
     // * @throws IOException  When read access to the passed file cannot be gained.
    // **/
    // public int playWAV(final File file) throws FileNotFoundException, IOException
    // {
        // if(!file.exists()) {
            // throw new FileNotFoundException("File does not exist: "+file.toString());
        // }
        // else if(!file.isFile()) {
            // throw new IllegalArgumentException("Invalid file argument: "+file.toString()+"\n Is not a file (its probably a directory?).");
        // }///Should test whether ev3 rejects wavs named .wave
        // else if(!((file.getName().toLowerCase().endsWith(".wav"))||(file.getName().toLowerCase().endsWith(".wave")))) {
            // throw new IllegalArgumentException("Invalid file argument: "+file.toString()+"\n Is not a .wav file.");
        // }
        // else if(!file.canRead()) {
            // throw new IOException("Cannot get read access to file: "+file.toString());
        // }
        // else{
            // return speaker.playSample(file);
        // }
    // }
}