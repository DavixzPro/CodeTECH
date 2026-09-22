package org.firstinspires.ftc.teamcode.StormBreakerTeleOp;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "StormBreakerTeleOp")
public class StormBreakerTeleOp extends OpMode {

    DcMotor BL;
    DcMotor BR;
    DcMotor FL;
    DcMotor FR;
    //Servo GiroE;
    //Servo GiroD;
    Servo Angulo;
    DcMotor HelpWheels;
    //DcMotor ShooterE;
    DcMotorEx ShooterD;
    DcMotor Intakes;

    //CONFIGURAÇÕES DO BOTAO MAGICO
    private boolean ultimoTrigger = false;
    private boolean intakeRodando = false;
    private long inicioIntake = 0;

    //ESTADOS DO SHOOTER
    private boolean ultimoDpadDireito = false;
    private boolean ultimoBumperEsquerdo = false;
    private boolean ultimoBumperDireito = false;

    private boolean shooterLigado = true;
    private double potenciaShooter = 0;

    //IMU
    private IMU imu;

    // CONTROLE DE ROTAÇÃO
    private double velocidadeRotacaoAlvo = 0;

    @Override
    public void init() {
        //HARDWARE
        BL = hardwareMap.get(DcMotor.class, "BL");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        FR = hardwareMap.get(DcMotor.class, "FR");
        //ShooterE = hardwareMap.get(DcMotor.class, "ShooterE");
        ShooterD = hardwareMap.get(DcMotorEx.class, "ShooterD");
        //GiroE = hardwareMap.get(Servo.class, "GiroE");
        //GiroD = hardwareMap.get(Servo.class, "GiroD");
        Angulo = hardwareMap.get(Servo.class, "Angulo");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");
        HelpWheels = hardwareMap.get(DcMotor.class, "HelpWheels");

        // IMU
        imu = hardwareMap.get(IMU.class, "imu");

        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );

        imu.initialize(parameters);
        imu.resetYaw();

        //INTAKE CONFIGS
        Intakes.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intakes.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        HelpWheels.setDirection(DcMotor.Direction.REVERSE);
        HelpWheels.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //SHOOTER CONFIGS
        //ShooterE.setDirection(DcMotorSimple.Direction.FORWARD);
        //ShooterE.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //ShooterE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        ShooterD.setDirection(DcMotorSimple.Direction.FORWARD);
        ShooterD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ShooterD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        //DIRECTION
        FR.setDirection(DcMotorSimple.Direction.REVERSE);
        BL.setDirection(DcMotorSimple.Direction.REVERSE);


        //DRIVER HUB:
        //CONTROL HUB:
        //Motor intakes = 0
        //Motor ShooterD = 1
        //Motor BR = 2
        //Motor FR = 3

        //Servo Angulo = 0
        //Servo GiroD = 1


        //EXPANSION HUB:
        //Motor FL = 0
        //Motor BL = 2
        //Motor HelpWheels = 3

        //Servo GiroE = 0
    }

    @Override
    public void loop() {

        //////////////////////////MECANUM//////////////////////////////

        //RESET DA IMU
        if (gamepad1.start) {
            imu.resetYaw();
        }


        //MOVIMENTAÇÃO
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        // Deadzone
        if (Math.abs(x) < 0.05) x = 0;
        if (Math.abs(y) < 0.05) y = 0;

        // ROTAÇÃO COM IMU
        double rx = 0;

        if (gamepad1.left_bumper) {
            velocidadeRotacaoAlvo = -0.8;
        } else if (gamepad1.right_bumper) {
            velocidadeRotacaoAlvo = 0.8;
        } else {
            velocidadeRotacaoAlvo = 0;
        }

        double velocidadeAngular =
                imu.getRobotAngularVelocity(AngleUnit.RADIANS).zRotationRate;
        double erroRotacao =
                velocidadeRotacaoAlvo - velocidadeAngular;
        double ganhoRotacao = 0.35;

        rx = velocidadeRotacaoAlvo + (erroRotacao * ganhoRotacao);

        // Limita a potência de rotação
        rx = Math.max(-1, Math.min(1, rx));

        // Se não estiver apertando nenhum bumper,
        // não deixa a IMU gerar rotação sozinha.
        if (velocidadeRotacaoAlvo == 0) {
            rx = 0;
        }

        //MECANUM
        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;

        // Normalização
        double max = Math.max(
                Math.abs(fl),
                Math.max(
                        Math.abs(fr),
                        Math.max(Math.abs(bl), Math.abs(br))
                )
        );

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


        //////////////////////////SERVOS//////////////////////////////

        //if (gamepad2.left_bumper) {
        //GiroE.setPosition(1);
        //GiroD.setPosition(1);
        //} else if (gamepad2.right_bumper) {
        //GiroE.setPosition(0.1);
        //GiroD.setPosition(0.1);
        //} else {
        //GiroE.setPosition(0.5);
        //GiroD.setPosition(0.5);
        //}

        if (gamepad2.dpad_up) {
            Angulo.setPosition(0.1);
        } else if (gamepad2.dpad_down) {
            Angulo.setPosition(1);
        } else {
            Angulo.setPosition(0.5);
        }


        //////////////////////////INTAKES//////////////////////////////

        if (gamepad2.x) {
            Intakes.setPower(1);
        } else if (gamepad2.y) {
            Intakes.setPower(-1);
        } else {
            Intakes.setPower(0);
        }

        if (gamepad2.a) {
            HelpWheels.setPower(1);
        } else if (gamepad2.b) {
            HelpWheels.setPower(-1);
        } else {
            HelpWheels.setPower(0);
        }


        ///////////////////////BOTAO MAGICO///////////////////////////

        boolean triggerAtual = gamepad2.right_trigger > 0.5;

        if (triggerAtual && !ultimoTrigger && !intakeRodando) {
            intakeRodando = true;
            inicioIntake = System.currentTimeMillis();
        }

        ultimoTrigger = triggerAtual;

        if (intakeRodando) {
            long tempo = System.currentTimeMillis() - inicioIntake;

            if (tempo < 100) {
                Intakes.setPower(-1);
                HelpWheels.setPower(-1);
            } else if (tempo < 500) {
                Intakes.setPower(1);
                HelpWheels.setPower(1);
            } else {
                Intakes.setPower(0);
                HelpWheels.setPower(0);
                intakeRodando = false;
            }
        }


        //////////////////////////SHOOTER//////////////////////////////
        boolean dpadDireitoAtual = gamepad2.dpad_right;
        boolean bumperEsquerdoAtual = gamepad2.left_bumper;
        boolean bumperDireitoAtual = gamepad2.right_bumper;
        boolean dpadEsquerdoAtual = gamepad2.dpad_left;

        // DPAD DIREITO (Potência 0.8)
        if (dpadDireitoAtual && !ultimoDpadDireito) {
            // Se já estiver ligado E a potência for 0.8, DESLIGA.
            if (shooterLigado && potenciaShooter == 0.8) {
                shooterLigado = false;
            } else {
                // Caso contrário, LIGA (ou muda) para 0.8
                shooterLigado = true;
                potenciaShooter = 0.8;
            }
        }

        // BUMPER ESQUERDO (Potência 0.7)
        if (bumperEsquerdoAtual && !ultimoBumperEsquerdo) {
            if (shooterLigado && potenciaShooter == 0.7) {
                shooterLigado = false;
            } else {
                shooterLigado = true;
                potenciaShooter = 0.7;
            }
        }

        // BUMPER DIREITO (Potência 0.9)
        if (bumperDireitoAtual && !ultimoBumperDireito) {
            if (shooterLigado && potenciaShooter == 0.9) {
                shooterLigado = false;
            } else {
                shooterLigado = true;
                potenciaShooter = 0.9;
            }
        }

        // DPAD ESQUERDO (Botão de Pânico / Forçar Desligar)
        if (dpadEsquerdoAtual) {
            shooterLigado = false;
            ShooterD.setPower(-1);
        }

        // APLICAÇÃO DE ENERGIA AO MOTOR (Tem que ficar solto aqui no final)
        if (shooterLigado) {
            ShooterD.setPower(potenciaShooter);
        } else {
            ShooterD.setPower(0);
        }

        // ATUALIZAÇÃO DOS BOTÕES PARA O PRÓXIMO LOOP (Tem que ficar solto aqui no final)
        ultimoDpadDireito = dpadDireitoAtual;
        ultimoBumperEsquerdo = bumperEsquerdoAtual;
        ultimoBumperDireito = bumperDireitoAtual;

    }
}