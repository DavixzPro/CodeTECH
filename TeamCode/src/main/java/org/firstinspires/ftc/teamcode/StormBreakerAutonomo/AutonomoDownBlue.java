package org.firstinspires.ftc.teamcode.StormBreakerAutonomo;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoDownBlue")
public class AutonomoDownBlue extends AutonomoTest {

    @Override
    public void runOpMode() {
        configurarRobo();
        waitForStart();
        if (opModeIsActive()) {

            andarFrente(0.9, 130);
            esperar(6);
            andarFrente(0.9, 240);
            girarEsquerda(290);
            ligarShooter(0.8);
            esperar(3);
            ligarHelpWheels(1);
            esperar(0.7);
            ligarIntake(1);
            esperar(2);
            andarEsquerda(0.9, 200);
            pararTudo();
        }
    }
}