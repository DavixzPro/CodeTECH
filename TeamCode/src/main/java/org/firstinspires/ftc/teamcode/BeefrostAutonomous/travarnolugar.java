package org.firstinspires.ftc.teamcode.BeefrostAutonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;

@TeleOp(name = "malcriado")
public class travarnolugar extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private GoBildaPinpointDriver pinpoint;

    // Ganhos PID: ajustar durante os testes.
    private double kP_X = 0.018;
    private double kP_Y = 0.018;
    private double kP_Heading = 0.012;

    // Limites de potência do controlador.
    private double maxTranslationPower = 0.45;
    private double maxRotationPower = 0.30;

    // Posição-alvo.
    private double targetX;
    private double targetY;
    private double targetHeading;

    private boolean holdEnabled = false;
    private boolean previousButtonState = false;

    @Override
    public void runOpMode() {

        // Inicialização dos motores.
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");

        // Ajustar conforme a montagem real do chassi.
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        // Inicialização do Pinpoint.
        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class, "pinpoint");


        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        pinpoint.setOffsets(165, 40, DistanceUnit.MM);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.setPosition(new Pose2D(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0));
        pinpoint.update();
        pinpoint.resetPosAndIMU();

        telemetry.addLine("Position Hold inicializado");
        telemetry.addLine("A = ativar/desativar Position Hold");
        telemetry.addLine("B = zerar referencia de posicao");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Atualiza os dados do sistema de odometria.
            pinpoint.update();

            double x = pinpoint.getPosX(DistanceUnit.MM);
            double y = pinpoint.getPosY(DistanceUnit.MM);
            double heading = pinpoint.getHeading(AngleUnit.RADIANS);

            // Alterna o sistema com o botão A.
            boolean currentButtonState = gamepad1.a;

            if (currentButtonState && !previousButtonState) {

                holdEnabled = !holdEnabled;

                if (holdEnabled) {
                    // Salva a posição atual como posição fixa.
                    targetX = x;
                    targetY = y;
                    targetHeading = heading;
                }
            }

            previousButtonState = currentButtonState;

            // Reinicia a referência com o botão B.
            if (gamepad1.b) {
                targetX = x;
                targetY = y;
                targetHeading = heading;
            }

            if (holdEnabled) {

                // Erro de posição no referencial global.
                double errorX = targetX - x;
                double errorY = targetY - y;

                // Menor diferença angular entre alvo e posição atual.
                double errorHeading = normalizeAngle(
                        targetHeading - heading);

                // Controladores proporcionais iniciais.
                double correctionX = clip(
                        kP_X * errorX,
                        -maxTranslationPower,
                        maxTranslationPower);

                double correctionY = clip(
                        kP_Y * errorY,
                        -maxTranslationPower,
                        maxTranslationPower);

                double correctionHeading = clip(
                        kP_Heading * errorHeading,
                        -maxRotationPower,
                        maxRotationPower);

                /*
                 * Transforma as correções do referencial global
                 * para o referencial do robô.
                 */
                double cos = Math.cos(heading);
                double sin = Math.sin(heading);

                double robotX =
                        correctionX * cos + correctionY * sin;

                double robotY =
                        -correctionX * sin + correctionY * cos;

                // Mistura mecanum: X, Y e rotação.
                double fl = robotY + robotX + correctionHeading;
                double fr = robotY - robotX - correctionHeading;
                double bl = robotY - robotX + correctionHeading;
                double br = robotY + robotX - correctionHeading;

                // Normalização para manter a potência entre -1 e 1.
                double max = Math.max(
                        1.0,
                        Math.max(
                                Math.max(Math.abs(fl), Math.abs(fr)),
                                Math.max(Math.abs(bl), Math.abs(br))));

                frontLeft.setPower(fl / max);
                frontRight.setPower(fr / max);
                backLeft.setPower(bl / max);
                backRight.setPower(br / max);

            } else {
                // Modo manual mecanum.
                double yDrive = -gamepad1.left_stick_y;
                double xDrive = gamepad1.left_stick_x;
                double rotation = gamepad1.right_stick_x;

                double fl = yDrive + xDrive + rotation;
                double fr = yDrive - xDrive - rotation;
                double bl = yDrive - xDrive + rotation;
                double br = yDrive + xDrive - rotation;

                double max = Math.max(
                        1.0,
                        Math.max(
                                Math.max(Math.abs(fl), Math.abs(fr)),
                                Math.max(Math.abs(bl), Math.abs(br))));

                frontLeft.setPower(fl / max);
                frontRight.setPower(fr / max);
                backLeft.setPower(bl / max);
                backRight.setPower(br / max);
            }

            telemetry.addData("Position Hold", holdEnabled);
            telemetry.addData("X atual (mm)", x);
            telemetry.addData("Y atual (mm)", y);
            telemetry.addData("Heading atual (graus)",
                    Math.toDegrees(heading));

            telemetry.addData("X alvo (mm)", targetX);
            telemetry.addData("Y alvo (mm)", targetY);
            telemetry.addData("Heading alvo (graus)",
                    Math.toDegrees(targetHeading));

            telemetry.update();
        }
    }

    private double normalizeAngle(double angle) {
        while (angle > Math.PI) {
            angle -= 2.0 * Math.PI;
        }

        while (angle < -Math.PI) {
            angle += 2.0 * Math.PI;
        }

        return angle;
    }

    private double clip(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

