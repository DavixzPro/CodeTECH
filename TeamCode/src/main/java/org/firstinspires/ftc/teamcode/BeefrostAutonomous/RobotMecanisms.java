package org.firstinspires.ftc.teamcode.BeefrostAutonomous;


public abstract class RobotMecanisms extends RobotMovement {
    void ligarIntakes() {
        Intakes.setPower(1);
    }
    void desligarIntakes() {
        Intakes.setPower(0);
    }
    void liberarShooter() { Trava.setPosition(0.0); }
    void travarShooter() { Trava.setPosition(1.0); }
    void PolenPosition() { ExtensorEsquerdo.setPosition(0.5); ExtensorDireito.setPosition(0.5); }
    void NectarPosition() { ExtensorEsquerdo.setPosition(0.85); ExtensorDireito.setPosition(0.15); }
    void ligarShooter() {
        Shooter.setVelocity(targetVelocity);
    }
    void desligarShooter() { Shooter.setVelocity(0); }
    void esperar(double segundos) {
        sleep((long)(segundos * 1000));
    }
}