package org.firstinspires.ftc.teamcode.BeefrostAutonomous;


public abstract class RobotMecanisms extends RobotMovement {
    void ligarIntake() {
        Intakes.setPower(1);
    }
    void desligarIntake() {
        Intakes.setPower(0);
    }
    void liberarShooter() { Trava.setPosition(0.0); }
    void travarShooter() { Trava.setPosition(1.0); }
    void PolenPosition() { ExtensorEsquerdo.setPosition(0.6); ExtensorDireito.setPosition(0.4); }
    void NectarPosition() { ExtensorEsquerdo.setPosition(0.3); ExtensorDireito.setPosition(0.7); }
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