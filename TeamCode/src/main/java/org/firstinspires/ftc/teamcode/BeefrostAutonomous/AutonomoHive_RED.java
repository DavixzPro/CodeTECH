package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoHive_RED")
public class AutonomoHive_RED extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        NectarPosition();
        travarShooter();
        setTelemetry();
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();
        if (opModeIsActive()) {

            PolenPosition();
            alinharAprilTag(0.3);
            ligarShooter();
            liberarShooter();
            andarTras(0.5, 5);
            esperar(2);
            for (int i = 0; i < 4; i ++) {
                ligarIntake();
                esperar(0.25);
                desligarIntake();
                esperar(0.25);
            }
            desligarShooter();
            andarDiagonalTrasDireita(0.7, 110);
            girarEsquerda(0.6, 10);
            andarTras(0.5, 165);
            andarDireita(0.4, 40);

        }
    }
}
