package org.firstinspires.ftc.teamcode.BIOBUZZ;

import android.hardware.Sensor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "CT04B2")
public class CT04B2 extends OpMode {

    DcMotorEx Shooter;
    Servo ExtensorEsquerdo;
    Servo ExtensorDireito;
    DcMotor FL, BL, FR, BR;
    DistanceSensor SensorDistancia;

    MaquinaEstado shooterEstado = new MaquinaEstado();

    //TOGGLE
    public class MaquinaEstado {
        private boolean isOn = false;

        public boolean getIsOn() {
            return isOn;
        }
        public void setIsOn(boolean isOn) {
            this.isOn = isOn;
        }

        public void toggleShooter() {
            setIsOn(!getIsOn());
        }
    }


    @Override
    public void init() {
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        ExtensorDireito = hardwareMap.get(Servo.class, "ExtensorDireito");
        ExtensorEsquerdo = hardwareMap.get(Servo.class, "ExtensorEsquerdo");
        SensorDistancia = hardwareMap.get(DistanceSensor.class, "SensorDistancia");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BR = hardwareMap.get(DcMotor.class, "BR");

        FL.setDirection(DcMotor.Direction.FORWARD);
        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.REVERSE);

        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Shooter.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        /////////////////////////////////Mecanum///////////////////////////////

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        if (Math.abs(x) < 0.05) {
            x = 0;
        }
        if (Math.abs(y) < 0.05) {
            y = 0;
        }

        /////////////////////////////////Giro///////////////////////////////
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

        ///////////////////////////////////Servos/////////////////////////////////

        if (gamepad1.dpad_left) {
            PolenPosition();
        }
        if (gamepad1.dpad_right) {
            NectarPosition();
        }


        ////////////////////////////////// Shooter //////////////////////////////////

        if (gamepad1.bWasPressed()) {
            shooterEstado.toggleShooter();
        }

        double distance = SensorDistancia.getDistance(DistanceUnit.CM);

        double potenciaShooter = 0;

        if (shooterEstado.getIsOn()) {

            if (distance <= 21) {
                potenciaShooter = 0.60;

            } else if (distance >= 33) {
                potenciaShooter = 0.45;

            } else {
                potenciaShooter =
                        0.45 + ((33.0 - distance) * (0.15 / 12.0));
            }

            Shooter.setPower(potenciaShooter);

        } else {
            Shooter.setPower(0);
        }
    }

    public void PolenPosition() {
        ExtensorDireito.setPosition(0.5);
        ExtensorEsquerdo.setPosition(0.5);
    }

    public void NectarPosition() {
        ExtensorDireito.setPosition(0.2);
        ExtensorEsquerdo.setPosition(0.8);
    }

}
