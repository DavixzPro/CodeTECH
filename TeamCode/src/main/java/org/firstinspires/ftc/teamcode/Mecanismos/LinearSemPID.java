package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Linear Simples", group = "Mecanismos")
public class LinearSemPID extends LinearOpMode {

    private DcMotor linear;

    final int BAIXO = 0;
    final int MEDIO = -1128;
    final int ALTO = 2255;

    @Override
    public void runOpMode() {
        linear = hardwareMap.get(DcMotor.class, "linear");

        linear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linear.setTargetPosition(BAIXO);
        linear.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.y) {
                linear.setTargetPosition(ALTO);
            }
            else if (gamepad1.x) {
                linear.setTargetPosition(MEDIO);
            }
            else if (gamepad1.a) {
                linear.setTargetPosition(BAIXO);
            }

            linear.setPower(0.8);

            telemetry.addData("Posição", linear.getCurrentPosition());
            telemetry.addData("Alvo", linear.getTargetPosition());
            telemetry.addData("Está movendo?", linear.isBusy());
            telemetry.update();
        }
    }
}