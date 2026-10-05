package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TelOperadoJoystick")
public class TeleOperadoJoystick extends OpMode {

    DcMotor frenteEsquerda;
    DcMotor frenteDireita;
    DcMotor trasEsquerda;
    DcMotor trasDireita;


    @Override
    public void init() {
        frenteEsquerda = hardwareMap.get(DcMotor.class, "FL");
        trasEsquerda = hardwareMap.get(DcMotor.class, "BL");
        frenteDireita = hardwareMap.get(DcMotor.class, "FR");
        trasDireita = hardwareMap.get(DcMotor.class, "BR");

        frenteDireita.setDirection(DcMotor.Direction.REVERSE);
        trasDireita.setDirection(DcMotor.Direction.REVERSE);

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

        frenteEsquerda.setPower(fl);
        frenteDireita.setPower(fr);
        trasEsquerda.setPower(bl);
        trasDireita.setPower(br);
    }

}
