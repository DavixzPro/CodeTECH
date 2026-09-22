package org.firstinspires.ftc.teamcode.CT04B1;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
//import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="CT04B1_TeleOp")
public class CT04B1_TeleOp extends OpMode {

    DcMotor BL;
    DcMotor BR;
    DcMotor FL;
    DcMotor FR;

    Servo servoX;
    Servo servoY;
    DcMotorEx Shooter;
    DcMotor Intakes;
    GoBildaPinpointDriver pinpoint;

    private double xInicial;

    private DistanceSensor SensorDeDistancia;
    private VoltageSensor Battery;

    private boolean ultimoTrigger = false;
    private boolean intakeRodando = false;
    private long inicioIntake = 0;

    private IMU imu;
    private double velocidadeRotacaoAlvo = 0;

    private boolean ultimoTriggerShooter = false;
    private boolean shooterLigado = false;


    @Override
    public void init() {

        BL = hardwareMap.get(DcMotor.class, "BL");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        FR = hardwareMap.get(DcMotor.class, "FR");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        //servo = hardwareMap.get(Servo.class, "servo");
        Battery = hardwareMap.voltageSensor.iterator().next();
        imu = hardwareMap.get(IMU.class, "imu");
        SensorDeDistancia = hardwareMap.get(DistanceSensor.class, "SensorDeDistancia");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );
        pinpoint.setOffsets(0, 50, DistanceUnit.MM);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 5, AngleUnit.DEGREES, 0));
        pinpoint.update();


        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP));

        imu.initialize(parameters);
        imu.resetYaw();

        Intakes.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Intakes.setDirection(DcMotorSimple.Direction.REVERSE);

        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.FORWARD);
        FL.setDirection(DcMotor.Direction.FORWARD);

        Shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        xInicial = pinpoint.getPosX(DistanceUnit.INCH);
    }


    @Override
    public void loop() {

       //////////////////////////////Reset da imu///////////////////////////////////

        if (gamepad1.start) {
            imu.resetYaw();
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        if (Math.abs(x) < 0.05) {
            x = 0;
        }
        if (Math.abs(y) < 0.05) {
            y = 0;
        }

        /////////////////////////////////Giro///////////////////////////////
        double rx = 0;

        double potenciaEsquerda = gamepad1.left_trigger;
        double potenciaDireita = gamepad1.right_trigger;

        if (potenciaEsquerda > 0.05) {
            rx = -potenciaEsquerda;
        } else if (potenciaDireita > 0.05) {
            rx = potenciaDireita;
        }

        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;

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


        ////////////////////////////////Intake/////////////////////////////
        if (!intakeRodando) {
            if (gamepad1.x) {
                Intakes.setPower(1);
            } else if (gamepad1.y) {
                Intakes.setPower(-1);
            } else {
                Intakes.setPower(0);
            }
        }

        boolean triggerAtualEsquerdo = gamepad1.left_bumper;

        if (triggerAtualEsquerdo && !ultimoTrigger && !intakeRodando) {
            intakeRodando = true;
            inicioIntake = System.currentTimeMillis();
        }

        ultimoTrigger = triggerAtualEsquerdo;


        /////////////////////////Botao magico////////////////////////////
        if (intakeRodando) {
            long tempo = System.currentTimeMillis() - inicioIntake;

            if (tempo < 100) {
                Intakes.setPower(-1);
            } else if (tempo < 500) {
                Intakes.setPower(1);
            } else {
                Intakes.setPower(0);
                intakeRodando = false;
            }
        }

        /////////////////////////Shooter automatico///////////////////////////////

        double distance = SensorDeDistancia.getDistance(DistanceUnit.CM);
        boolean triggerAtualShooter = gamepad1.right_bumper;

        if (triggerAtualShooter && !ultimoTriggerShooter) {
            shooterLigado = !shooterLigado;
        }

        ultimoTriggerShooter = triggerAtualShooter;

        double potenciaShooter = 0;

        if (shooterLigado) {
            if (distance <= 21) {
                potenciaShooter = 0.60;
            } else if (distance >= 33) {
                potenciaShooter = 0.45;
            } else {
                potenciaShooter = 0.45 + ((33.0 - distance) * (0.15 / 12.0));
            }
            Shooter.setPower(potenciaShooter);
        } else {
            Shooter.setPower(0);
        }

        ////////////////////////////////Servo////////////////////////////////////

        if (gamepad1.a) {
            servoX.setPosition(1.0);
            servoY.setPosition(1.0);
        }
        else if (gamepad1.b) {
            servoX.setPosition(0.1);
            servoY.setPosition(1.0);
        } else {
            servoX.setPosition(0.1);
            servoY.setPosition(1.0);
        }

        ////////////////////////////Valores da Telemetria///////////////////////////

        double voltagem = Battery.getVoltage();
        double rpm = Shooter.getVelocity() / 28.0 * 60.0;
        double xAtual = pinpoint.getPosX(DistanceUnit.INCH);
        double deltaX = xAtual - xInicial;
        double distanciaPercorrida = Math.abs(deltaX);

        telemetry.addData("Bateria", "%.2f V", voltagem);
        telemetry.addData("Shooter Potencia", "%.3f", potenciaShooter);
        telemetry.addData("Shooter RPM", "%.0f", rpm);
        telemetry.addData("Distância", "%.2f cm", distance);
        telemetry.addData("Rotação", "%.2f", rx);
        telemetry.addData("Distância percorrida X", "%.2f in", distanciaPercorrida);
        telemetry.update();
    }
}