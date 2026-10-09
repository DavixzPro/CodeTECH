package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp
public class Shooter30x extends OpMode {

    public DcMotorEx Shooter;
    public DcMotor Intakes;
    public double highVelocity = 1350;
    public double lowVelocity = 750;
    double curTargetVelocity = highVelocity;
    double F = 17.75;
    double P = 240.0;
    double[] stepSizes = {100.0, 10.0, 1.0, 0.1, 0.01};
    int stepIndex = 1;


    @Override
    public void init() {
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        Intakes.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Intakes.setDirection(DcMotor.Direction.REVERSE);
        telemetry.addLine("init completo");
    }

    @Override
    public void loop() {
        if (gamepad1.yWasPressed()) {
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            } else {
                curTargetVelocity = highVelocity;
            }
        }

        if (gamepad1.x) {
            Intakes.setPower(1);
        } else if (gamepad1.a) {
            Intakes.setPower(-1);
        } else {
            Intakes.setPower(0);
        }

        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad1.dpadDownWasPressed()) {
            P -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadUpWasPressed()) {
            P += stepSizes[stepIndex];
        }
        if (gamepad1.dpadLeftWasPressed()) {
            F += stepSizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()) {
            F -= stepSizes[stepIndex];
        }


        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0 , F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        //seta velocidade
        Shooter.setVelocity(curTargetVelocity);

        double curVelocity = Shooter.getVelocity();
        double error = Math.abs(curTargetVelocity - curVelocity);

        telemetry.addData("targetVelocity ", curTargetVelocity);
        telemetry.addData("current Velociy ", "%.2f", curVelocity);
        telemetry.addData("Error ", "%.2f", error);
        telemetry.addLine("----------------");
        telemetry.addData("Tuning P",  "%.4f (D-Pad U/D)", P);
        telemetry.addData("Tuning F", "%.4f (D-pad L/R)", Math.abs(F));
        telemetry.addData("Step Sizes, %.4f (B button)", stepSizes[stepIndex]);
    }
}
