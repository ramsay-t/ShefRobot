package ShefRobot;

import lejos.remote.ev3.*;
import ShefRobot.*;

public class MediumMotor extends Motor
{
    public MediumMotor(Robot robot, Port port)
    {
        super(robot, port, Motor.Type.M);
    }
}