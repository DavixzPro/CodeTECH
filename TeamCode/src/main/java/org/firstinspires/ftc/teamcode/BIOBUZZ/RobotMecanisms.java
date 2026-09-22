package org.firstinspires.ftc.teamcode.BIOBUZZ;


public abstract class RobotMecanisms extends RobotMovement {
    void ligarIntakes() { Intakes.setPower(1); }
    void desligarIntakes() { Intakes.setPower(0); }
    void liberarShooter() {Trava.setPosition(1.0); }
    void travarShooter() { Trava.setPosition(0.5); }
    void NectarLaunch() { ServoShooter.setPosition(0.5); }
    void PolenLaunch() { ServoShooter.setPosition(1.0); }
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