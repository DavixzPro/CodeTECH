package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoSOS")
public class AutonomoSOS extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();

        waitForStart();
        if (opModeIsActive()) {

            andarEsquerda(80, 90);

        }
    }
}