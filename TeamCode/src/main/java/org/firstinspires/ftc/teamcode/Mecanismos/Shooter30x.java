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
    public double highVelocity = 1500;
    public double lowVelocit = 900;
    double curTargetVelocity = highVelocity;
    double[] stepSizes = {100.0, 10.0, 1.0, 0.1, 0.01};
    int stepIndex = 1;


    double F = 0;
    double P = 0;


    @Override
    public void init() {
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("init completo");


    }

    @Override
    public void loop() {
        if (gamepad1.yWasPressed()) {
            if (curTargetVelocity == highVelocity) ;
            curTargetVelocity = lowVelocit;
        } else {
            curTargetVelocity = highVelocity;
        }

        if (gamepad1.bWasPressed()) {
            stepIndex =(stepIndex + 1) % stepSizes.length;
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
        //=====seta o novo coeficiente PIDF=====//
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0 , F);
        Shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        //seta velocidade
        Shooter.setVelocity(curTargetVelocity);

        double curVelocity = Shooter.getVelocity();
        double error = curTargetVelocity - curVelocity;

        telemetry.addData("targetVelocity ", curTargetVelocity);
        telemetry.addData("current Velociy ", "%.2f", curVelocity);
        telemetry.addData("Error, %.2f", error);
        telemetry.addLine("----------------");
        telemetry.addData("Tuning P",  "%.4f (D-Pad U/D)", P);
        telemetry.addData("Tunig F", "%.4f (D-pad L/R)", F);
        telemetry.addData("Step Sizes, %.4f (B button)", stepSizes[stepIndex]);
    }
}
