package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import com.qualcomm.hardware.limelightvision.Limelight3A;

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
    protected Limelight3A limelight;

    protected double kP = 0.030;

    protected static final double TOLERANCIA_CM = 1.0;
    protected static final double TOLERANCIA_GRAUS = 1.0;

    double F = 16.7;
    double P = 240.0;
    double polenVelocity = 1300;
    //double nectarVelocity = 1500;
    boolean seguirAprilTag;


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

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

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
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        pinpoint.setOffsets(165, 40, DistanceUnit.MM);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
        pinpoint.update();
    }

    void setTelemetry() {
        telemetry.addLine("=== Beefrost AUTO ===");
        telemetry.addLine("");
        telemetry.addLine("Componentes configurados");
        telemetry.addLine("");
        telemetry.addData("Y", "%.2f cm", pinpoint.getPosX(DistanceUnit.CM));
        telemetry.addData("X", "%.2f cm", pinpoint.getPosY(DistanceUnit.CM));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();
    }
}