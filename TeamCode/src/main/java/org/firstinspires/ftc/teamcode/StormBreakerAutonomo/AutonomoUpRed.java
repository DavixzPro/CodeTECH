package org.firstinspires.ftc.teamcode.StormBreakerAutonomo;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutonomoUpRed")
public class AutonomoUpRed extends AutonomoTest {

    @Override
    public void runOpMode() {
        configurarRobo();
        waitForStart();
        if (opModeIsActive()) {

            //PRIMEIRO LANÇAMENTO
            ligarShooter(0.8);
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
            girarDireita(179);
            andarDireita(0.9, 142);
            ligarIntake(1);
            andarFrente(0.9, 130);
            desligarIntake();
            andarTras(0.9, 110);
            ligarShooter(0.70);
            andarEsquerda(0.9, 140);
            girarEsquerda(180);
            andarFrente(0.9,30);
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

            //ARTEFATOS DE CIMA
            girarDireita(160);
            andarDireita(0.9, 35);
            ligarIntake(1);
            ligarHelpWheels(1);
            andarFrente(0.9, 100);
            desligarIntake();
            desligarHelpWheels();
            andarTras(0.9, 100);

            pararTudo();
        }
    }
}