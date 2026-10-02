package org.firstinspires.ftc.teamcode.BeefrostTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Beefrost_TeleOp")
public class Beefrost_TeleOp extends OpMode {

    DcMotor Intakes;
    DcMotorEx Shooter;
    DcMotor FL;
    DcMotor BL;
    DcMotor FR;
    DcMotor BR;
    Servo ExtensorDireito;
    Servo Trava;

    boolean TravaAtiva = false;

    @Override
    public void init() {
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BR = hardwareMap.get(DcMotor.class, "BR");

        ExtensorDireito = hardwareMap.get(Servo.class, "ExtensorDireito");
        Trava = hardwareMap.get(Servo.class, "Trava");

        BL.setDirection(DcMotor.Direction.REVERSE);
        FL.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);

        Intakes.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Intakes.setDirection(DcMotor.Direction.REVERSE);
        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        // MECANUM + GIRO

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        if (Math.abs(x) < 0.05) {
            x = 0;
        }
        if (Math.abs(y) < 0.05) {
            y = 0;
        }

        double rx = 0;

        double potenciaEsquerda = gamepad1.left_trigger;
        double potenciaDireita = gamepad1.right_trigger;

        if (potenciaEsquerda > 0.05) {
            rx = -potenciaEsquerda;
        } else if (potenciaDireita > 0.05) {
            rx = potenciaDireita;
        }

        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;

        double max = Math.max(Math.abs(fl), Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br))));

        if (max > 1) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        FL.setPower(fl);
        FR.setPower(fr);
        BL.setPower(bl);
        BR.setPower(br);


        // SHOOTER

        if (gamepad1.b) {
            Shooter.setPower(0.65);
        } else {
            Shooter.setPower(0);
        }


        // INTAKES

        if (gamepad1.x) {
            Intakes.setPower(1);
        } else if (gamepad1.y) {
            Intakes.setPower(-1);
        } else {
            Intakes.setPower(0);
        }


        // AJUSTE DO SHOOTER

        if (gamepad1.dpadLeftWasPressed()) {
            PolenPosition();
        }
        if (gamepad1.dpadRightWasPressed()) {
            NectarPosition();
        }


        // TRAVA

        if (gamepad1.aWasPressed()) {
            TravaAtiva = !TravaAtiva;

            if (TravaAtiva) {
                Trava.setPosition(0.6);
            } else {
                Trava.setPosition(0.2);
            }
        }
    }

    public void PolenPosition() {
        ExtensorDireito.setPosition(0.45);
    }

    public void NectarPosition() {
        ExtensorDireito.setPosition(0.25);
    }

}
