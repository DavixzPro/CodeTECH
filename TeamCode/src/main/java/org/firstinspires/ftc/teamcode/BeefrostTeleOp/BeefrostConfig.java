package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BeefrostConfig {

    public DcMotor FL, FR, BL, BR;
    public DcMotorEx Shooter;
    public DcMotor Intakes;

    public Servo ExtensorEsquerdo;
    public Servo ExtensorDireito;
    public Servo Trava;
    public Servo Violet;
    public Servo Lily;

    public VoltageSensor Battery;
    public Limelight3A limelight;
    public ColorSensor CS1;
    public ColorSensor CS2;
    public ColorSensor CS3;

    public final double P = 246.3;
    public final double F = 15.42;
    public final double polenVelocity = 1230;
    public final double nectarVelocity = 1300;
    public boolean alternarExtensao = false;

    public final double kP = 0.030;

    public boolean shooterLigado = false;
    public boolean shooterMode = true;
    public boolean shooterTravado = false;

    public boolean seguirAprilTag = false;
    public boolean Flower = false;

    public void init(HardwareMap hardwareMap) {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");

        ExtensorEsquerdo = hardwareMap.get(Servo.class, "ExtensorEsquerdo");
        ExtensorDireito = hardwareMap.get(Servo.class, "ExtensorDireito");
        Trava = hardwareMap.get(Servo.class, "Trava");
        Violet = hardwareMap.get(Servo.class,"Violet");
        Lily = hardwareMap.get(Servo.class,"Lily");

        Battery = hardwareMap.voltageSensor.iterator().next();
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        CS1 = hardwareMap.get(ColorSensor.class, "SensorCor1");
        CS2 = hardwareMap.get(ColorSensor.class, "SensorCor2");
        CS3 = hardwareMap.get(ColorSensor.class, "SensorCor3");

        FL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        PIDFCoefficients pidf = new PIDFCoefficients(P, 0, 0, F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        limelight.pipelineSwitch(0);
       }
}
