package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Linear PID", group = "Mecanismos")
public class TesteDoLinear extends LinearOpMode {

    private DcMotor linear;

    // ================= POSIÇÕES =================

    final int BAIXO = 0;
    final int MEDIO = -1128;
    final int ALTO = 2255;


    // ================= PID =================

    double Kp = 0.005;
    double Ki = 0.000;
    double Kd = 0.0005;

    double erroAnterior = 0;
    double integral = 0;
    long ultimoTempo = 0;


    // ================= FUNÇÃO PID =================

    private double calcularPID(int alvo) {

        int posicaoAtual = linear.getCurrentPosition();

        double erro = alvo - posicaoAtual;

        long tempoAtual = System.currentTimeMillis();
        double dt = (tempoAtual - ultimoTempo) / 1000.0;

        if (dt <= 0) {
            dt = 0.001;
        }


        // ===== PROPORCIONAL =====

        double proporcional = Kp * erro;

        // ===== INTEGRAL =====

        integral += erro * dt;
        double integralTermo = Ki * integral;


        // ===== DERIVATIVO =====

        double derivativo = (erro - erroAnterior) / dt;
        double derivativoTermo = Kd * derivativo;

        // Guarda valores
        erroAnterior = erro;
        ultimoTempo = tempoAtual;

        // ===== PID =====

        double potencia =
                proporcional
                        + integralTermo
                        + derivativoTermo;


        // Limita entre -1 e 1

        potencia = Math.max(-1, Math.min(1, potencia));

        return potencia;
    }


    // ================= PROGRAMA PRINCIPAL =================

    @Override
    public void runOpMode() {

        // Hardware
        linear = hardwareMap.get(DcMotor.class, "linear");


        // Configuração
        linear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        int alvo = BAIXO;

        ultimoTempo = System.currentTimeMillis();


        // ================= LOOP =================

        while (opModeIsActive()) {

            // ===== ESCOLHA DA POSIÇÃO =====
            if (gamepad1.y) {
                alvo = ALTO;
            }
            else if (gamepad1.x) {
                alvo = MEDIO;
            }
            else if (gamepad1.a) {
                alvo = BAIXO;
            }

            // ===== PID =====
            double potencia = calcularPID(alvo);
            linear.setPower(potencia);


            // ===== TELEMETRIA =====
            telemetry.addData("Posição", linear.getCurrentPosition());
            telemetry.addData("Alvo", alvo);
            telemetry.addData("Erro", alvo - linear.getCurrentPosition());
            telemetry.addData("Potência", potencia);

            telemetry.update();
        }
    }
}
