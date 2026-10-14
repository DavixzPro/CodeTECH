package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoWall")
public class AutonomoWall extends RobotMecanisms  {

    @Override
    public void runOpMode() {

        ConfigureRobot();
        PolenPosition();
        travarShooter();
        setTelemetry();

        waitForStart();
        if (opModeIsActive()) {


            andarFrente(70, 50);
            andarEsquerda(70, 110);
            girarDireita(50, 90);
            andarEsquerda(70, 80);
            esperar(1);
            girarDireita(50,180);
            ligarShooter();
            esperar(5);
            ligarIntake();
            esperar(2);
            desligarIntake();
            desligarShooter();
            andarEsquerda(80, 125);
            girarEsquerda(50,90);
        }
    }

}