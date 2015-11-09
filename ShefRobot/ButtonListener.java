package ShefRobot;
/**
 * Listener interface for catching Button press and release events.
 * The methods for connecting ButtonListener's have been disabled due to inconsistent behaviour
 // * @see Buttons#addListener(Buttons.Button)
**/
public abstract interface ButtonListener
{
    /**
     * Invoked when the listened button is pressed
     * @param button {@link Buttons.Button} that has been pressed
    **/
    public abstract void buttonPressed(Buttons.Button button);
    /**
     * Invoked when the listened button is released
     * @param button {@link Buttons.Button} that has been released
    **/
    public abstract void buttonReleased(Buttons.Button button); 
}