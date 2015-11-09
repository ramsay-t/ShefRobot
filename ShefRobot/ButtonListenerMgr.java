package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;
import java.util.*;

/**
 * Internal class used for wrapping KeyListener to manage ButtonListeners
**/
class ButtonListenerMgr implements KeyListener
{
    /**
     * Stores a hashset for each button
     * Each hashset contains the listeners for that button
    **/
    private ArrayList<HashSet<ButtonListener>> blTracker;// = initTracker();
    /**
     * @param ev3 The ev3 of which button listening is to be managed for
    **/
    protected ButtonListenerMgr(RemoteEV3 ev3)
    {
        //Init blTracker
        blTracker = new ArrayList<HashSet<ButtonListener>>(Buttons.Button.values().length);
        for(int i=0; i<Buttons.Button.values().length;i++)
        {
            blTracker.add(i, new HashSet<ButtonListener>());
        }
        //Set this class to listen to all key presses
        for(Buttons.Button b:Buttons.Button.values())
        {
            ev3.getKey(b.getString()).addKeyListener(this);
        }
    }
    /**
     * Catch key press events and forward them to any matching ButtonListener's
     * @param k The lejos Key that has been pressed
    **/
    public void keyPressed(Key k)
    {
      Buttons.Button pressed = Buttons.Button.getButton(k);
      for(ButtonListener bl:blTracker.get(pressed.getId()))
          bl.buttonPressed(pressed);
    }
    /**
     * Catch key release events and forward them to any matching ButtonListener's
     * @param k The lejos Key that has been release
    **/
    public void keyReleased(Key k)
    {
      Buttons.Button released = Buttons.Button.getButton(k);
      for(ButtonListener bl:blTracker.get(released.getId()))
          bl.buttonReleased(released);      
    }
    /**
     * Add a new ButtonListener to be tracked
     * @param b The button to be listened
     * @param bl The button listener to listen with
    **/
    public void addButtonListener(Buttons.Button b, ButtonListener bl)
    {
        blTracker.get(b.id).add(bl);
    }
    /**
     * Removes tracking of an existing ButtonListener from the specified Button
     * @param b The button that is being listened
     * @param bl The button listener to remove
    **/
    public void removeButtonListener(Buttons.Button b, ButtonListener bl)
    {
        blTracker.get(b.id).remove(bl);      
    }
    /**
     * Removes tracking of an existing ButtonListener from all Buttons
     * @param bl The button listener to remove
    **/
    public void removeButtonListener(ButtonListener bl)
    {
        for(int i=0;i<blTracker.size();i++)
        {
            blTracker.get(i).remove(bl);    
        }
    }
    
    
}