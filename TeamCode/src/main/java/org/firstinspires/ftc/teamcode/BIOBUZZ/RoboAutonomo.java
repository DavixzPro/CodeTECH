package org.firstinspires.ftc.teamcode.BIOBUZZ;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "RoboAutonomo")
public class RoboAutonomo extends RobotMecanisms  {

    @Override
    public void runOpMode() {
        ConfigureRobot();

        waitForStart();
        if (opModeIsActive()) {

            for (int i = 0; i < 16; i++) {
                andarFrente(0.7, 40);
                girarDireita(0.7, 90);
            }
        }
    }
}
