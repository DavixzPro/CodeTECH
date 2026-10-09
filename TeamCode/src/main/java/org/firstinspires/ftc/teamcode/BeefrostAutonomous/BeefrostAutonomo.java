package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BeefrostAutonomo")
public class BeefrostAutonomo extends RobotMecanisms  {

    @Override
    public void runOpMode() {
        ConfigureRobot();
        PolenPosition();
        travarShooter();

        waitForStart();
        if (opModeIsActive()) {

            for (int i = 0; i < 16; i++) {
                andarFrente(0.7, 40);
                girarDireita(0.7, 90);
            }
        }
    }
}
