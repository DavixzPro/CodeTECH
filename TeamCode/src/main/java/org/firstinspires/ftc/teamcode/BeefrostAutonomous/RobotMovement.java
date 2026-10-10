package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.hardware.limelightvision.LLResult;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public abstract class RobotMovement extends RobotConfig {

    //======================= MOVIMENTO E GIRO =========================//
    void andarFrente(double potencia, double distanciaCm) {
        moverRelativo(potencia, 0, distanciaCm, "FRENTE");
    }
    void andarTras(double potencia, double distanciaCm) {
        moverRelativo(-potencia, 0, distanciaCm, "TRÁS");
    }
    void andarDireita(double potencia, double distanciaCm) {
        moverRelativo(0, potencia, distanciaCm, "DIREITA");
    }
    void andarEsquerda(double potencia, double distanciaCm) {
        moverRelativo(0, -potencia, distanciaCm, "ESQUERDA");
    }

    void moverRelativo(
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


            double potenciaMaxima = Math.max(Math.abs(frente), Math.abs(lateral));
            double distanciaDesaceleracao = 15.0;
            double potenciaMinima = 0.12;
            double fator = Math.min(1.0, distanciaRestante / distanciaDesaceleracao);

            double potencia = potenciaMinima + (potenciaMaxima - potenciaMinima) * fator;

            double moduloErro = Math.hypot(erroFrente, erroLateral);

            if (moduloErro > 0) {
                double comandoFrente = (erroFrente / moduloErro) * potencia;
                double comandoLateral = (erroLateral / moduloErro) * potencia;

                moverMecanum(comandoFrente, comandoLateral);
            }

        }

        parar();
        sleep(100);
    }

    void girarEsquerda(double potenciaMaxima, double angulo) {
        girar(-Math.abs(potenciaMaxima), Math.abs(angulo), "ESQUERDA");
    }
    void girarDireita(double potenciaMaxima, double angulo) {
        girar(Math.abs(potenciaMaxima), Math.abs(angulo), "DIREITA");
    }

    void girar(
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

    void moverMecanum(double frente, double lateral) {

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

    void girarDireitaMotores(double potencia) {
        FL.setPower(potencia);
        FR.setPower(-potencia);
        BL.setPower(potencia);
        BR.setPower(-potencia);
    }
    void girarEsquerdaMotores(double potencia) {
        FL.setPower(-potencia);
        FR.setPower(potencia);
        BL.setPower(-potencia);
        BR.setPower(potencia);
    }

    void parar() {
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    void alinharAprilTag(double potenciaMaxima) {

        double toleranciaTX = 1.0;

        while (opModeIsActive()) {

            limelight.updateRobotOrientation(
                    pinpoint.getHeading(
                            org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES));

            LLResult resultado = limelight.getLatestResult();

            if (resultado == null || !resultado.isValid() || resultado.getFiducialResults().isEmpty()) {

                parar();

                telemetry.addLine("Limelight: procurando AprilTag");
                telemetry.update();

                sleep(20);
                continue;
            }

            double tx = resultado.getTx();

            telemetry.addData("AprilTag", "Detectado");
            telemetry.addData("TX", "%.2f graus", tx);
            telemetry.update();

            if (Math.abs(tx) <= toleranciaTX) {
                parar();

                telemetry.addLine("AprilTag alinhado!");
                telemetry.update();

                break;
            }

            double rotacao = tx * kP;

            rotacao = Math.max(
                    -Math.abs(potenciaMaxima),
                    Math.min(Math.abs(potenciaMaxima), rotacao));

            double potenciaMinima = Math.min(0.12, Math.abs(potenciaMaxima));

            if (Math.abs(rotacao) < potenciaMinima) {
                rotacao = Math.copySign(potenciaMinima, rotacao);
            }

            girarParaAlinhar(rotacao);
            sleep(20);
        }

        parar();
    }

    private void girarParaAlinhar(double potencia) {

        FL.setPower(potencia);
        FR.setPower(-potencia);
        BL.setPower(potencia);
        BR.setPower(-potencia);
    }

    double limitar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

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