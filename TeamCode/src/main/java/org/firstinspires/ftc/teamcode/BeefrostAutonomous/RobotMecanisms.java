package org.firstinspires.ftc.teamcode.BeefrostAutonomous;


public abstract class RobotMecanisms extends RobotMovement {
    void ligarIntake() {
        Intakes.setPower(1);
    }
    void desligarIntake() {
        Intakes.setPower(0);
    }
    void travarShooter() { Trava.setPosition(0.1); }
    void liberarShooter() { Trava.setPosition(0.4); }
    void PolenPosition() {
        ExtensorDireito.setPosition(0.45);
        ExtensorEsquerdo.setPosition(0.55);
    }
    void NectarPosition() {
        ExtensorDireito.setPosition(0.7);
        ExtensorEsquerdo.setPosition(0.3);
    }
    void ligarShooter() {
        Shooter.setVelocity(polenVelocity);
        seguirAprilTag = !seguirAprilTag;
    }
    void desligarShooter() {
        Shooter.setVelocity(0);
        seguirAprilTag = !seguirAprilTag;
    };

    void esperar(double segundos) {
        sleep((long)(segundos * 1000));
    }
}