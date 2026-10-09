package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public abstract class RobotConfig extends LinearOpMode {

    protected DcMotor FL;
    protected DcMotor FR;
    protected DcMotor BL;
    protected DcMotor BR;
    protected DcMotorEx Shooter;
    protected DcMotor Intakes;
    protected Servo Trava;
    protected Servo ExtensorEsquerdo;
    protected Servo ExtensorDireito;
    protected GoBildaPinpointDriver pinpoint;
    protected DistanceSensor SensorDistancia;

    protected static final double TOLERANCIA_CM = 1.0;
    protected static final double TOLERANCIA_GRAUS = 1.0;

    double F = 17.75;
    double P = 240.0;
    double targetVelocity = 1350;


    //====================== CONFIGURAÇÃO DO ROBÔ ======================//
    void ConfigureRobot() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");

        Trava = hardwareMap.get(Servo.class, "Trava");
        ExtensorEsquerdo = hardwareMap.get(Servo.class, "ExtensorEsquerdo");
        ExtensorDireito = hardwareMap.get(Servo.class, "ExtensorDireito");

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        SensorDistancia = hardwareMap.get(DistanceSensor.class, "SensorDistancia");

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0 , F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setDirection(DcMotor.Direction.REVERSE);

        //Motores
        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Pinpoint
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setOffsets(165, 50, DistanceUnit.MM);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
        pinpoint.update();
    }

    void setTelemetry() {
        telemetry.addLine("=== Beefrost AUTO ===");
        telemetry.addLine("");
        telemetry.addLine("Pinpoint configurado");
        telemetry.addLine("Motores configurados");
        telemetry.addLine("");
        telemetry.addData("Y", "%.2f in", pinpoint.getPosX(DistanceUnit.INCH));
        telemetry.addData("X", "%.2f in", pinpoint.getPosY(DistanceUnit.INCH));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();
    }
}