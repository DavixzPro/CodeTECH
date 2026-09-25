package org.firstinspires.ftc.teamcode.BeefrostAutonomous;


public abstract class RobotMecanisms extends RobotMovement {
    void ligarIntakes() {
        Intakes.setPower(1);
        ServoFlowerD.setPower(1);
        ServoFlowerE.setPower(-1);
    }
    void desligarIntakes() {
        Intakes.setPower(0);
        ServoFlowerD.setPower(0);
        ServoFlowerE.setPower(0);
    }
    void liberarShooter() {Trava.setPosition(0.5); }
    void travarShooter() { Trava.setPosition(1.0); }
    void PolenLaunch() { ExtensorEsquerdo.setPosition(0.5); ExtensorDireito.setPosition(0.5); }
    void NectarLaunch() { ExtensorEsquerdo.setPosition(0.85); ExtensorDireito.setPosition(0.15); }
    void ligarShooter() {
        double potencia;

        if (distancia <= 21) {
            potencia = 0.60;
        } else if (distancia >= 33) {
            potencia = 0.45;
        } else {
            potencia = 0.45 + ((33.0 - distancia) * (0.15 / 12.0));
        }

        Shooter.setPower(potencia);
    }
    void desligarShooter() { Shooter.setPower(0); }
    void esperar(double segundos) {
        sleep((long)(segundos * 1000));
    }
}