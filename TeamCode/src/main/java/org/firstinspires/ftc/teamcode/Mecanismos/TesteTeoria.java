package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "TesteTeoria")
public class TesteTeoria extends LinearOpMode {

    private DcMotor FL, FR, BL, BR;
    private DcMotor Shooter, Intakes;
    private GoBildaPinpointDriver pinpoint;

    private PIDController pidX;

    private static final double TOLERANCIA_DISTANCIA_CM = 0.5;
    private static final double TOLERANCIA_ANGULO_GRAUS = 1.0;

    @Override
    public void runOpMode() {

        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        BR = hardwareMap.get(DcMotor.class, "BR");

        Shooter = hardwareMap.get(DcMotor.class, "Shooter");
        Intakes = hardwareMap.get(DcMotor.class, "Intakes");

        BL.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.FORWARD);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "Pinpoint"
        );

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        pinpoint.setOffsets(
                0,
                50,
                DistanceUnit.MM
        );

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.setPosition(
                new Pose2D(
                        DistanceUnit.CM,
                        0,
                        5,
                        AngleUnit.DEGREES,
                        0
                )
        );

        pinpoint.update();

        pidX = new PIDController(
                0.03,
                0.0,
                0.002
        );

        telemetry.addLine("TesteTeoria pronto");
        telemetry.addData(
                "X",
                "%.2f cm",
                pinpoint.getPosX(DistanceUnit.CM)
        );
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            andarFrente(0.6, 50);
            parar();
        }
    }

    private void andarFrente(
            double potenciaMaxima,
            double distanciaEmCm
    ) {

        potenciaMaxima = Math.abs(potenciaMaxima);
        distanciaEmCm = Math.abs(distanciaEmCm);

        pinpoint.update();

        double xInicial =
                pinpoint.getPosX(DistanceUnit.CM);

        double alvoX =
                xInicial + distanciaEmCm;

        pidX.reset();

        while (opModeIsActive()) {

            pinpoint.update();

            double xAtual =
                    pinpoint.getPosX(DistanceUnit.CM);

            double erro =
                    alvoX - xAtual;

            double comando =
                    pidX.calcular(erro);

            comando = Math.max(
                    -potenciaMaxima,
                    Math.min(
                            potenciaMaxima,
                            comando
                    )
            );

            if (Math.abs(erro) <= TOLERANCIA_DISTANCIA_CM) {
                parar();
            } else {
                moverFrente(comando);
            }

            telemetry.addData("X", "%.2f cm", xAtual);
            telemetry.addData("Alvo", "%.2f cm", alvoX);
            telemetry.addData("Erro", "%.2f cm", erro);
            telemetry.addData("PID", "%.3f", comando);
            telemetry.update();
        }

        parar();
    }

    private void moverFrente(double potencia) {
        FL.setPower(potencia);
        FR.setPower(potencia);
        BL.setPower(potencia);
        BR.setPower(potencia);
    }

    private void moverTras(double potencia) {
        potencia = Math.abs(potencia);

        FL.setPower(-potencia);
        FR.setPower(-potencia);
        BL.setPower(-potencia);
        BR.setPower(-potencia);
    }

    private void parar() {
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }

    private void girarDireita(double potencia) {
        potencia = Math.abs(potencia);

        FL.setPower(potencia);
        BL.setPower(potencia);
        FR.setPower(-potencia);
        BR.setPower(-potencia);
    }

    private void girarEsquerda(double potencia) {
        potencia = Math.abs(potencia);

        FL.setPower(-potencia);
        BL.setPower(-potencia);
        FR.setPower(potencia);
        BR.setPower(potencia);
    }
}