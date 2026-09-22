package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Teste Pinpoint Sem Base")
public class TESTEPINPOITO extends LinearOpMode {

    private GoBildaPinpointDriver odo;

    @Override
    public void runOpMode() {

        // =====================================
        // PINPOINT
        // =====================================

        odo = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "Pinpoint"
        );

        // NÃO colocamos nenhum offset.
        // Começamos com a configuração padrão.

        odo.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        odo.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        // Zera posição e IMU
        odo.resetPosAndIMU();

        telemetry.addLine("TESTE PINPOINT");
        telemetry.addLine("");
        telemetry.addLine("Sem offsets definidos");
        telemetry.addLine("");
        telemetry.addLine("START = zerar");
        telemetry.addLine("Aguardando...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Atualiza o Pinpoint
            odo.update();

            // =====================================
            // POSIÇÃO
            // =====================================

            Pose2D pose = odo.getPosition();

            double x = pose.getX(
                    DistanceUnit.MM
            );

            double y = pose.getY(
                    DistanceUnit.MM
            );

            double heading = pose.getHeading(
                    AngleUnit.DEGREES
            );

            // =====================================
            // RESET
            // =====================================

            if (gamepad1.start) {

                odo.resetPosAndIMU();

            }

            // =====================================
            // TELEMETRIA
            // =====================================

            telemetry.addLine(
                    "========== PINPOINT =========="
            );

            telemetry.addData(
                    "X",
                    "%.2f mm",
                    x
            );

            telemetry.addData(
                    "Y",
                    "%.2f mm",
                    y
            );

            telemetry.addData(
                    "X",
                    "%.2f cm",
                    x / 10.0
            );

            telemetry.addData(
                    "Y",
                    "%.2f cm",
                    y / 10.0
            );

            telemetry.addData(
                    "Heading",
                    "%.2f graus",
                    heading
            );

            telemetry.addLine("");

            telemetry.addLine(
                    "Offsets: NAO DEFINIDOS"
            );

            telemetry.update();

            sleep(20);
        }
    }
}