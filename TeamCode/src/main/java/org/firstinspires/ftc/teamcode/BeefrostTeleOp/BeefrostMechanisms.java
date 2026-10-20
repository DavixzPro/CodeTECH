package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayDeque;
import java.util.Deque;

public class BeefrostMechanisms {

    private final BeefrostConfig robot;
    private final OpMode opMode;

//    private enum BolaTipo {
//        NECTAR,
//        POLLEN,
//        UNKNOWN
//    }

//    private final Deque<BolaTipo> shootingQueue = new ArrayDeque<>();
//
//    private enum IntakeState {
//        WAIT_CS1,
//        WAIT_CS2,
//        WAIT_CLEAR_CS1
//    }

//    private IntakeState intakeState = IntakeState.WAIT_CS1;
//    private BolaTipo pendingBall = BolaTipo.UNKNOWN;
//    private long intakeStateStartedAt;
//    private static final long SENSOR_TIMEOUT_MS = 1500;
//
//    private enum ShotState {
//        IDLE,
//        POSITIONING,
//        RTS,
//        WAITING_FOR_CONFIRMATION
//    }
//
//    private ShotState shotState = ShotState.IDLE;
//    private boolean shooterSensorWasActive = false;

//    private void updateIntakeQueue() {
//        BolaTipo tipo1 = classificarSensor(robot.CS1);
//        BolaTipo tipo2 = classificarSensor(robot.CS2);
//
//        switch (intakeState) {
//            case WAIT_CS1:
//                if (tipo1 != BolaTipo.UNKNOWN) {
//                    pendingBall = tipo1;
//                    intakeStateStartedAt = System.currentTimeMillis();
//                    intakeState = intakeState.WAIT_CS2;
//                }
//                break;
//            case WAIT_CS2:
//                if (tipo2 == pendingBall) {
//                    shootingQueue.offerLast(pendingBall);
//
//                    opMode.telemetry.addData(
//                            "Added to queue", pendingBall
//                    );
//
//                    pendingBall = BolaTipo.UNKNOWN;
//                    intakeState = IntakeState.WAIT_CLEAR_CS1;
//                } else if (System.currentTimeMillis()
//                        - intakeStateStartedAt > SENSOR_TIMEOUT_MS) {
//                    pendingBall = BolaTipo.UNKNOWN;
//                    intakeState = IntakeState.WAIT_CS1;
//                }
//                break;
//            case WAIT_CLEAR_CS1:
//                if (tipo1 == BolaTipo.UNKNOWN) {
//                    intakeState = IntakeState.WAIT_CS1;
//                }
//                break;
//        }
//        opMode.telemetry.addData("CS1 type", tipo1);
//        opMode.telemetry.addData("CS2 type", tipo2);
//    }

//    private void updateShootingFSM() {
//        boolean shooterActive = isShooterSensorActive();
//        boolean shooterRisingEdge = shooterActive && !shooterSensorWasActive;
//        shooterSensorWasActive = shooterActive;
//        switch (shotState) {
//            case IDLE:
//                if (!shootingQueue.isEmpty()) {
//                    shotState = ShotState.POSITIONING;
//                }
//                break;
//
//            case POSITIONING: {
//                BolaTipo next = shootingQueue.peekFirst();
//
//                if (next == BolaTipo.NECTAR) {
//                    NectarPosition();
//                    shotState = ShotState.RTS;
//                } else if (next == BolaTipo.POLLEN) {
//                    PolenPosition();
//                    shotState = ShotState.RTS;
//                } else {
//                    shotState = ShotState.IDLE;
//                }
//                break;
//            }
//
//            case RTS:
//                if (opMode.gamepad1.y) {
//                    shotState = ShotState.WAITING_FOR_CONFIRMATION;
//                }
//                break;
//
//            case WAITING_FOR_CONFIRMATION:
//                if (shooterRisingEdge && !shootingQueue.isEmpty()) {
//                    BolaTipo confirmed = shootingQueue.removeFirst();
//                    opMode.telemetry.addData("Shot confirmed", confirmed);
//                    shotState = ShotState.IDLE;
//                }
//                break;
//        }
//
//        opMode.telemetry.addData("Next piece", shootingQueue.peekFirst());
//    }

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
//        updateIntakeQueue();
//        updateShootingFSM();

//        opMode.telemetry.addData("Queue", shootingQueue);
//        opMode.telemetry.addData("Intake state", intakeState);
//        opMode.telemetry.addData("Shot state", shotState);
//        opMode.telemetry.update();
    }
//    private boolean isShooterSensorActive() {
//        return classificarSensor(robot.CS3) != BolaTipo.UNKNOWN;
//    }

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

        double distance = robot.DSD.getDistance(DistanceUnit.CM);
        opMode.telemetry.addData("Distance:", "%.2f", distance);

        if (opMode.gamepad1.x) {
            robot.Intakes.setPower(-1);
            robot.CS1.enableLed(true);
        } else if (opMode.gamepad1.y) {
            robot.Intakes.setPower(1);
            robot.CS1.enableLed(true);
        } else {
            robot.Intakes.setPower(0);
            robot.CS1.enableLed(false);
        }

        if (opMode.gamepad1.aWasPressed()) {
            robot.Flower = !robot.Flower;
        }

        if (robot.Flower && distance < 60) {
            FlowerOn();
        } else {
            FlowerOff();
        }

    }

    private void controlarServos() {

        if (opMode.gamepad1.rightBumperWasPressed()) {
            robot.alternarExtensao = !robot.alternarExtensao;
        }

        if (!robot.alternarExtensao) {
            PolenPosition();
        } else {
            NectarPosition();
        }
    }
//    public boolean isRed(ColorSensor sensor) {
//        int r = sensor.red();
//        int g = sensor.green();
//        int b = sensor.blue();
//
//        return (r > g * 1.5 && r > b * 1.5);
//    }
//    public boolean isBlue(ColorSensor sensor) {
//        int r = sensor.red();
//        int g = sensor.green();
//        int b = sensor.blue();
//
//        return (b > r * 1.5 && b > g * 1.5);
//    }
//
//    public boolean isYellow(ColorSensor sensor) {
//        int r = sensor.red();
//        int g = sensor.green();
//        int b = sensor.blue();
//
//        return (r > b * 1.5 && g > b * 1.5);
//    }
//
//
//     private BolaTipo classificarSensor(ColorSensor sensor) {
//        if (isRed(sensor) || isBlue(sensor)) {
//            return BolaTipo.NECTAR;
//        }
//        if (isYellow(sensor)) {
//            return BolaTipo.POLLEN;
//        }
//
//        return BolaTipo.UNKNOWN;
//     }

    public void PolenPosition() {

        robot.ExtensorDireito.setPosition(0.65);
        robot.ExtensorEsquerdo.setPosition(0.35);
        robot.shooterMode = true;

    }
    public void NectarPosition() {

        robot.ExtensorDireito.setPosition(0.85);
        robot.ExtensorEsquerdo.setPosition(0.15);
        robot.shooterMode = false;

    }
    public void FlowerOff() {
        robot.Lily.setPosition(0.0);
        robot.Violet.setPosition(1.0);

    }
    public void FlowerOn() {
        robot.Lily.setPosition(0.5);
        robot.Violet.setPosition(0.0);

    }

    private void liberarShooter() { robot.Trava.setPosition(0.4); }
    private void travarShooter() { robot.Trava.setPosition(0.2); }
}
