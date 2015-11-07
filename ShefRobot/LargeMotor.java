package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;
/**
 * Represents a large motor
 * Each EV3 should contain 2 large motors
 * All {@code LargeMotor} methods are common to {@code Motor}, this class is only used for calibration at construction.
 * @see Motor
**/
public class LargeMotor extends Motor
{
    protected LargeMotor(Robot robot, Port port)
    {
        super(robot, port, Motor.Type.L);
    }
}