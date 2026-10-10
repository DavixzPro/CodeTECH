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

            alinharAprilTag(0.2);
            ligarShooter();
            esperar(1);
            ligarIntake();
            esperar(3);
            desligarIntake();
            desligarShooter();
            andarTras(0.6, 15);
            andarDireita(0.6, 80);
            andarTras(0.6, 260);
            andarDireita(0.6, 40);
        }
    }
}
