package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoWall_BLUE")
public class AutonomoWall_BLUE extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();
        limelight.pipelineSwitch(1);
        limelight.start();

        waitForStart();
        if (opModeIsActive()) {

            andarFrente(0.6, 50);
            andarEsquerda(6.0, 100);
            andarFrente(0.6, 80);
            girarEsquerda(0.5,70);
            andarDireita(0.4, 14);
            andarFrente(0.5,15);
            alinharAprilTag(0.4);
            liberarShooter();
            ligarShooter();
            esperar(2);
            ligarIntake();
            esperar(3);
            desligarIntake();
            desligarShooter();
            andarTras(40,25);
            girarDireita(0.5,90);
            andarTras(0.6, 130);

        }
    }
}