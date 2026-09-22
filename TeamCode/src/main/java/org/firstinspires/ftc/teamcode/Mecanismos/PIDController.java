package org.firstinspires.ftc.teamcode.Mecanismos;

public class PIDController {

    private double kP;
    private double kI;
    private double kD;

    private double erroAnterior = 0;
    private double integral = 0;

    private long tempoAnterior;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        tempoAnterior = System.nanoTime();
    }

    public double calcular(double erro) {

        long agora = System.nanoTime();

        double dt = (agora - tempoAnterior) / 1_000_000_000.0;

        tempoAnterior = agora;

        if (dt <= 0 || dt > 0.1) {
            dt = 0.02;
        }

        // P
        double P = kP * erro;

        // I
        integral += erro * dt;
        double I = kI * integral;

        // D
        double derivada = (erro - erroAnterior) / dt;
        double D = kD * derivada;

        erroAnterior = erro;

        return P + I + D;
    }

    public void reset() {
        erroAnterior = 0;
        integral = 0;
        tempoAnterior = System.nanoTime();
    }
}