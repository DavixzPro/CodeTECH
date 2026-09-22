package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "OdometryTestDAVI")
public class OdometryTestDAVI extends LinearOpMode {

    // =========================
    // MOTORES
    // =========================

    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private GoBildaPinpointDriver pinpoint;

    //diametro
    private static final double WHEEL_DIAMETER_CM = 10.4;

    //configs do motor
    private static final double MOTOR_TICKS = 28.0;
    private static final double REDUCTION = 20.0;


    //configs
    private static final double POSITION_TOLERANCE_CM = 2.0;
    private static final double MAX_POWER = 0.6;
    private static final double MIN_POWER = 0.15;

    private static final double KP = 0.015;
    private static final double COMMAND_DISTANCE_CM = 8.0;

    @Override
    public void runOpMode() {

        FL = hardwareMap.get(DcMotorEx.class, "FL");
        FR = hardwareMap.get(DcMotorEx.class, "FR");
        BL = hardwareMap.get(DcMotorEx.class, "BL");
        BR = hardwareMap.get(DcMotorEx.class, "BR");

        FL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.FORWARD);

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "Pinpoint");

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);


        /*
         * ATENÇÃO:
         *
         * ESTES OFFSETS SÃO EXEMPLOS.
         *
         * NÃO copie esses números para o robô de vocês.
         *
         * Quando as pods estiverem instaladas,
         * vamos descobrir os offsets corretos.
         */
        pinpoint.setOffsets(
                0,
                0,
                DistanceUnit.MM
        );


        /*
         * Direção dos encoders das pods.
         *
         * Também vamos confirmar isso fisicamente
         * quando as pods estiverem instaladas.
         */
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );


        /*
         * Começamos a arena em:
         *
         * X = 0
         * Y = 0
         * Heading = 0
         */
        pinpoint.setPosition(
                new Pose2D(
                        DistanceUnit.CM,
                        0,
                        0,
                        org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES,
                        0
                )
        );


        telemetry.addLine("PINPOINT OK");
        telemetry.addLine("Auto XY pronto");
        telemetry.addLine("");
        telemetry.addLine("START para iniciar");
        telemetry.update();


        waitForStart();


        if (isStopRequested()) {
            return;
        }

        irPara(100, 50);
        sleep(500);
        irPara(100, 100);
        sleep(500);
        irPara(0, 0);
        parar();
    }


    private void irPara(double alvoX, double alvoY) {

        while (opModeIsActive()) {
            pinpoint.update();

            double xAtual =
                    pinpoint.getPosX(DistanceUnit.CM);

            double yAtual =
                    pinpoint.getPosY(DistanceUnit.CM);

            double heading =
                    pinpoint.getHeading(
                            org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS
                    );

            double erroX = alvoX - xAtual;
            double erroY = alvoY - yAtual;

            double distancia = Math.sqrt(erroX * erroX + erroY * erroY);


            if (distancia <= POSITION_TOLERANCE_CM) {
                parar();
                telemetry.addLine("CHEGOU!");
                telemetry.addData("X", xAtual);
                telemetry.addData("Y", yAtual);
                telemetry.addData("Alvo X", alvoX);
                telemetry.addData("Alvo Y", alvoY);
                telemetry.addData("Heading", Math.toDegrees(heading));
                telemetry.update();

                break;
            }

            double campoX = erroX / distancia;
            double campoY = erroY / distancia;

            double robotX =
                    campoX * Math.cos(heading)
                            + campoY * Math.sin(heading);

            double robotY =
                    -campoX * Math.sin(heading)
                            + campoY * Math.cos(heading);

            double power =
                    distancia * KP;

            power = Math.min(power, MAX_POWER);

            if (power < MIN_POWER) {
                power = MIN_POWER;
            }

            double forward =
                    robotX * power;

            double strafe =
                    robotY * power;

            //andar de forma linear, sem o robo girar ainda
            double fl =
                    forward + strafe;

            double fr =
                    forward - strafe;

            double bl =
                    forward - strafe;

            double br =
                    forward + strafe;


            double maior =
                    Math.max(
                            1.0,
                            Math.max(
                                    Math.abs(fl),
                                    Math.max(
                                            Math.abs(fr),
                                            Math.max(
                                                    Math.abs(bl),
                                                    Math.abs(br)
                                            )
                                    )
                            )
                    );

            fl /= maior;
            fr /= maior;
            bl /= maior;
            br /= maior;


            double distanciaComando =
                    COMMAND_DISTANCE_CM * power;

            int ticks =
                    cmParaTicks(distanciaComando);

            int alvoFL =
                    FL.getCurrentPosition()
                            + (int) (fl * ticks);

            int alvoFR =
                    FR.getCurrentPosition()
                            + (int) (fr * ticks);

            int alvoBL =
                    BL.getCurrentPosition()
                            + (int) (bl * ticks);

            int alvoBR =
                    BR.getCurrentPosition()
                            + (int) (br * ticks);


            FL.setTargetPosition(alvoFL);
            FR.setTargetPosition(alvoFR);
            BL.setTargetPosition(alvoBL);
            BR.setTargetPosition(alvoBR);


            FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            FL.setPower(Math.abs(fl));
            FR.setPower(Math.abs(fr));
            BL.setPower(Math.abs(bl));
            BR.setPower(Math.abs(br));
        }

        parar();
        colocarRunUsingEncoder();
    }

    private int cmParaTicks(double cm) {

        double circunferencia =
                Math.PI * WHEEL_DIAMETER_CM;

        double ticksPorVolta =
                MOTOR_TICKS * REDUCTION;

        double ticksPorCm =
                ticksPorVolta /
                        circunferencia;

        return (int) (cm * ticksPorCm);
    }

    private void parar() {
        FL.setPower(0);
        FR.setPower(0);
        BL.setPower(0);
        BR.setPower(0);
    }
    private void colocarRunUsingEncoder() {
        FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}