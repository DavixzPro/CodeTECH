package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Malcriado")
public class travarnolugar extends RobotMecanisms  {

    @Override
    public void runOpMode() {


        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();
        TOLERANCIA_CM = 0.3;

        waitForStart();
        if (opModeIsActive()) {

            andarFrente(0.7, 5);

        }
    }
}
