package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "BeefrostDeVictor")
public class BeefrostDeVictor extends OpMode {

    private DcMotor FL, FR, BL, BR;
    private DcMotorEx Shooter;
    private DcMotor Intakes;

    private Servo ExtensorEsquerdo, ExtensorDireito;

    private VoltageSensor Battery;
    private GoBildaPinpointDriver pinpoint;
    private Limelight3A limelight;

    private boolean shooterLigado = false;
    private boolean ultimoB = false;

    private boolean seguirAprilTag = false;
    private boolean ultimoRightBumper = false;

    private double kP = 0.040;

    @Override
    public void init() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");

        ExtensorEsquerdo = hardwareMap.get(
                Servo.class,
                "ExtensorEsquerdo"
        );

        ExtensorDireito = hardwareMap.get(
                Servo.class,
                "ExtensorDireito"
        );

        Battery = hardwareMap.voltageSensor.iterator().next();

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "pinpoint"
        );

        pinpoint.setOffsets(
                0,
                50,
                DistanceUnit.MM
        );

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.resetPosAndIMU();

        limelight = hardwareMap.get(
                Limelight3A.class,
                "limelight"
        );

        limelight.pipelineSwitch(0);

        telemetry.addLine("BEEFROST iniciado");
        telemetry.update();
    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {

        controlarModoAprilTag();
        controlarMovimento();
        controlarIntake();
        controlarShooter();
        controlarExtensores();
        atualizarLimelight();

        telemetry.addData(
                "AprilTag Assist",
                seguirAprilTag ? "ATIVO" : "DESATIVADO"
        );

        telemetry.addData(
                "Bateria",
                "%.2f V",
                Battery.getVoltage()
        );

        telemetry.update();
    }

    private void controlarModoAprilTag() {

        if (gamepad1.right_bumper && !ultimoRightBumper) {
            seguirAprilTag = !seguirAprilTag;
        }

        ultimoRightBumper = gamepad1.right_bumper;
    }

    private void controlarMovimento() {

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        double rx = 0;

        if (seguirAprilTag) {

            LLResult resultado = limelight.getLatestResult();

            if (resultado != null &&
                    resultado.isValid() &&
                    !resultado.getFiducialResults().isEmpty()) {

                double tx = resultado.getTx();

                rx = tx * kP;

                if (rx > 1) {
                    rx = 1;
                }

                if (rx < -1) {
                    rx = -1;
                }

            } else {

                if (gamepad1.left_trigger > 0.05) {
                    rx = -gamepad1.left_trigger;
                }

                if (gamepad1.right_trigger > 0.05) {
                    rx = gamepad1.right_trigger;
                }
            }

        } else {

            if (gamepad1.left_trigger > 0.05) {
                rx = -gamepad1.left_trigger;
            }

            if (gamepad1.right_trigger > 0.05) {
                rx = gamepad1.right_trigger;
            }
        }

        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;

        double max = Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))
        );

        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        FL.setPower(fl);
        FR.setPower(fr);
        BL.setPower(bl);
        BR.setPower(br);
    }

    private void controlarIntake() {

        if (gamepad1.x) {
            Intakes.setPower(1);
        } else if (gamepad1.y) {
            Intakes.setPower(-1);
        } else {
            Intakes.setPower(0);
        }
    }

    private void controlarShooter() {

        if (gamepad1.b && !ultimoB) {
            shooterLigado = !shooterLigado;
        }

        ultimoB = gamepad1.b;

        if (shooterLigado) {
            Shooter.setVelocity(1500);
        } else {
            Shooter.setPower(0);
        }

        telemetry.addData(
                "Shooter",
                shooterLigado ? "LIGADO" : "DESLIGADO"
        );
    }

    private void controlarExtensores() {

        if (gamepad1.dpad_left) {
            PolenPosition();
        }

        if (gamepad1.dpad_right) {
            NectarPosition();
        }
    }

    private void PolenPosition() {

        ExtensorDireito.setPosition(0.45);
        ExtensorEsquerdo.setPosition(0.20);
    }

    private void NectarPosition() {

        ExtensorDireito.setPosition(0.20);
        ExtensorEsquerdo.setPosition(0.70);
    }

    private void atualizarLimelight() {

        LLResult resultado = limelight.getLatestResult();

        if (resultado != null) {

            telemetry.addData(
                    "Limelight",
                    resultado.isValid() ? "Detectando" : "Sem alvo"
            );

            if (resultado.isValid()) {

                telemetry.addData(
                        "TX",
                        "%.2f",
                        resultado.getTx()
                );

                telemetry.addData(
                        "TY",
                        "%.2f",
                        resultado.getTy()
                );

                telemetry.addData(
                        "TA",
                        "%.2f",
                        resultado.getTa()
                );

                if (!resultado.getFiducialResults().isEmpty()) {

                    for (LLResultTypes.FiducialResult tag :
                            resultado.getFiducialResults()) {

                        telemetry.addData(
                                "AprilTag ID",
                                tag.getFiducialId()
                        );
                    }

                } else {

                    telemetry.addData(
                            "AprilTag ID",
                            "Nenhuma"
                    );
                }
            }
        }
    }

    @Override
    public void stop() {
        limelight.stop();
    }
}