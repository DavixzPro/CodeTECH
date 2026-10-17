package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import java.util.ArrayDeque;
import java.util.Queue;

public class BeefrostMechanisms {

    private final BeefrostConfig robot;
    private final OpMode opMode;

    private enum BolaTipo {
        NECTAR,
        POLLEN,
        UNKNOWN
    }

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

//    private void controlarServos() {
//
//        if (opMode.gamepad1.dpad_left) {
//            PolenPosition();
//        }
//
//        if (opMode.gamepad1.dpad_right) {
//            NectarPosition();
//        }
//    }
    public boolean isRed(ColorSensor sensor) {
        int r = sensor.red();
        int g = sensor.green();
        int b = sensor.blue();

        return (r > g * 1.5 && r > b * 1.5);
    }
    public boolean isBlue(ColorSensor sensor) {
        int r = sensor.red();
        int g = sensor.green();
        int b = sensor.blue();

        return (b > r * 1.5 && b > g * 1.5);
    }

    private final Queue<BolaTipo> shootingQueue = new ArrayDeque<>();

    private BolaTipo detectarTipo() {
        boolean red1 = isRed(robot.CS1);
        boolean blue1 = isBlue(robot.CS1);
        boolean red2 = isRed(robot.CS2);
        boolean blue2 = isBlue(robot.CS2);

        boolean bothRed = red1 && red2;
        boolean bothBlue = blue1 && blue2;

        BolaTipo tipo;

        if (red1 && red2) {
            tipo =  BolaTipo.NECTAR;
        } else if (blue1 && blue2 ) {
            tipo = BolaTipo.NECTAR;
        } else {
            tipo = BolaTipo.POLLEN;
        }

        telemetry.addData("CS1 Red", red1);
        telemetry.addData("CS2 Red", red2);
        telemetry.addData("CS1 Blue", blue1);
        telemetry.addData("CS2 Blue", blue2);

        telemetry.addData("Both red", bothRed);
        telemetry.addData("Both blue", bothBlue);
        telemetry.addData("Detected type", tipo);

        return tipo;
     }
    private void controlarServos() {
        BolaTipo tipo = detectarTipo();
        switch (tipo) {
            case NECTAR:
                NectarPosition();
                break;
            case POLLEN:
                PolenPosition();
                break;
            case UNKNOWN:
                break;
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

    private void liberarShooter() { robot.Trava.setPosition(0.4); }
    private void travarShooter() { robot.Trava.setPosition(0.2); }
}
