package org.firstinspires.ftc.teamcode.BIOBUZZ;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "RoboTeleOp")
public class RoboTeleOp extends OpMode {

    DcMotorEx Shooter;
    Servo ExtensorEsquerdo;
    Servo ExtensorDireito;

    //TOGGLE
    public class MaquinaEstado {
        private boolean isOn = false;
        private double potencia = 0.0;


        public boolean getIsOn() {
            return isOn;
        }
        public double getPotencia(double potencia) {
            return potencia;
        }
        public void setIsOn(boolean isOn) {
            this.isOn = isOn;
        }
        public void setPotencia(double potencia) {
            this.potencia = potencia;
        }
        public void toggleShooter(double potencia) {
            if (this.isOn == false) {
                setIsOn(true);
                setPotencia(potencia);
            } else {
                setPotencia(0);
                setIsOn(false);
            }
        }
    }


    @Override
    public void init() {
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        ExtensorDireito = hardwareMap.get(Servo.class, "ExtensorDireito");
        ExtensorEsquerdo = hardwareMap.get(Servo.class, "ExtensorEsquerdo");

        Shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //TOGGLE
    }

    @Override
    public void loop() {

        //Servos
        if (gamepad1.dpad_left) {
            PolenPosition();
        }
        if (gamepad1.dpad_right) {
            NectarPosition();
        }

        //Shooter
        MaquinaEstado shooterEstado = new MaquinaEstado();
        if (gamepad1.backWasPressed()) {
            shooterEstado.toggleShooter(0.6);
        }
    }

    public void PolenPosition() {
        ExtensorDireito.setPosition(0.5);
        ExtensorEsquerdo.setPosition(0.5);
    }

    public void NectarPosition() {
        ExtensorDireito.setPosition(0.1);
        ExtensorEsquerdo.setPosition(1.0);
    }

}
