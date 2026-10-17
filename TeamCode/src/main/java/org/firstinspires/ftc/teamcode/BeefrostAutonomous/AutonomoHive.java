package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoHive")
public class AutonomoHive extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();

        waitForStart();
        if (opModeIsActive()) {

            alinharAprilTag(0.3);
            ligarShooter();
            liberarShooter();
            esperar(2);
            ligarIntake();
            esperar(5);
            desligarIntake();
            desligarShooter();
            andarDiagonalTrasDireita(0.6, 170);
            andarTras(0.5, 120);

        }
    }
}
