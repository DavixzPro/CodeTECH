package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TelOperado")
public class TeleOperado extends OpMode {

    DcMotor frenteEsquerda;
    DcMotor frenteDireita;
    DcMotor trasEsquerda;
    DcMotor trasDireita;


    @Override
    public void init() {
        frenteEsquerda = hardwareMap.get(DcMotor.class, "frenteEsquerda");
        trasEsquerda = hardwareMap.get(DcMotor.class, "trasEsquerda");
        frenteDireita = hardwareMap.get(DcMotor.class, "frenteDireita");
        trasDireita = hardwareMap.get(DcMotor.class, "trasDireita");

        frenteDireita.setDirection(DcMotor.Direction.REVERSE);
        trasDireita.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void loop() {

        if (gamepad1.dpad_up) {
            frenteEsquerda.setPower(0.7);
            trasEsquerda.setPower(0.7);
            frenteDireita.setPower(0.7);
            trasDireita.setPower(0.7);
        } else if (gamepad1.dpad_down) {
            frenteEsquerda.setPower(-0.7);
            trasEsquerda.setPower(-0.7);
            frenteDireita.setPower(-0.7);
            trasDireita.setPower(-0.7);
        } else if (gamepad1.dpad_right) {
            frenteEsquerda.setPower(0.7);
            trasEsquerda.setPower(-0.7);
            frenteDireita.setPower(-0.7);
            trasDireita.setPower(0.7);

        }else if(gamepad1.dpad_left) {
            frenteEsquerda.setPower(-0.7);
            trasEsquerda.setPower(0.7);
            frenteDireita.setPower(0.7);
            trasDireita.setPower(-0.7);

        }else if(gamepad1.left_bumper) {
            frenteEsquerda.setPower(-0.7);
            trasEsquerda.setPower(-0.7);
            frenteDireita.setPower(0.7);
            trasDireita.setPower(0.7);

        }else if(gamepad1.right_bumper) {
            frenteEsquerda.setPower(0.7);
            trasEsquerda.setPower(0.7);
            frenteDireita.setPower(-0.7);
            trasDireita.setPower(-0.7);

        }else {
            frenteEsquerda.setPower(0);
            trasEsquerda.setPower(0);
            frenteDireita.setPower(0);
            trasDireita.setPower(0);
        }
    }

}
