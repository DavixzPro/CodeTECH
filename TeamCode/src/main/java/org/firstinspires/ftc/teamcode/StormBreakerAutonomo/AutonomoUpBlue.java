package org.firstinspires.ftc.teamcode.StormBreakerAutonomo;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoUpBlue")
public class AutonomoUpBlue extends AutonomoTest {

    @Override
    public void runOpMode() {

        configurarRobo();

        waitForStart();
        if (opModeIsActive()) {

            //PRIMEIRO LANÇAMENTO
            ligarShooter(0.7);
            andarTras(0.8, 110);
            esperar(2.2);
            ligarHelpWheels(1);
            esperar(0.7);
            ligarIntake(1);
            esperar(2);
            desligarShooter();
            desligarIntake();
            desligarHelpWheels();

            //ARTEFATOS DO MEIO
            girarEsquerda(179);
            andarEsquerda(0.9, 142);
            ligarIntake(1);
            andarFrente(0.9, 130);
            desligarIntake();
            andarTras(0.9, 110);
            ligarShooter(0.70);
            andarDireita(0.9, 140);
            girarDireita(180);
            andarFrente(0.90,30);
            ligarIntake(1);
            ligarHelpWheels(1);
            esperar(0.7);
            desligarHelpWheels();
            esperar(0.7);
            ligarHelpWheels(1);
            esperar(2);
            desligarShooter();
            desligarIntake();
            desligarHelpWheels();

            girarEsquerda(180);
            andarEsquerda(0.9, 45);;
            andarFrente(0.9, 100);
            andarDireita(0.9, 30);
            desligarIntake();
            desligarHelpWheels();
            andarTras(0.9, 100);
            pararTudo();
        }
    }
}