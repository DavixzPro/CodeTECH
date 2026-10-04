package org.firstinspires.ftc.teamcode.CT04B;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "CT04B2")
public class CT04B2 extends OpMode {

    DcMotorEx Shooter;
    DcMotor Intakes;
    Servo ExtensorEsquerdo;
    Servo ExtensorDireito;
    DcMotor FL, BL, FR, BR;
    DistanceSensor SensorDistancia;
    ColorSensor SensorCor;
    VoltageSensor Battery;

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
        Battery = hardwareMap.voltageSensor.iterator().next();
        SensorCor = hardwareMap.get(ColorSensor.class, "SensorCor");

        Intakes = hardwareMap.get(DcMotor.class, "Intakes");

        Intakes.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

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

        PolenPosition();
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

        if (gamepad1.dpadLeftWasPressed()) {
            PolenPosition();
        }
        if (gamepad1.dpadRightWasPressed()) {
            NectarPosition();
        }

        //////////////////////////////////Intakes/////////////////////////////////

        if (gamepad1.x) {
            Intakes.setPower(1);
        } else if (gamepad1.y) {
            Intakes.setPower(-1);
        } else {
            Intakes.setPower(0);
        }


        ////////////////////////////////// Shooter //////////////////////////////////

        if (gamepad1.bWasPressed()) {
            shooterEstado.toggleShooter();
        }

        double distance = SensorDistancia.getDistance(DistanceUnit.CM);
        double potenciaShooter = 0;

        if (shooterEstado.getIsOn()) {

            if (distance <= 21) {
                potenciaShooter = 0.57;

            } else if (distance >= 33) {
                potenciaShooter = 0.45;

            } else {
                potenciaShooter = 0.45 + ((33.0 - distance) * (0.12 / 12.0));
            }

            Shooter.setPower(potenciaShooter);

        } else {
            Shooter.setPower(0);
        }

        ////////////////////////////////Telemetria/////////////////////////////////

        double voltagem = Battery.getVoltage();
        double rpm = Shooter.getVelocity() / 28.0 * 60.0;

        telemetry.addData("Bateria", "%.2f V", voltagem);
        telemetry.addData("Shooter RPM", "%.0f", rpm);
        telemetry.addData("Distância", "%.2f cm", distance);
        telemetry.update();

    }

    public void PolenPosition() {
        ExtensorDireito.setPosition(0.45);
        ExtensorEsquerdo.setPosition(0.55);
    }

    public void NectarPosition() {
        ExtensorDireito.setPosition(0.25);
        ExtensorEsquerdo.setPosition(0.75);
    }

}
