package org.firstinspires.ftc.teamcode.BeefrostTeleOpRED;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class BeefrostMovement_RED {

    private final BeefrostRedConfig robot;
    private final OpMode opMode;

    private LLResult resultadoLimelight;

    public BeefrostMovement_RED(
            BeefrostRedConfig robot,
            OpMode opMode
    ) {
        this.robot = robot;
        this.opMode = opMode;
    }

    public void iniciarLimelight() {
        robot.limelight.start();
    }

    public void atualizar() {

        resultadoLimelight = robot.limelight.getLatestResult();

        double y = -opMode.gamepad1.left_stick_y;
        double x = opMode.gamepad1.left_stick_x;
        double rx = 0;

        boolean tagDetectada =
                resultadoLimelight != null
                        && resultadoLimelight.isValid()
                        && !resultadoLimelight
                        .getFiducialResults()
                        .isEmpty();

        if (robot.seguirAprilTag && tagDetectada) {

            double tx = resultadoLimelight.getTx();
            rx = tx * robot.kP;
            rx = Math.max(-1, Math.min(1, rx));

        } else {

            if (opMode.gamepad1.left_trigger > 0.05) {
                rx = -opMode.gamepad1.left_trigger;
            }

            if (opMode.gamepad1.right_trigger > 0.05) {
                rx = opMode.gamepad1.right_trigger;
            }
        }

        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;

        double max = Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br)));

        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        robot.FL.setPower(fl);
        robot.FR.setPower(fr);
        robot.BL.setPower(bl);
        robot.BR.setPower(br);
    }


    public LLResult getResultadoLimelight() {
        return resultadoLimelight;
    }

    public boolean isTagDetectada() {
        return resultadoLimelight != null
                && resultadoLimelight.isValid() && !resultadoLimelight
                        .getFiducialResults().isEmpty();
    }


    public void parar() {

        robot.limelight.stop();
        robot.FL.setPower(0);
        robot.FR.setPower(0);
        robot.BL.setPower(0);
        robot.BR.setPower(0);

    }
}

