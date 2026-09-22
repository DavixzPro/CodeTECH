package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MacanumJ {

    private DcMotor CimaEsquerdo;
    private DcMotor CimaDireito;
    private DcMotor EsquerdaBaixa;
    private DcMotor DireitaBaixa;

    private IMU imu;

    public void init(HardwareMap hwMap) {

        CimaEsquerdo = hwMap.get(DcMotor.class, "CimaEsquerdo");
        CimaDireito = hwMap.get(DcMotor.class, "CimaDireito");
        EsquerdaBaixa = hwMap.get(DcMotor.class, "EsquerdaBaixa");
        DireitaBaixa = hwMap.get(DcMotor.class, "DireitaBaixa");

        CimaEsquerdo.setDirection(DcMotor.Direction.REVERSE);
        EsquerdaBaixa.setDirection(DcMotor.Direction.REVERSE);

        CimaEsquerdo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        CimaDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        EsquerdaBaixa.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DireitaBaixa.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        CimaEsquerdo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        CimaDireito.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        EsquerdaBaixa.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        DireitaBaixa.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = hwMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        );

        imu.initialize(parameters);
    }

    public void mover(double forward, double strafe, double rotate) {

        double frontLeft = forward + strafe + rotate;
        double backLeft = forward - strafe + rotate;
        double frontRight = forward - strafe - rotate;
        double backRight = forward + strafe - rotate;

        double max = Math.max(
                1.0,
                Math.max(
                        Math.abs(frontLeft),
                        Math.max(
                                Math.abs(backLeft),
                                Math.max(
                                        Math.abs(frontRight),
                                        Math.abs(backRight)
                                )
                        )
                )
        );

        CimaEsquerdo.setPower(frontLeft / max);
        EsquerdaBaixa.setPower(backLeft / max);
        CimaDireito.setPower(frontRight / max);
        DireitaBaixa.setPower(backRight / max);
    }

    public void driveFieldRelative(double forward, double strafe, double rotate) {

        double heading =
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = strafe * Math.cos(-heading) - forward * Math.sin(-heading);
        double rotY = strafe * Math.sin(-heading) + forward * Math.cos(-heading);

        mover(rotY, rotX, rotate);
    }

    public void resetHeading() {
        imu.resetYaw();
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void stop() {
        mover(0, 0, 0);
    }
}