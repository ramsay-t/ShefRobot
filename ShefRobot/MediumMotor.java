package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;

/**
 * Represents a medium motor
 * Each EV3 should contain 1 medium motor
 * All {@code MediumMotor} methods are common to {@code Motor}, this class is only used for calibration at construction.
 * @see Motor
**/
public class MediumMotor extends Motor
{
    protected MediumMotor(Robot robot, Port port)
    {
        super(robot, port, Motor.Type.M);
    }
}