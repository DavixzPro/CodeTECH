package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "BeefrostAutonomo")
public class BeefrostAutonomo extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();

        waitForStart();
        if (opModeIsActive()) {

            for (int i = 0; i < 4; i++) {
                andarFrente(0.5, 30);
                girarDireita(0.5, 90);
            }
        }
    }
}
