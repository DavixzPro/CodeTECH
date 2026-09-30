package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ROBO")
public class Beefrost_TeleOp extends OpMode {

    DcMotor Intakes;
    DcMotorEx Shooter;

    @Override
    public void init() {
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");

        Intakes.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        if (gamepad1.b) {
            Shooter.setPower(1);
        } else {
            Shooter.setPower(0);
        }

        if (gamepad1.x) {
            Intakes.setPower(1);
        } else if (gamepad1.y) {
            Intakes.setPower(-1);
        } else {
            Intakes.setPower(0);
        }
    }

}
