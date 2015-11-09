package ShefRobot;

import lejos.remote.ev3.*;
import lejos.hardware.*;
import lejos.robotics.*;
import java.rmi.*;
import java.util.*;

/**
 * Objects of this class represent the Button interface to the {@link Robot} which it was created from
 * Provides the ability to wait for a button on the robot to be pressed.
 * Pressed buttons DO affect the Robots menus, so choose button combinations wisely (e.g. only use up/down/left/right).
 * @see Robot#getButtons()
**/
public class Buttons
{

    public enum Button {
        UP    (0, Keys.ID_UP, "Up"), 
        DOWN  (1, Keys.ID_DOWN, "Down"), 
        LEFT  (2, Keys.ID_LEFT, "Left"), 
        RIGHT (3, Keys.ID_RIGHT, "Right"), 
        ENTER (4, Keys.ID_ENTER, "Enter"), 
        ESCAPE(5, Keys.ID_ESCAPE, "Escape");
        Button(final int id, final int lejosKey, final String lejosKeyString)
        {
            this.id=id;
            this.lejosKey = lejosKey;
            this.lejosKeyString = lejosKeyString;
        }
        protected int getKey(){return lejosKey;}
        protected String getString(){return lejosKeyString;}
        protected int getId(){return id;}
        public final int id;
        private final int lejosKey;
        private final String lejosKeyString;
        protected static Button getButton(final String lejosKey){
          for(Button b:Button.values())
          {
              if(b.getString().equals(lejosKey))
                  return b;
          }
          return null;
        }
        protected static Button getButton(final Key lejosKey){
          return getButton(lejosKey.getId());
        }
        protected static Button getButton(final int lejosKey){
          for(Button b:Button.values())
          {
              if(b.getKey()==lejosKey)
                  return b;
          }
          return null;
        }
    };
    private Keys keys;
    private ButtonListenerMgr blMgr;
    private RemoteEV3 ev3;
    /**
     * This object should not be constructed directly, it should be created using {@link Robot#getButtons() Robot.getButtons}
     * method present in {@link Robot Robot}
     * @param ev3 The ev3 object with which the buttons belongs to.
     * @see Robot#getButtons()
    **/
    protected Buttons(RemoteEV3 ev3)
    {
        this.ev3 = ev3;
        keys = ev3.getKeys();
        keys.discardEvents();
        blMgr = new ButtonListenerMgr(ev3);
    }
    public void waitForButton(Button button)
    {
        ev3.getKey(button.getString()).waitForPressAndRelease();
    }
    public void waitForAnyButton()
    {
        keys.waitForAnyPress();
    }
    //Button listeners disabled due to inconsistent behaviour
    // /**
     // * Add a new ButtonListener to be tracked
     // * @param b The button to be listened
     // * @param bl The button listener to listen with
    // **/
    // public void addButtonListener(Button b, ButtonListener bl)
    // {
        // blMgr.addButtonListener(b, bl);
    // }
    // /**
     // * Removes tracking of an existing ButtonListener from the specified Button
     // * @param b The button that is being listened
     // * @param bl The button listener to remove
    // **/
    // public void removeButtonListener(Button b, ButtonListener bl)
    // {
        // blMgr.removeButtonListener(b, bl);
    // }
    // /**
     // * Removes tracking of an existing ButtonListener from all Buttons
     // * @param bl The button listener to remove
    // **/
    // public void removeButtonListener(ButtonListener bl)
    // {
        // removeButtonListener(bl);
    // }

}