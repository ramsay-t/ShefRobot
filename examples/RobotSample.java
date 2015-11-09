import ShefRobot.*;

/**
 *
 * @author sdn
 */
public class RobotSample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //These lines go at the start of every program
        Robot myRobot = new Robot();
       
        //Get references to the objects we wish to use.
        Motor leftMotor = myRobot.getLargeMotor(Motor.Port.B);
        Motor rightMotor = myRobot.getLargeMotor(Motor.Port.C);
        Speaker speaker = myRobot.getSpeaker();

        //Go Forwards
        leftMotor.setSpeed(150);
        rightMotor.setSpeed(150);
        leftMotor.forward();
        rightMotor.forward();

        //Keep going for 5 seconds
        myRobot.sleep(5000);

        //Stop
        leftMotor.stop();
        rightMotor.stop();

        //Beep at 1000Hz for half a second
        speaker.playTone(1000, 500);

        //Go Backwards
        leftMotor.backward();
        rightMotor.backward();

        //Keep going for 5 seconds
        myRobot.sleep(5000);

        //Stop
        leftMotor.stop();
        rightMotor.stop();
        
        //Disconnect from the Robot
        myRobot.close();

    }

}
