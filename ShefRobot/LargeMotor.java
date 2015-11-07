package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;

public class LargeMotor extends Motor
{
    public LargeMotor(Robot robot, Port port)
    {
        super(robot, port, Motor.Type.L);
    }
}