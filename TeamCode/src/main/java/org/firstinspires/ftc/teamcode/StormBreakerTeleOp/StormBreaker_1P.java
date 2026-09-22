package org.firstinspires.ftc.teamcode.StormBreakerTeleOp;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="StormBreaker_1P")
public class StormBreaker_1P extends OpMode {

    DcMotor BL;
    DcMotor BR;
    DcMotor FL;
    DcMotor FR;
    //Servo GiroE;
    //Servo GiroD;
    Servo Angulo;
    DcMotor HelpWheels;
    //DcMotor ShooterE;
    DcMotorEx ShooterD;
    DcMotor Intakes;
     private VoltageSensor Battery;

    // ESTADOS DO INTAKE
    private boolean ultimoTrigger = false;
    private boolean intakeRodando = false;
    private long inicioIntake = 0;

    // ESTADOS DO SHOOTER
    private boolean ultimoTriggerShooter = false;
    private boolean shooterLigado = false;

    // IMU
    private IMU imu;

    // CONTROLE DE ROTAÇÃO
    private double velocidadeRotacaoAlvo = 0;

    @Override
    public void init() {

        // HARDWARE
        BL = hardwareMap.get(DcMotor.class, "BL");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        FR = hardwareMap.get(DcMotor.class, "FR");

        ShooterD = hardwareMap.get(DcMotorEx.class, "ShooterD");

        //GiroE = hardwareMap.get(Servo.class, "GiroE");
        //GiroD = hardwareMap.get(Servo.class, "GiroD");
        Angulo = hardwareMap.get(Servo.class, "Angulo");

        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        HelpWheels = hardwareMap.get(DcMotor.class, "HelpWheels");

        Battery = hardwareMap.voltageSensor.iterator().next();

        // DRIVER HUB:
            // CONTROL HUB:
                // Motor Intakes = 0
                // Motor ShooterD = 1
                // Motor BR = 2
                // Motor FR = 3

                // Servo Angulo = 0
                // Servo GiroD = 1

            // EXPANSION HUB:
                // Motor FL = 0
                // Motor BL = 2
                // Motor HelpWheels = 3

                // Servo GiroE = 0

        telemetry.addLine("CONTROL:");
        telemetry.addLine("Motor Intakes = 0");
        telemetry.addLine("Motor ShooterD = 1");
        telemetry.addLine("Motor BR = 2");
        telemetry.addLine("Motor FR = 3");
        telemetry.addLine("Servo Angulo = 0");
        telemetry.addLine("Servo GiroD = 1");

        telemetry.addLine("EXPANSION:");
        telemetry.addLine("Motor FL = 0");
        telemetry.addLine("Motor BL = 2");
        telemetry.addLine("Motor HelpWheels = 3");
        telemetry.addLine("Servo GiroE = 0");
        telemetry.update();

        // IMU
        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );

        imu.initialize(parameters);
        imu.resetYaw();

        // INTAKE CONFIGS
        Intakes.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Intakes.setDirection(DcMotorSimple.Direction.REVERSE);
        HelpWheels.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        HelpWheels.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        HelpWheels.setDirection(DcMotorSimple.Direction.REVERSE);

        // DIREÇÃO
        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);

        // SHOOTER CONFIGS
        ShooterD.setDirection(DcMotorSimple.Direction.FORWARD);
        ShooterD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ShooterD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {

        //RESET DA IMU
        if (gamepad1.start) {
            imu.resetYaw();
        }

        //MOVIMENTAÇÃO
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        // Deadzone
        if (Math.abs(x) < 0.05) x = 0;
        if (Math.abs(y) < 0.05) y = 0;

        // ROTAÇÃO COM IMU
        double rx = 0;

        if (gamepad1.left_bumper) {
            velocidadeRotacaoAlvo = -0.8;
        } else if (gamepad1.right_bumper) {
            velocidadeRotacaoAlvo = 0.8;
        } else {
            velocidadeRotacaoAlvo = 0;
        }

        double velocidadeAngular =
                imu.getRobotAngularVelocity(AngleUnit.RADIANS).zRotationRate;
        double erroRotacao =
                velocidadeRotacaoAlvo - velocidadeAngular;
        double ganhoRotacao = 0.35;

        rx = velocidadeRotacaoAlvo + (erroRotacao * ganhoRotacao);

        // Limita a potência de rotação
        rx = Math.max(-1, Math.min(1, rx));

        // Se não estiver apertando nenhum bumper,
        // não deixa a IMU gerar rotação sozinha.
        if (velocidadeRotacaoAlvo == 0) {
            rx = 0;
        }

        //MECANUM
        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;

        // Normalização
        double max = Math.max(Math.abs(fl), Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br))));

        if (max > 1) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        FL.setPower(fl);
        FR.setPower(fr);
        BL.setPower(bl);
        BR.setPower(br);

        // INTAKES
        if (gamepad1.a) {
            HelpWheels.setPower(-0.9);
        } else if (gamepad1.b) {
            HelpWheels.setPower(0.9);
        } else {
            HelpWheels.setPower(0);
        }

        if (!intakeRodando) {

            if (gamepad1.x) {
                Intakes.setPower(1);
            } else if (gamepad1.y) {
                Intakes.setPower(-1);
            } else {
                Intakes.setPower(0);
            }
        }

        // SERVOS DE AJUSTE
        if (gamepad1.right_stick_y < -0.5) {
            Angulo.setPosition(1);
        } else if (gamepad1.right_stick_y > 0.5) {
            Angulo.setPosition(0.1);
        } else {
            Angulo.setPosition(0.5);
        }

        //if (gamepad1.right_stick_x < -0.5) {
            //GiroE.setPosition(1);
            //GiroD.setPosition(1);
        //} else if (gamepad1.right_stick_x > 0.5) {
            //GiroE.setPosition(0.1);
            //GiroD.setPosition(0.1);
        //}

        //SHOOTER
        boolean triggerAtual =
                gamepad1.right_trigger > 0.5;
        if (triggerAtual && !ultimoTriggerShooter) {
            shooterLigado = !shooterLigado;
            if (shooterLigado) {
                ShooterD.setPower(0.8);
            } else {
                ShooterD.setPower(0);
            }
        }

        ultimoTriggerShooter = triggerAtual;

        //BOTÃO MÁGICO
        boolean triggerAtualEsquerdo =
                gamepad1.left_trigger > 0.5;
        if (triggerAtualEsquerdo && !ultimoTrigger && !intakeRodando) {
            intakeRodando = true;
            inicioIntake = System.currentTimeMillis();
        }
        ultimoTrigger = triggerAtualEsquerdo;

        if (intakeRodando) {
            long tempo = System.currentTimeMillis() - inicioIntake;
            if (tempo < 100) {
                Intakes.setPower(-1);
                HelpWheels.setPower(1);
            } else if (tempo < 500) {
                Intakes.setPower(1);
                HelpWheels.setPower(-1);
            } else {
                Intakes.setPower(0);
                HelpWheels.setPower(0);
                intakeRodando = false;
            }
        }

        double voltagem = Battery.getVoltage();
        double rpm = ShooterD.getVelocity() / 28.0 * 60.0;

        telemetry.addData("Bateria", "%.2f V", voltagem);
        telemetry.addData("Shooter RPM", "%.0f", rpm);

        telemetry.update();
    }
}