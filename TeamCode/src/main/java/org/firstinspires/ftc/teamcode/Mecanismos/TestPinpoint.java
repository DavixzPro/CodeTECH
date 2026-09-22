package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Autonomous(name = "TestPinpoint")
public class TestPinpoint extends LinearOpMode {

    private GoBildaPinpointDriver pinpoint;

    @Override
    public void runOpMode() {

        pinpoint = hardwareMap.get(
                GoBildaPinpointDriver.class,
                "Pinpoint"
        );

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        pinpoint.setOffsets(
                0,
                0,
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
                        0,
                        AngleUnit.DEGREES,
                        0
                )
        );

        telemetry.addLine("Pinpoint configurado");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            pinpoint.update();

            double x = pinpoint.getPosX(DistanceUnit.CM);
            double y = pinpoint.getPosY(DistanceUnit.CM);
            double heading = pinpoint.getHeading(AngleUnit.DEGREES);

            telemetry.addData("X", "%.2f cm", x);
            telemetry.addData("Y", "%.2f cm", y);
            telemetry.addData("Heading", "%.2f graus", heading);
            telemetry.update();

            sleep(50);
        }
    }
}