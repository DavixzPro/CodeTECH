package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "CT04B1_Autonomo")
public class BeefrostAutonomo_UC extends LinearOpMode {

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;

    private GoBildaPinpointDriver pinpoint;

    private static final double TOLERANCIA_DISTANCIA = 0.5;
    private static final double TOLERANCIA_ANGULO = 1.0;


    @Override
    public void runOpMode() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        FL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.FORWARD);
        BL.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        /*
         * xOffset:
         * quanto o X POD está para a esquerda/direita
         * do centro do robô.
         * yOffset:
         * quanto o Y POD está para frente/trás
         * do centro do robô.
         */

        pinpoint.setOffsets(0, 0, DistanceUnit.MM);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.setPosition(
                new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        pinpoint.update();

        telemetry.addLine("=== Beefrost AUTO ===");
        telemetry.addLine("");
        telemetry.addLine("Pinpoint configurado");
        telemetry.addLine("Motores configurados");
        telemetry.addLine("");
        telemetry.addData("Y", "%.2f in", pinpoint.getPosX(DistanceUnit.INCH));
        telemetry.addData("X", "%.2f in", pinpoint.getPosY(DistanceUnit.INCH));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();

        waitForStart();
        if (opModeIsActive()) {

            andarFrente(0.2, 96);
            girarEsquerda(0.2, 180);
            andarFrente(0.2, 48);
            andarDireita(0.2, 24);
            parar();
        }
    }

    /////////////////////////// MOVIMENTO ////////////////////////////////
    private void andarFrente(double potencia, double distancia) {

        potencia = Math.abs(potencia);
        distancia = Math.abs(distancia);
        pinpoint.update();

        double xInicial = pinpoint.getPosX(DistanceUnit.INCH);

        while (opModeIsActive()) {

            pinpoint.update();
            double xAtual = pinpoint.getPosX(DistanceUnit.INCH);
            double distanciaPercorrida = Math.abs(xAtual - xInicial);

            mostrarTelemetria("FRENTE", distanciaPercorrida, distancia);

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA) {
                break;
            }

            moverFrente(potencia);
        }

        parar();
    }
    private void andarTras(double potencia, double distancia) {

        potencia = Math.abs(potencia);
        distancia = Math.abs(distancia);
        pinpoint.update();

        double xInicial = pinpoint.getPosX(DistanceUnit.INCH);

        while (opModeIsActive()) {

            pinpoint.update();
            double xAtual = pinpoint.getPosX(DistanceUnit.INCH);
            double distanciaPercorrida =
                    Math.abs(xAtual - xInicial);

            mostrarTelemetria("TRÁS", distanciaPercorrida, distancia);

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA) {
                break;
            }

            moverTras(potencia);
        }

        parar();
    }

    private void andarEsquerda(double potencia, double distancia) {

        potencia = Math.abs(potencia);
        distancia = Math.abs(distancia);
        pinpoint.update();

        double yInicial = pinpoint.getPosY(DistanceUnit.INCH);

        while (opModeIsActive()) {

            pinpoint.update();
            double yAtual = pinpoint.getPosY(DistanceUnit.INCH);
            double distanciaPercorrida = Math.abs(yAtual - yInicial);

            mostrarTelemetria("ESQUERDA", distanciaPercorrida, distancia);

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA) {
                break;
            }

            moverEsquerda(potencia);
        }

        parar();
    }
    private void andarDireita(double potencia, double distancia) {

        potencia = Math.abs(potencia);
        distancia = Math.abs(distancia);
        pinpoint.update();

        double yInicial = pinpoint.getPosY(DistanceUnit.INCH);

        while (opModeIsActive()) {

            pinpoint.update();
            double yAtual = pinpoint.getPosY(DistanceUnit.INCH);
            double distanciaPercorrida = Math.abs(yAtual - yInicial);

            mostrarTelemetria("DIREITA", distanciaPercorrida, distancia);

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA) {
                break;
            }

            moverDireita(potencia);
        }

        parar();
    }

    ///////////////////////////// GIRO ///////////////////////////////////
    private void girarDireita(double potenciaMaxima, double angulo) {

        potenciaMaxima = Math.abs(potenciaMaxima);
        angulo = Math.abs(angulo);

        pinpoint.update();

        double headingInicial = pinpoint.getHeading(AngleUnit.DEGREES);

        while (opModeIsActive()) {

            pinpoint.update();
            double headingAtual = pinpoint.getHeading(AngleUnit.DEGREES);
            double grausGirados =
                    Math.abs(normalizarAngulo(headingAtual - headingInicial));
            double erro = angulo - grausGirados;

            telemetry.addData("Movimento", "DIREITA");
            telemetry.addData("Girado", "%.2f / %.2f graus", grausGirados, angulo);
            telemetry.addData("Heading", "%.2f graus", headingAtual);
            telemetry.update();

            if (grausGirados >= angulo - TOLERANCIA_ANGULO) {
                break;
            }

            double potencia;

            if (erro > 30) {
                potencia = potenciaMaxima;
            }
            else if (erro > 15) {
                potencia = 0.40;
            }
            else if (erro > 5) {
                potencia = 0.25;
            }
            else {
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

        double headingInicial = pinpoint.getHeading(AngleUnit.DEGREES);

        while (opModeIsActive()) {

            pinpoint.update();
            double headingAtual = pinpoint.getHeading(AngleUnit.DEGREES);
            double grausGirados =
                    Math.abs(normalizarAngulo(headingAtual - headingInicial));
            double erro = angulo - grausGirados;

            telemetry.addData("Movimento", "ESQUERDA");
            telemetry.addData("Girado", "%.2f / %.2f graus", grausGirados, angulo);
            telemetry.addData("Heading", "%.2f graus", headingAtual);
            telemetry.update();

            if (grausGirados >= angulo - TOLERANCIA_ANGULO) {
                break;
            }

            double potencia;

            if (erro > 30) {
                potencia = potenciaMaxima;
            }
            else if (erro > 15) {
                potencia = 0.40;
            }
            else if (erro > 5) {
                potencia = 0.25;
            }
            else {
                potencia = 0.15;
            }


            potencia = Math.min(potencia, potenciaMaxima);
            girarEsquerdaMotores(potencia);
        }

        parar();
        sleep(100);
    }

    /////////////////////////// MOTORES /////////////////////////////////
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
    private void moverDireita(double potencia) {
        FL.setPower(potencia);
        FR.setPower(-potencia);
        BL.setPower(-potencia);
        BR.setPower(potencia);
    }
    private void moverEsquerda(double potencia) {
        FL.setPower(-potencia);
        FR.setPower(potencia);
        BL.setPower(potencia);
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
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    ///////////////////////// TELEMETRIA ///////////////////////////////
    private void mostrarTelemetria(String movimento, double percorrido, double alvo) {

        pinpoint.update();
        telemetry.addData("Movimento", movimento);
        telemetry.addData("Progresso", "%.2f / %.2f in", percorrido, alvo);
        telemetry.addData("Y", "%.2f in", pinpoint.getPosX(DistanceUnit.INCH));
        telemetry.addData("X", "%.2f in", pinpoint.getPosY(DistanceUnit.INCH));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();
    }

    ////////////////////////// ANGULO //////////////////////////////////
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