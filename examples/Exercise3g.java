import ShefRobot.*;

public class Exercise3g {

    public static void main(String[] args) {
        final int SIDE;
        final int TURNING_SPEED=200;
        final int WALKING_SPEED=500;
        final int WALKING_TIME=2000;
        final int TURN_TIME=1500;
        
        Robot myRobot = new Robot();
        
        Motor leftMotor = myRobot.getLargeMotor(Motor.Port.B);
        Motor rightMotor = myRobot.getLargeMotor(Motor.Port.C);
        Speaker speaker = myRobot.getSpeaker();
        
        //Walk in a square
        //Walk forward half a side
        leftMotor.setSpeed(WALKING_SPEED);
        rightMotor.setSpeed(WALKING_SPEED);
        leftMotor.forward();
        rightMotor.forward();
        myRobot.sleep(WALKING_TIME / 2);
        leftMotor.stop();
        rightMotor.stop();
    
        //Turn 90 degrees to right
        speaker.playTone(1000,200); 
        rightMotor.setSpeed(TURNING_SPEED);
        rightMotor.forward();
        myRobot.sleep(TURN_TIME);
        rightMotor.stop();
        
        //Walk forward a full side
        leftMotor.setSpeed(WALKING_SPEED);
        rightMotor.setSpeed(WALKING_SPEED);
        leftMotor.forward();
        rightMotor.forward();
        myRobot.sleep(WALKING_TIME);
        leftMotor.stop();
        rightMotor.stop();

        //Turn 90 degrees to right
        speaker.playTone(1000,200); 
        rightMotor.setSpeed(TURNING_SPEED);
        rightMotor.forward();
        myRobot.sleep(TURN_TIME);
        rightMotor.stop();
        
        //Walk forward a full side
        leftMotor.setSpeed(WALKING_SPEED);
        rightMotor.setSpeed(WALKING_SPEED);
        leftMotor.forward();
        rightMotor.forward();
        myRobot.sleep(WALKING_TIME);
        leftMotor.stop();
        rightMotor.stop();

        //Turn 90 degrees to right
        speaker.playTone(1000,200); 
        rightMotor.setSpeed(TURNING_SPEED);
        rightMotor.forward();
        myRobot.sleep(TURN_TIME);
        rightMotor.stop();
        
        //Walk forward a full side
        leftMotor.setSpeed(WALKING_SPEED);
        rightMotor.setSpeed(WALKING_SPEED);
        leftMotor.forward();
        rightMotor.forward();
        myRobot.sleep(WALKING_TIME);
        leftMotor.stop();
        rightMotor.stop();

        //Turn 90 degrees to right
        speaker.playTone(1000,200); 
        rightMotor.setSpeed(TURNING_SPEED);
        rightMotor.forward();
        myRobot.sleep(TURN_TIME);
        rightMotor.stop();
        
        //Walk forward half a side
        leftMotor.setSpeed(WALKING_SPEED);
        rightMotor.setSpeed(WALKING_SPEED);
        leftMotor.forward();
        rightMotor.forward();
        myRobot.sleep(WALKING_TIME / 2);
        leftMotor.stop();
        rightMotor.stop();
        
        
        myRobot.close();            
    }


}
