package org.firstinspires.ftc.teamcode.StormBreakerAutonomo;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public abstract class AutonomoTest extends LinearOpMode {
    protected DcMotor FL;
    protected DcMotor FR;
    protected DcMotor BL;
    protected DcMotor BR;
    protected DcMotor ShooterD;
    protected DcMotor Intakes;
    protected DcMotor HelpWheels;

    protected static final double MOTOR_TICKS = 28;
    protected static final double REDUCTION = 15.0;
    protected static final double WHEEL_DIAMETER = 10.4;
    protected static final double TRACK_WIDTH = 24.5;
    protected static final double COUNTS_PER_CM =
            (MOTOR_TICKS * REDUCTION) /
                    (WHEEL_DIAMETER * Math.PI);

    //Inicialização
    protected void configurarRobo() {
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        ShooterD = hardwareMap.get(DcMotor.class, "ShooterD");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        HelpWheels = hardwareMap.get(DcMotor.class, "HelpWheels");

        FR.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);

        ShooterD.setDirection(DcMotorSimple.Direction.FORWARD);
        ShooterD.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ShooterD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        Intakes.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Intakes.setDirection(DcMotor.Direction.REVERSE);

        HelpWheels.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        HelpWheels.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    //Movimentos unidirecionais
    protected void andarFrente(double potencia, double distanciaCm) {
        int alvo = (int) (distanciaCm * COUNTS_PER_CM);

        resetarEncoders();

        FL.setTargetPosition(alvo);
        FR.setTargetPosition(alvo);
        BL.setTargetPosition(alvo);
        BR.setTargetPosition(alvo);

        colocarRunToPosition();
        aplicarPotencia(potencia);
        esperarMotores();
        pararMotores();
        voltarModoNormal();
    }
    protected void andarTras(double potencia, double distanciaCm) {
        int alvo = (int) (distanciaCm * COUNTS_PER_CM);

        resetarEncoders();

        FL.setTargetPosition(-alvo);
        FR.setTargetPosition(-alvo);
        BL.setTargetPosition(-alvo);
        BR.setTargetPosition(-alvo);

        colocarRunToPosition();
        aplicarPotencia(potencia);
        esperarMotores();
        pararMotores();
        voltarModoNormal();
    }
    protected void andarEsquerda(double potencia, double distanciaCm) {
        int alvo = (int) (distanciaCm * COUNTS_PER_CM);

        resetarEncoders();

        FL.setTargetPosition(-alvo);
        FR.setTargetPosition(alvo);
        BL.setTargetPosition(alvo);
        BR.setTargetPosition(-alvo);

        colocarRunToPosition();
        aplicarPotencia(potencia);
        esperarMotores();
        pararMotores();
        voltarModoNormal();
    }

    protected void andarDireita(double potencia, double distanciaCm) {
        int alvo = (int) (distanciaCm * COUNTS_PER_CM);
        resetarEncoders();

        FL.setTargetPosition(alvo);
        FR.setTargetPosition(-alvo);
        BL.setTargetPosition(-alvo);
        BR.setTargetPosition(alvo);

        colocarRunToPosition();
        aplicarPotencia(potencia);
        esperarMotores();
        pararMotores();
        voltarModoNormal();
    }

    protected void girarEsquerda(double graus) {
        double distanciaGiro = (Math.PI * TRACK_WIDTH * graus) / 360.0;
        int alvo = (int) (distanciaGiro * COUNTS_PER_CM);
        resetarEncoders();
        FL.setTargetPosition(-alvo);
        BL.setTargetPosition(-alvo);
        FR.setTargetPosition(alvo);
        BR.setTargetPosition(alvo);

        colocarRunToPosition();
        aplicarPotencia(0.5);
        esperarMotores();
        pararMotores();
        voltarModoNormal();
    }
    protected void girarDireita(double graus) {
        double distanciaGiro = (Math.PI * TRACK_WIDTH * graus) / 360.0;
        int alvo = (int) (distanciaGiro * COUNTS_PER_CM);

        resetarEncoders();

        FL.setTargetPosition(alvo);
        BL.setTargetPosition(alvo);
        FR.setTargetPosition(-alvo);
        BR.setTargetPosition(-alvo);

        colocarRunToPosition();
        aplicarPotencia(0.5);
        esperarMotores();
        pararMotores();
        voltarModoNormal();
    }

    //Shooter
    protected void ligarShooter(double potencia) {
        ShooterD.setPower(potencia);
    }
    protected void desligarShooter() {
        ShooterD.setPower(0);
    }

    //Roletes
    protected void ligarIntake(double potencia) {
        Intakes.setPower(potencia);
    }
    protected void desligarIntake() {
        Intakes.setPower(0);
    }
    protected void ligarHelpWheels(double potencia) {
        HelpWheels.setPower(potencia);
    }
    protected void desligarHelpWheels() {
        HelpWheels.setPower(0);
    }

    //Sleep
    protected void esperar(double segundos) {
        sleep((long) (segundos * 1000));
    }

    //Configurações
    protected void pararTudo() {
        pararMotores();
        desligarShooter();
        desligarIntake();
        desligarHelpWheels();
    }
    private void resetarEncoders() {
        FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    private void colocarRunToPosition() {
        FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    private void aplicarPotencia(double potencia) {
        FL.setPower(potencia);
        FR.setPower(potencia);
        BL.setPower(potencia);
        BR.setPower(potencia);
    }
    private void esperarMotores() {
        while (opModeIsActive() &&
                (FL.isBusy() || FR.isBusy() ||
                        BL.isBusy() || BR.isBusy())) {

            telemetry.addData("FL", FL.getCurrentPosition());
            telemetry.addData("FR", FR.getCurrentPosition());
            telemetry.addData("BL", BL.getCurrentPosition());
            telemetry.addData("BR", BR.getCurrentPosition());
            telemetry.update();
        }
    }
    private void pararMotores() {

        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }
    private void voltarModoNormal() {
        FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}