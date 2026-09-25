package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RoboAutonomo")
public class RoboAutonomo extends RobotMecanisms  {

    @Override
    public void runOpMode() {
        ConfigureRobot();
        PolenLaunch();
        Trava.setPosition(0.5);

        waitForStart();
        if (opModeIsActive()) {

            for (int i = 0; i < 16; i++) {
                andarFrente(0.7, 40);
                girarDireita(0.7, 90);
            }
        }
    }
}
