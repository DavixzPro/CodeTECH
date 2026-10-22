package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoWall_RED")
public class AutonomoWall_RED extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();
        if (opModeIsActive()) {


            andarFrente(0.6, 50);
            andarEsquerda(6.0, 97);
            andarFrente(0.6, 80);
            esperar(1);
            girarEsquerda(0.5,90);
            esperar(1);
            andarFrente(10,10);
            alinharAprilTag(0.3);
            liberarShooter();
            ligarShooter();
            esperar(2);
            ligarIntake();
            esperar(3);
            desligarIntake();
            desligarShooter();
            andarTras(40,25);
            esperar(1);
            girarDireita(0.5,90);
            esperar(1);
            andarTras(0.7, 113);


        }
    }

}