package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "BeefrostAutonomo_UC")
public class BeefrostAutonomo_UC extends LinearOpMode {

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;

    private GoBildaPinpointDriver pinpoint;

    private static final double TOLERANCIA_CM = 1.0;
    private static final double TOLERANCIA_GRAUS = 1.0;

    @Override
    public void runOpMode() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);

        FL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        pinpoint.setOffsets(165, 50, DistanceUnit.MM);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.setPosition(
                new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));

        pinpoint.update();

        telemetry.addLine("Beefrost configurado");
        telemetry.addData("X", "%.2f cm", pinpoint.getPosX(DistanceUnit.CM));
        telemetry.addData("Y", "%.2f cm", pinpoint.getPosY(DistanceUnit.CM));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();

        waitForStart();
        if (opModeIsActive()) {

            andarFrente(0.4, 30);
            girarEsquerda(0.4, 90);
            andarFrente(0.4, 30);
            andarDireita(0.4, 30);
            parar();
        }
    }

    private void andarFrente(double potencia, double distanciaCm) {
        moverRelativo(potencia, 0, distanciaCm, "FRENTE");
    }
    private void andarTras(double potencia, double distanciaCm) {
        moverRelativo(-potencia, 0, distanciaCm, "TRÁS");
    }
    private void andarDireita(double potencia, double distanciaCm) {
        moverRelativo(0, potencia, distanciaCm, "DIREITA");
    }
    private void andarEsquerda(double potencia, double distanciaCm) {
        moverRelativo(0, -potencia, distanciaCm, "ESQUERDA");
    }

    private void moverRelativo(
            double frente,
            double lateral,
            double distanciaCm,
            String movimento) {

        frente = Math.max(-1.0, Math.min(1.0, frente));
        lateral = Math.max(-1.0, Math.min(1.0, lateral));

        pinpoint.update();
        double xInicial = pinpoint.getPosX(DistanceUnit.CM);
        double yInicial = pinpoint.getPosY(DistanceUnit.CM);
        double headingInicial = pinpoint.getHeading(AngleUnit.RADIANS);

        double distanciaInicial = Math.hypot(frente, lateral);

        if (distanciaInicial == 0) {
            parar();
            return;
        }

        double frenteUnitario = frente / distanciaInicial;
        double lateralUnitario = lateral / distanciaInicial;

        double direcaoX =
                Math.cos(headingInicial) * frenteUnitario
                        - Math.sin(headingInicial) * lateralUnitario;

        double direcaoY =
                Math.sin(headingInicial) * frenteUnitario
                        + Math.cos(headingInicial) * lateralUnitario;

        double alvoX = xInicial + direcaoX * distanciaCm;
        double alvoY = yInicial + direcaoY * distanciaCm;

        while (opModeIsActive()) {

            pinpoint.update();
            double xAtual = pinpoint.getPosX(DistanceUnit.CM);
            double yAtual = pinpoint.getPosY(DistanceUnit.CM);
            double headingAtual = pinpoint.getHeading(AngleUnit.RADIANS);

            double erroX = alvoX - xAtual;
            double erroY = alvoY - yAtual;

            double distanciaRestante = Math.hypot(erroX, erroY);

            telemetry.addData("Movimento", movimento);
            telemetry.addData("Distância", "%.2f / %.2f cm", distanciaCm - distanciaRestante, distanciaCm);
            telemetry.addData("X", "%.2f / %.2f", xAtual, alvoX);
            telemetry.addData("Y", "%.2f / %.2f", yAtual, alvoY);
            telemetry.addData("Heading", "%.2f°", Math.toDegrees(headingAtual));
            telemetry.update();

            if (distanciaRestante <= TOLERANCIA_CM) {
                break;
            }

            double erroFrente =
                    erroX * Math.cos(headingAtual)
                            + erroY * Math.sin(headingAtual);

            double erroLateral =
                    -erroX * Math.sin(headingAtual)
                            + erroY * Math.cos(headingAtual);


            double comandoFrente = erroFrente * 0.035;
            double comandoLateral = erroLateral * 0.035;

            double potenciaLimite = Math.max(Math.abs(frente), Math.abs(lateral));
            comandoFrente = limitar(comandoFrente, -potenciaLimite, potenciaLimite);
            comandoLateral = limitar(comandoLateral, -potenciaLimite, potenciaLimite);

            moverMecanum(comandoFrente, comandoLateral);
        }

        parar();
        sleep(100);
    }

    private void girarEsquerda(double potenciaMaxima, double angulo) {
        girar(-Math.abs(potenciaMaxima), Math.abs(angulo), "ESQUERDA");
    }
    private void girarDireita(double potenciaMaxima, double angulo) {
        girar(Math.abs(potenciaMaxima), Math.abs(angulo), "DIREITA");
    }

    private void girar(
            double potenciaMaxima,
            double anguloAlvo,
            String movimento) {

        pinpoint.update();
        double headingInicial = pinpoint.getHeading(AngleUnit.DEGREES);

        while (opModeIsActive()) {

            pinpoint.update();
            double headingAtual = pinpoint.getHeading(AngleUnit.DEGREES);

            double grausGirados =
                    Math.abs(normalizarAngulo(
                            headingAtual - headingInicial));

            double erro = anguloAlvo - grausGirados;

            telemetry.addData("Movimento", movimento);
            telemetry.addData("Girado", "%.2f / %.2f°", grausGirados, anguloAlvo);
            telemetry.addData("Heading", "%.2f°", headingAtual);
            telemetry.update();

            if (grausGirados >= anguloAlvo - TOLERANCIA_GRAUS) {
                break;
            }

            double potencia;

            if (erro > 30) {
                potencia = Math.abs(potenciaMaxima);
            } else if (erro > 15) {
                potencia = 0.40;
            } else if (erro > 5) {
                potencia = 0.25;
            } else {
                potencia = 0.15;
            }

            potencia = Math.min(
                    potencia,
                    Math.abs(potenciaMaxima));

            if (potenciaMaxima > 0) {
                girarDireitaMotores(potencia);
            } else {
                girarEsquerdaMotores(potencia);
            }
        }

        parar();
        sleep(100);
    }

    private void moverMecanum(double frente, double lateral) {

        double fl = frente + lateral;
        double fr = frente - lateral;
        double bl = frente - lateral;
        double br = frente + lateral;

        double max = Math.max(1.0, Math.max(Math.abs(fl),
                Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));

        FL.setPower(fl / max);
        FR.setPower(fr / max);
        BL.setPower(bl / max);
        BR.setPower(br / max);
    }

    private void girarDireitaMotores(double potencia) {
        FL.setPower(potencia);
        FR.setPower(-potencia);
        BL.setPower(potencia);
        BR.setPower(-potencia);
    }
    private void girarEsquerdaMotores(double potencia) {
        FL.setPower(-potencia);
        FR.setPower(potencia);
        BL.setPower(-potencia);
        BR.setPower(potencia);
    }

    private void parar() {
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    private double limitar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
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