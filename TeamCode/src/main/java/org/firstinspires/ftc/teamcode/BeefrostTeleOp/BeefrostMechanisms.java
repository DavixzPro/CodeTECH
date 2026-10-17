package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class BeefrostMechanisms {

    private final BeefrostConfig robot;
    private final OpMode opMode;

    public BeefrostMechanisms(
            BeefrostConfig robot,
            OpMode opMode
    ) {
        this.robot = robot;
        this.opMode = opMode;
    }

    public void atualizar() {

        controlarShooter();
        controlarIntake();
        controlarServos();

    }

    private void controlarShooter() {

        if (opMode.gamepad1.bWasPressed()) {
            robot.shooterLigado = !robot.shooterLigado;
            robot.seguirAprilTag = !robot.seguirAprilTag;
        }

        if (robot.shooterLigado) {
            if (robot.shooterMode) {
                robot.Shooter.setVelocity(robot.polenVelocity);
            } else {
                robot.Shooter.setVelocity(robot.nectarVelocity);
            }
        } else {
            robot.Shooter.setVelocity(0);
        }

        if (robot.shooterLigado && !robot.shooterTravado) {
            liberarShooter();
        } else {
            travarShooter();
        }
    }

    private void controlarIntake() {

        if (opMode.gamepad1.x) {
            robot.Intakes.setPower(-1);
        } else if (opMode.gamepad1.y) {
            robot.Intakes.setPower(1);
        } else {
            robot.Intakes.setPower(0);
        }

        if (opMode.gamepad1.leftBumperWasPressed()) {
            FlowerOff();
        }
        if (opMode.gamepad1.rightBumperWasPressed()) {
            FlowerOn();
        }


    }

    private void controlarServos() {

        if (opMode.gamepad1.dpad_left) {
            PolenPosition();
        }

        if (opMode.gamepad1.dpad_right) {
            NectarPosition();
        }
    }

    public void PolenPosition() {

        robot.ExtensorDireito.setPosition(0.45);
        robot.ExtensorEsquerdo.setPosition(0.55);
        robot.shooterMode = true;

    }
    public void NectarPosition() {

        robot.ExtensorDireito.setPosition(0.7);
        robot.ExtensorEsquerdo.setPosition(0.3);
        robot.shooterMode = false;

    }
    public void FlowerOn() {
        robot.Lily.setPosition(0.0);
        robot.Violet.setPosition(1.0);

    }
    public void FlowerOff() {
        robot.Lily.setPosition(0.5);
        robot.Violet.setPosition(0.0);

    }

    private void liberarShooter() {

        robot.Trava.setPosition(0.4);
        robot.FlowerSensor.enableLed(true);

    }
    private void travarShooter() {

        robot.Trava.setPosition(0.2);
        robot.FlowerSensor.enableLed(false);

    }
}
