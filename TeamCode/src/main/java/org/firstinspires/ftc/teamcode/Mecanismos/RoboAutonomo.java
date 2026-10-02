package org.firstinspires.ftc.teamcode.Mecanismos;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Autonomous(name = "AutonomoCurso")
public class RoboAutonomo extends OpMode {

    DcMotor frenteEsquerda;
    DcMotor frenteDireita;
    DcMotor trasEsquerda;
    DcMotor trasDireita;

    @Override
    public void init() {
        frenteEsquerda = hardwareMap.get(DcMotor.class, "frenteEsquerda");
        frenteDireita = hardwareMap.get(DcMotor.class, "frenteDireita");
        trasEsquerda = hardwareMap.get(DcMotor.class, "trasEsquerda");
        trasDireita = hardwareMap.get(DcMotor.class, "trasDireita");

        frenteDireita.setDirection(DcMotor.Direction.REVERSE);
        trasDireita.setDirection(DcMotor.Direction.REVERSE);
    }

    public void loop() {

        frenteEsquerda.setPower(0.7);
        trasEsquerda.setPower(0);
        frenteDireita.setPower(0);
        trasDireita.setPower(0.7);
        sleep(1000);
        frenteEsquerda.setPower(0);
        trasEsquerda.setPower(-0.7);
        frenteDireita.setPower(-0.7);
        trasDireita.setPower(0);
        sleep(1000);
        frenteEsquerda.setPower(-0.7);
        trasEsquerda.setPower(0.7);
        frenteDireita.setPower(0.7);
        trasDireita.setPower(-0.7);
        sleep(1000);
        frenteEsquerda.setPower(0);
        trasEsquerda.setPower(0);
        frenteDireita.setPower(0);
        trasDireita.setPower(0);
        sleep(10000);

    }

}