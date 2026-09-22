package org.firstinspires.ftc.teamcode.CT04B1;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "CT04B1_Autonomo")
public class CT04B1_Autonomo extends LinearOpMode {

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;

    private GoBildaPinpointDriver pinpoint;

    // Tolerâncias
    private static final double TOLERANCIA_DISTANCIA_INCHES = 0.5;
    private static final double TOLERANCIA_ANGULO_GRAUS = 1.0;

    @Override
    public void runOpMode() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        FL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BL.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.FORWARD);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );
        pinpoint.setOffsets(0, 50, DistanceUnit.INCH);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 5, AngleUnit.DEGREES, 0));
        pinpoint.update();

        telemetry.addLine("=== CT04B1 AUTO ===");
        telemetry.addLine("");
        telemetry.addLine("Pinpoint configurado");
        telemetry.addLine("Pronto!");
        telemetry.update();

        waitForStart();
        if (opModeIsActive()) {

            andarFrente(0.2, 96, DistanceUnit.INCH, "in");
            parar();
            girarEsquerda(0.2, 180);
            parar();
            andarFrente(0.2, 48, DistanceUnit.INCH, "in");
            parar();
        }
    }

    private void andarFrente(double potencia, double distancia, DistanceUnit unidade, String unidadeStr) {

        potencia = Math.abs(potencia);
        distancia = Math.abs(distancia);
        pinpoint.update();

        double xInicial = pinpoint.getPosX(unidade);

        while (opModeIsActive()) {
            pinpoint.update();

            double xAtual = pinpoint.getPosX(unidade);
            double deltaX = xAtual - xInicial;
            double distanciaPercorrida = Math.abs(deltaX);

            telemetry.addData("Movimento", "FRENTE");
            telemetry.addData("Distancia", "%.2f / %.2f " + unidadeStr, distanciaPercorrida, distancia);
            telemetry.addData("X", "%.2f " + unidadeStr, xAtual);
            telemetry.addData("Angulo", "%.2f graus", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA_INCHES) {
                break;
            }

            moverFrente(potencia);
        }

        parar();
    }

    private void andarTras(double potencia, double distancia, DistanceUnit unidade, String unidadeStr) {

        potencia = Math.abs(potencia);
        distancia = Math.abs(distancia);
        pinpoint.update();

        double xInicial = pinpoint.getPosX(unidade);

        while (opModeIsActive()) {
            pinpoint.update();

            double xAtual = pinpoint.getPosX(unidade);
            double deltaX = xAtual - xInicial;
            double distanciaPercorrida = Math.abs(deltaX);

            telemetry.addData("Movimento", "TRÁS");
            telemetry.addData("Distancia", "%.2f / %.2f " + unidadeStr, distanciaPercorrida, distancia);
            telemetry.addData("X", "%.2f " + unidadeStr, xAtual);
            telemetry.addData("Angulo", "%.2f graus", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA_INCHES) {
                break;
            }

            moverTras(potencia);
        }
        parar();
    }

    private void girarDireita(double potenciaMaxima, double angulo) {

        potenciaMaxima = Math.abs(potenciaMaxima);
        angulo = Math.abs(angulo);
        pinpoint.update();

        double headingAnterior = pinpoint.getHeading(AngleUnit.DEGREES);
        double grausGirados = 0;

        while (opModeIsActive()) {
            pinpoint.update();

            double headingAtual = pinpoint.getHeading(AngleUnit.DEGREES);
            double delta = normalizarAngulo(headingAtual - headingAnterior);
            grausGirados += Math.abs(delta);
            headingAnterior = headingAtual;
            double erro = angulo - grausGirados;

            telemetry.addData("Movimento", "DIREITA");
            telemetry.addData("Girado", "%.2f / %.2f graus", grausGirados, angulo);
            telemetry.addData("Heading", "%.2f graus", headingAtual);
            telemetry.update();

            if (grausGirados >= angulo - TOLERANCIA_ANGULO_GRAUS) {
                break;
            }

            double potencia;

            if (erro > 30) {
                potencia = potenciaMaxima;
            } else if (erro > 15) {
                potencia = 0.40;
            } else if (erro > 5) {
                potencia = 0.25;
            } else {
                potencia = 0.15;
            }

            potencia = Math.min(potencia, potenciaMaxima);
            girarDireitaMotores(potencia);
        }

        parar();
        sleep(100);
    }

    private void girarEsquerda(double potenciaMaxima, double angulo) {

        potenciaMaxima = Math.abs(potenciaMaxima);
        angulo = Math.abs(angulo);
        pinpoint.update();

        double headingAnterior = pinpoint.getHeading(AngleUnit.DEGREES);
        double grausGirados = 0;

        while (opModeIsActive()) {
            pinpoint.update();

            double headingAtual = pinpoint.getHeading(AngleUnit.DEGREES);
            double delta = normalizarAngulo(headingAtual - headingAnterior);
            grausGirados += Math.abs(delta);
            headingAnterior = headingAtual;
            double erro = angulo - grausGirados;

            telemetry.addData("Movimento", "ESQUERDA");
            telemetry.addData("Girado", "%.2f / %.2f graus", grausGirados, angulo);
            telemetry.addData("Heading", "%.2f graus", headingAtual);
            telemetry.update();

            if (grausGirados >= angulo - TOLERANCIA_ANGULO_GRAUS) {
                break;
            }

            double potencia;

            if (erro > 30) {
                potencia = potenciaMaxima;
            } else if (erro > 15) {
                potencia = 0.40;
            } else if (erro > 5) {
                potencia = 0.25;
            } else {
                potencia = 0.15;
            }

            potencia = Math.min(potencia, potenciaMaxima);
            girarEsquerdaMotores(potencia);
        }

        parar();
        sleep(100);
    }

    private void moverFrente(double potencia) {
        FL.setPower(potencia);
        FR.setPower(potencia);
        BL.setPower(potencia);
        BR.setPower(potencia);
    }

    private void moverTras(double potencia) {
        FL.setPower(-potencia);
        FR.setPower(-potencia);
        BL.setPower(-potencia);
        BR.setPower(-potencia);
    }

    private void girarDireitaMotores(double potencia) {
        FL.setPower(potencia);
        BL.setPower(potencia);
        FR.setPower(-potencia);
        BR.setPower(-potencia);
    }

    private void girarEsquerdaMotores(double potencia) {
        FL.setPower(-potencia);
        BL.setPower(-potencia);
        FR.setPower(potencia);
        BR.setPower(potencia);
    }

    private void parar() {
        pinpoint.update();
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    private double normalizarAngulo(double angulo) {
        while (angulo > 180) {
            angulo -= 360;
        }
        while (angulo < -180) {
            angulo += 360;
        }

        return angulo;
    }
}