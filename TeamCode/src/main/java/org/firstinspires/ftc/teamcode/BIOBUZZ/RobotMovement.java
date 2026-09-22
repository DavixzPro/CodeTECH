package org.firstinspires.ftc.teamcode.BIOBUZZ;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public abstract class RobotMovement extends RobotConfig {

    //======================= MOVIMENTO E GIRO =========================//
    void andarFrente(double potencia, double distanciaEmCm) {

        potencia = Math.abs(potencia);
        distanciaEmCm = Math.abs(distanciaEmCm);
        pinpoint.update();

        double xInicial = pinpoint.getPosX(DistanceUnit.CM);

        while (opModeIsActive()) {
            pinpoint.update();

            double xAtual = pinpoint.getPosX(DistanceUnit.CM);
            double deltaX = xAtual - xInicial;
            double distanciaPercorrida = Math.abs(deltaX);

            telemetry.addData("Movimento", "FRENTE");
            telemetry.addData("Distancia", "%.2f / %.2f cm", distanciaPercorrida, distanciaEmCm);
            telemetry.addData("X", "%.2f cm", xAtual);
            telemetry.addData("Heading", "%.2f graus", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();

            if (distanciaPercorrida >=
                    distanciaEmCm - TOLERANCIA_DISTANCIA_CM) {
                break;
            }

            moverFrente(potencia);
        }
        parar();
    }

     void andarTras(double potencia, double distanciaEmCm) {

        potencia = Math.abs(potencia);
        distanciaEmCm = Math.abs(distanciaEmCm);
        pinpoint.update();

        double xInicial = pinpoint.getPosX(DistanceUnit.CM);

        while (opModeIsActive()) {
            pinpoint.update();

            double xAtual = pinpoint.getPosX(DistanceUnit.CM);
            double deltaX = xAtual - xInicial;
            double distanciaPercorrida = Math.abs(deltaX);

            telemetry.addData("Movimento", "TRÁS");
            telemetry.addData("Distancia", "%.2f / %.2f cm", distanciaPercorrida, distanciaEmCm);
            telemetry.addData("X", "%.2f cm", xAtual);
            telemetry.addData("Heading", "%.2f graus", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();

            if (distanciaPercorrida >= distanciaEmCm - TOLERANCIA_DISTANCIA_CM) {
                break;
            }

            moverTras(potencia);
        }
        parar();
    }

     void girarDireita(double potenciaMaxima, double angulo) {

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

            // Chegou nos 90 graus
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

     void girarEsquerda(double potenciaMaxima, double angulo) {

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

    //===================== APLICAÇÃO DE POTÊNCIA =====================//
     void moverFrente(double potencia) {
        FL.setPower(potencia);
        FR.setPower(potencia);
        BL.setPower(potencia);
        BR.setPower(potencia);
    }

     void moverTras(double potencia) {
        FL.setPower(-potencia);
        FR.setPower(-potencia);
        BL.setPower(-potencia);
        BR.setPower(-potencia);
    }

     void girarDireitaMotores(double potencia) {
        FL.setPower(potencia);
        BL.setPower(potencia);
        FR.setPower(-potencia);
        BR.setPower(-potencia);
    }

     void girarEsquerdaMotores(double potencia) {
        FL.setPower(-potencia);
        BL.setPower(-potencia);
        FR.setPower(potencia);
        BR.setPower(potencia);
    }

     void parar() {
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    //====================== AJUSTE DE ÂNGULO ======================//
     double normalizarAngulo(double angulo) {
        while (angulo > 180) {
            angulo -= 360;
        }
        while (angulo < -180) {
            angulo += 360;
        }

        return angulo;
    }

}