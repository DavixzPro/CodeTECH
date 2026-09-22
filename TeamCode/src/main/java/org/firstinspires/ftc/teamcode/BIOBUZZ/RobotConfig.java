package org.firstinspires.ftc.teamcode.BIOBUZZ;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public abstract class RobotConfig extends LinearOpMode {
    protected DcMotor FL;
    protected DcMotor FR;
    protected DcMotor BL;
    protected DcMotor BR;
    protected DcMotorEx Shooter;
    protected DcMotor Intakes;
    protected Servo Trava;
    protected Servo ServoShooter;
    protected GoBildaPinpointDriver pinpoint;
    protected DistanceSensor SensorDistancia;

    double distancia = SensorDistancia.getDistance(DistanceUnit.CM);

    protected static final double TOLERANCIA_DISTANCIA_CM = 0.5;
    protected static final double TOLERANCIA_ANGULO_GRAUS = 1.0;

    private boolean ultimoTriggerShooter = false;
    private boolean shooterLigado = false;


    //====================== CONFIGURAÇÃO DO ROBÔ ======================//
    void ConfigureRobot() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Trava = hardwareMap.get(Servo.class, "Trava");
        ServoShooter = hardwareMap.get(Servo.class, "ServoShooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        SensorDistancia = hardwareMap.get(DistanceSensor.class, "SensorDistancia");

        //Motores
        FL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BL.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.FORWARD);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Pinpoint
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setOffsets(0, 50, DistanceUnit.MM);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.setPosition(
                new Pose2D(DistanceUnit.CM, 0, 5, AngleUnit.DEGREES, 0));
        pinpoint.update();

        telemetry.addLine("=== Modo Autônomo ===");
        telemetry.addLine("");
        telemetry.addLine("Pronto para inicializar");
        telemetry.update();
    }
}