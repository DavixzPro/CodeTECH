package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.internal.opengl.shaders.ShaderHelper;

@TeleOp(name = "BeefrostTeleOp")
public class BeefrostTeleOp extends OpMode {

    private DcMotor FL, FR, BL, BR;
    private DcMotorEx Shooter;
    private DcMotor Intakes;

    private Servo ExtensorEsquerdo, ExtensorDireito;

    private VoltageSensor Battery;
    private Limelight3A limelight;

    double F = 16.7;
    double P = 240.0;
    boolean shooterLigado;
    double polenVelocity = 1300;
    double nectarVelocity = 1500;
    boolean shooterMode = true;

    private boolean seguirAprilTag = false;

    private double kP = 0.030;

    @Override
    public void init() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        ExtensorEsquerdo = hardwareMap.get(Servo.class, "ExtensorEsquerdo");
        ExtensorDireito = hardwareMap.get(Servo.class, "ExtensorDireito");
        Battery = hardwareMap.voltageSensor.iterator().next();

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0 , F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);

        telemetry.addLine("BEEFROST iniciado");
        telemetry.update();

        PolenPosition();
    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {
        controlarMovimento();
        controlarIntake();
        controlarShooter();
        controlarExtensores();
        atualizarLimelight();

        //tags
        telemetry.addData("AprilTag Assist", seguirAprilTag ? "ATIVO" : "DESATIVADO");
        telemetry.addData("Bateria", "%.2f V", Battery.getVoltage());
        telemetry.update();
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
            Intakes.setPower(-1);
        } else if (gamepad1.y) {
            Intakes.setPower(1);
        } else {
            Intakes.setPower(0);
        }
    }

    private void controlarShooter() {

        if (gamepad1.bWasPressed()) {
            shooterLigado = !shooterLigado;
            seguirAprilTag = !seguirAprilTag;
        }
        if (shooterLigado) {
            if (shooterMode) {
                F = 16.7;
                Shooter.setVelocity(polenVelocity);
            } else {
                F = 18.0;
                Shooter.setVelocity(nectarVelocity);
            }
        } else {
            Shooter.setVelocity(0);
        }
    }

    private void controlarExtensores() {

        if (gamepad1.dpad_left) {
            PolenPosition();
        }

        if (gamepad1.dpad_right) {
            NectarPosition();
        }
    }

    private void NectarPosition() {
        ExtensorDireito.setPosition(0.45);
        ExtensorEsquerdo.setPosition(0.20);
        shooterMode = false;
    }

    private void PolenPosition() {
        ExtensorDireito.setPosition(0.20);
        ExtensorEsquerdo.setPosition(0.70);
        shooterMode = true;
    }

    private void atualizarLimelight() {

        LLResult resultado = limelight.getLatestResult();

        if (resultado != null) {

            telemetry.addData("Limelight", resultado.isValid() ? "Detectando" : "Sem alvo");

            if (resultado.isValid()) {

                telemetry.addData("TX", "%.2f", resultado.getTx());
                telemetry.addData("TY", "%.2f", resultado.getTy());
                telemetry.addData("TA", "%.2f", resultado.getTa());

                if (!resultado.getFiducialResults().isEmpty()) {

                    for (LLResultTypes.FiducialResult tag :
                            resultado.getFiducialResults()) {
                        telemetry.addData("AprilTag ID", tag.getFiducialId());
                    }

                } else {
                    telemetry.addData("AprilTag ID", "Nenhuma");
                }
            }
        }
    }

    @Override
    public void stop() {
        limelight.stop();
    }
}