package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "CT04B1_Autonomo")
public class BeefrostAutonomo_UC extends LinearOpMode {

    private DcMotor FL, FR, BL, BR;
    private GoBildaPinpointDriver pinpoint;

    private static final double TOLERANCIA_DISTANCIA = 1.0; // cm
    private static final double TOLERANCIA_ANGULO = 1.0;    // graus

    @Override
    public void runOpMode() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
        );

        FL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.REVERSE);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pinpoint.setOffsets(0, 0, DistanceUnit.MM);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.setPosition(
                new Pose2D(
                        DistanceUnit.CM,
                        0, 0,
                        AngleUnit.DEGREES,
                        0
                )
        );

        pinpoint.update();

        telemetry.addLine("=== BEEFROST AUTO ===");
        telemetry.addLine("Pinpoint configurado");
        telemetry.addData("X", "%.2f cm", pinpoint.getPosX(DistanceUnit.CM));
        telemetry.addData("Y", "%.2f cm", pinpoint.getPosY(DistanceUnit.CM));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            andarFrente(0.5, 10);
            girarEsquerda(0.2, 90);
            andarFrente(0.5, 10);

            parar();
        }
    }

    // ============================================================
    // MOVIMENTOS RETILÍNEOS
    // ============================================================

    private void andarFrente(double potencia, double distancia) {
        moverRelativo(potencia, 0, distancia, "FRENTE");
    }

    private void andarTras(double potencia, double distancia) {
        moverRelativo(-potencia, 0, distancia, "TRÁS");
    }

    private void andarDireita(double potencia, double distancia) {
        moverRelativo(0, potencia, distancia, "DIREITA");
    }

    private void andarEsquerda(double potencia, double distancia) {
        moverRelativo(0, -potencia, distancia, "ESQUERDA");
    }

    // ============================================================
    // MOVER RELATIVO
    // ============================================================

    private void moverRelativo(double frente, double lateral, double distancia, String nome) {
        distancia = Math.abs(distancia);

        double potenciaFrente = frente;
        double potenciaLateral = lateral;

        pinpoint.update();

        double xInicial = pinpoint.getPosX(DistanceUnit.CM);
        double yInicial = pinpoint.getPosY(DistanceUnit.CM);
        double headingInicial = pinpoint.getHeading(AngleUnit.RADIANS);

        // Vetor da direção "frente" do robô no campo
        double frenteX = Math.cos(headingInicial);
        double frenteY = Math.sin(headingInicial);

        // Vetor da direção "direita" do robô no campo
        double direitaX = -Math.sin(headingInicial);
        double direitaY = Math.cos(headingInicial);

        double comprimento = Math.sqrt(
                potenciaFrente * potenciaFrente +
                        potenciaLateral * potenciaLateral
        );

        if (comprimento == 0) {
            parar();
            return;
        }

        while (opModeIsActive()) {
            pinpoint.update();

            double xAtual = pinpoint.getPosX(DistanceUnit.CM);
            double yAtual = pinpoint.getPosY(DistanceUnit.CM);

            double dx = xAtual - xInicial;
            double dy = yAtual - yInicial;

            // Quanto o robô andou na direção desejada
            double deslocamentoFrente =
                    dx * frenteX + dy * frenteY;

            double deslocamentoLateral =
                    dx * direitaX + dy * direitaY;

            // Combina frente/lateral para descobrir a distância
            // percorrida na direção do movimento solicitado.
            double direcaoX =
                    (potenciaFrente * frenteX +
                            potenciaLateral * direitaX) / comprimento;

            double direcaoY =
                    (potenciaFrente * frenteY +
                            potenciaLateral * direitaY) / comprimento;

            double distanciaPercorrida =
                    deslocamentoFrente * (potenciaFrente / comprimento) +
                            deslocamentoLateral * (potenciaLateral / comprimento);

            mostrarTelemetria(
                    nome,
                    distanciaPercorrida,
                    distancia,
                    deslocamentoFrente,
                    deslocamentoLateral
            );

            if (distanciaPercorrida >= distancia - TOLERANCIA_DISTANCIA) {
                break;
            }

            moverMecanum(
                    potenciaFrente,
                    potenciaLateral
            );
        }

        parar();
        sleep(100);
    }

    // ============================================================
    // MECANUM
    // ============================================================

    private void moverMecanum(double frente, double lateral) {
        double FLPower = frente + lateral;
        double FRPower = frente - lateral;
        double BLPower = frente - lateral;
        double BRPower = frente + lateral;

        double maior = Math.max(
                1.0,
                Math.max(
                        Math.abs(FLPower),
                        Math.max(
                                Math.abs(FRPower),
                                Math.max(
                                        Math.abs(BLPower),
                                        Math.abs(BRPower)
                                )
                        )
                )
        );

        FL.setPower(FLPower / maior);
        FR.setPower(FRPower / maior);
        BL.setPower(BLPower / maior);
        BR.setPower(BRPower / maior);
    }

    // ============================================================
    // GIROS
    // ============================================================

    private void girarDireita(double potenciaMaxima, double angulo) {
        girar(potenciaMaxima, angulo, 1, "DIREITA");
    }

    private void girarEsquerda(double potenciaMaxima, double angulo) {
        girar(potenciaMaxima, angulo, -1, "ESQUERDA");
    }

    private void girar(double potenciaMaxima, double angulo, int sentido, String nome) {
        potenciaMaxima = Math.abs(potenciaMaxima);
        angulo = Math.abs(angulo);

        pinpoint.update();

        double headingInicial = pinpoint.getHeading(AngleUnit.DEGREES);

        while (opModeIsActive()) {
            pinpoint.update();

            double headingAtual = pinpoint.getHeading(AngleUnit.DEGREES);

            double diferenca = normalizarAngulo(
                    headingAtual - headingInicial
            );

            double grausGirados = Math.abs(diferenca);
            double erro = angulo - grausGirados;

            telemetry.addData("Movimento", "GIRO " + nome);
            telemetry.addData("Girado", "%.2f / %.2f graus", grausGirados, angulo);
            telemetry.addData("Heading", "%.2f°", headingAtual);
            telemetry.update();

            if (grausGirados >= angulo - TOLERANCIA_ANGULO) {
                break;
            }

            double potencia;

            if (erro > 30) {
                potencia = potenciaMaxima;
            } else if (erro > 15) {
                potencia = Math.min(0.40, potenciaMaxima);
            } else if (erro > 5) {
                potencia = Math.min(0.25, potenciaMaxima);
            } else {
                potencia = Math.min(0.15, potenciaMaxima);
            }

            girarMotores(potencia * sentido);
        }

        parar();
        sleep(100);
    }

    private void girarMotores(double potencia) {
        FL.setPower(potencia);
        BL.setPower(potencia);
        FR.setPower(-potencia);
        BR.setPower(-potencia);
    }

    // ============================================================
    // PARAR
    // ============================================================

    private void parar() {
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    // ============================================================
    // TELEMETRIA
    // ============================================================

    private void mostrarTelemetria(
            String movimento,
            double percorrido,
            double alvo,
            double frente,
            double lateral
    ) {
        telemetry.addData("Movimento", movimento);
        telemetry.addData("Progresso", "%.2f / %.2f cm", percorrido, alvo);
        telemetry.addData("Deslocamento Frente", "%.2f cm", frente);
        telemetry.addData("Deslocamento Lateral", "%.2f cm", lateral);
        telemetry.addData("X", "%.2f cm", pinpoint.getPosX(DistanceUnit.CM));
        telemetry.addData("Y", "%.2f cm", pinpoint.getPosY(DistanceUnit.CM));
        telemetry.addData("Heading", "%.2f°", pinpoint.getHeading(AngleUnit.DEGREES));
        telemetry.update();
    }

    // ============================================================
    // ÂNGULO
    // ============================================================

    private double normalizarAngulo(double angulo) {
        while (angulo > 180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}