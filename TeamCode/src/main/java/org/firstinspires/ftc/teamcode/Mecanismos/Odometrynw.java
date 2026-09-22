package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "OdometryNW")
public class Odometrynw extends OpMode {

    // =========================================================
    // MOTORES DE TRAÇÃO
    // =========================================================

    DcMotor BL;
    DcMotor FL;
    DcMotor BR;
    DcMotor FR;


    // =========================================================
    // PINPOINT
    // =========================================================

    private GoBildaPinpointDriver pinpoint;


    // =========================================================
    // IMU
    // =========================================================


    // =========================================================
    // POSIÇÃO DO ROBÔ
    // =========================================================

    double posX = 0;
    double posY = 0;

    double heading = 0;


    // =========================================================
    // POSIÇÃO ALVO
    // =========================================================

    double targetX = 0;
    double targetY = 0;
    double targetHeading = 0;


    // =========================================================
    // PID DE POSIÇÃO
    // =========================================================

    static final double KP_X = 0.002;
    static final double KP_Y = 0.002;
    static final double KP_HEADING = 1.5;


    // =========================================================
    // LIMITES
    // =========================================================

    static final double MAX_CORRECTION_POWER = 0.5;

    static final double POSITION_TOLERANCE = 20; // mm
    static final double HEADING_TOLERANCE = 3;   // graus


    // =========================================================
    // CONTROLE DA CORREÇÃO
    // =========================================================

    boolean correctionEnabled = false;


    // =========================================================
    // INIT
    // =========================================================

    @Override
    public void init() {

        // =====================================================
        // MOTORES
        // =====================================================

        BL = hardwareMap.get(DcMotor.class, "BL");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BR = hardwareMap.get(DcMotor.class, "BR");
        FR = hardwareMap.get(DcMotor.class, "FR");


        // =====================================================
        // PINPOINT
        // =====================================================

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "Pinpoint"
        );


        // =====================================================
        // CONFIGURAÇÃO DO PINPOINT
        // =====================================================

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        pinpoint.setOffsets(
                100, 100, DistanceUnit.MM);


        // =====================================================
        // RESET DA POSIÇÃO
        // =====================================================

        pinpoint.resetPosAndIMU();


        // =====================================================
        // IMU
        // =====================================================




        // =====================================================
        // DIREÇÃO DOS MOTORES
        // =====================================================

        BL.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);


        // =====================================================
        // POSIÇÃO INICIAL
        // =====================================================

        posX = 0;
        posY = 0;
        heading = 0;

        targetX = 0;
        targetY = 0;
        targetHeading = 0;


        telemetry.addLine("Pinpoint inicializado!");
        telemetry.update();
    }


    // =========================================================
    // LOOP
    // =========================================================

    @Override
    public void loop() {

        // =====================================================
        // ATUALIZA PINPOINT
        // =====================================================

        pinpoint.update();


        // =====================================================
        // LÊ POSIÇÃO DO PINPOINT
        // =====================================================

        posX = pinpoint.getPosX(DistanceUnit.MM);
        posY = pinpoint.getPosY(DistanceUnit.MM);

        heading = pinpoint.getHeading(AngleUnit.RADIANS);


        // =====================================================
        // RESET
        // =====================================================

        if (gamepad1.start) {

            pinpoint.resetPosAndIMU();

            posX = 0;
            posY = 0;
            heading = 0;

            targetX = 0;
            targetY = 0;
            targetHeading = 0;
        }


        // =====================================================
        // MOVIMENTAÇÃO MANUAL
        // =====================================================

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        double rx = -gamepad1.right_stick_x;


        // =====================================================
        // DEADZONE
        // =====================================================

        if (Math.abs(x) < 0.05)
            x = 0;

        if (Math.abs(y) < 0.05)
            y = 0;

        if (Math.abs(rx) < 0.05)
            rx = 0;


        // =====================================================
        // CORREÇÃO AUTOMÁTICA
        // =====================================================

        if (correctionEnabled) {

            double[] correction =
                    calculateCorrection();

            x = correction[0];
            y = correction[1];
            rx = correction[2];
        }


        // =====================================================
        // MECANUM
        // =====================================================

        double fl = y + x + rx;
        double fr = y - x - rx;
        double bl = y - x + rx;
        double br = y + x - rx;


        // =====================================================
        // NORMALIZAÇÃO
        // =====================================================

        double max = Math.max(
                Math.abs(fl),
                Math.max(
                        Math.abs(fr),
                        Math.max(
                                Math.abs(bl),
                                Math.abs(br)
                        )
                )
        );


        if (max > 1) {

            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }


        // =====================================================
        // POTÊNCIA
        // =====================================================

        FL.setPower(fl);
        FR.setPower(fr);
        BL.setPower(bl);
        BR.setPower(br);


        // =====================================================
        // TELEMETRIA
        // =====================================================

        telemetry.addLine("===== PINPOINT ODOMETRY =====");

        telemetry.addData(
                "X",
                "%.1f mm",
                posX
        );

        telemetry.addData(
                "Y",
                "%.1f mm",
                posY
        );

        telemetry.addData(
                "Heading",
                "%.1f graus",
                Math.toDegrees(heading)
        );


        telemetry.addLine();

        telemetry.addLine("===== ALVO =====");

        telemetry.addData(
                "Target X",
                "%.1f mm",
                targetX
        );

        telemetry.addData(
                "Target Y",
                "%.1f mm",
                targetY
        );

        telemetry.addData(
                "Target Heading",
                "%.1f graus",
                Math.toDegrees(targetHeading)
        );


        telemetry.addLine();

        telemetry.addData(
                "Correção",
                correctionEnabled
                        ? "ATIVADA"
                        : "DESATIVADA"
        );


        telemetry.addData(
                "AtTarget",
                atTarget()
        );


        telemetry.update();
    }


    // =========================================================
    // CALCULA CORREÇÃO
    // =========================================================

    private double[] calculateCorrection() {

        // -----------------------------------------------------
        // ERRO NO CAMPO
        // -----------------------------------------------------

        double errorX =
                targetX - posX;

        double errorY =
                targetY - posY;


        // -----------------------------------------------------
        // CAMPO → ROBÔ
        // -----------------------------------------------------

        double robotX =
                errorX * Math.cos(heading)
                        + errorY * Math.sin(heading);


        double robotY =
                -errorX * Math.sin(heading)
                        + errorY * Math.cos(heading);


        // -----------------------------------------------------
        // POTÊNCIA
        // -----------------------------------------------------

        double xPower =
                robotX * KP_X;

        double yPower =
                robotY * KP_Y;


        // -----------------------------------------------------
        // ERRO DE HEADING
        // -----------------------------------------------------

        double headingError =
                targetHeading - heading;


        // -----------------------------------------------------
        // NORMALIZA HEADING
        // -----------------------------------------------------

        while (headingError > Math.PI) {

            headingError -= 2 * Math.PI;
        }


        while (headingError < -Math.PI) {

            headingError += 2 * Math.PI;
        }


        // -----------------------------------------------------
        // CORREÇÃO DE ROTAÇÃO
        // -----------------------------------------------------

        double rotationPower =
                headingError * KP_HEADING;


        // -----------------------------------------------------
        // LIMITA POTÊNCIA
        // -----------------------------------------------------

        xPower = clip(
                xPower,
                -MAX_CORRECTION_POWER,
                MAX_CORRECTION_POWER
        );


        yPower = clip(
                yPower,
                -MAX_CORRECTION_POWER,
                MAX_CORRECTION_POWER
        );


        rotationPower = clip(
                rotationPower,
                -MAX_CORRECTION_POWER,
                MAX_CORRECTION_POWER
        );


        return new double[]{
                xPower,
                yPower,
                rotationPower
        };
    }


    // =========================================================
    // DEFINIR POSIÇÃO ALVO
    // =========================================================

    public void setTarget(
            double x,
            double y,
            double headingDegrees
    ) {

        targetX = x;
        targetY = y;

        targetHeading =
                Math.toRadians(headingDegrees);
    }


    // =========================================================
    // VERIFICA SE CHEGOU
    // =========================================================

    public boolean atTarget() {

        double errorX =
                targetX - posX;

        double errorY =
                targetY - posY;


        double distance =
                Math.sqrt(
                        errorX * errorX
                                + errorY * errorY
                );


        double headingError =
                targetHeading - heading;


        while (headingError > Math.PI) {

            headingError -= 2 * Math.PI;
        }


        while (headingError < -Math.PI) {

            headingError += 2 * Math.PI;
        }


        double headingErrorDegrees =
                Math.abs(
                        Math.toDegrees(headingError)
                );


        return distance < POSITION_TOLERANCE
                && headingErrorDegrees < HEADING_TOLERANCE;
    }


    // =========================================================
    // CLIP
    // =========================================================

    private double clip(
            double value,
            double min,
            double max
    ) {

        if (value > max)
            return max;

        if (value < min)
            return min;

        return value;
    }
}