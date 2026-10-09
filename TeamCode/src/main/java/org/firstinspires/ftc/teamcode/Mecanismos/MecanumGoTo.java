package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/** Inicialização de hardware e controlador goTo para uso em um LinearOpMode. */
public class MecanumGoTo {
    private final LinearOpMode opMode;
    private final DcMotorEx fl, fr, bl, br, odoLong, odoLat;
    private final IMU imu;
    private final TwoWheelLocalizer localizer;

    public MecanumGoTo(LinearOpMode opMode) {
        this.opMode = opMode;
        fl = opMode.hardwareMap.get(DcMotorEx.class, DriveConstants.FRONT_LEFT);
        fr = opMode.hardwareMap.get(DcMotorEx.class, DriveConstants.FRONT_RIGHT);
        bl = opMode.hardwareMap.get(DcMotorEx.class, DriveConstants.BACK_LEFT);
        br = opMode.hardwareMap.get(DcMotorEx.class, DriveConstants.BACK_RIGHT);
        odoLong = opMode.hardwareMap.get(DcMotorEx.class, DriveConstants.ODO_LONG);
        odoLat = opMode.hardwareMap.get(DcMotorEx.class, DriveConstants.ODO_LAT);
        imu = opMode.hardwareMap.get(IMU.class, DriveConstants.IMU);

        // Direções usuais; inverta individualmente se o seu chassi exigir.
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);
        for (DcMotorEx motor : new DcMotorEx[]{fl, fr, bl, br}) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        // Se os encoders forem rodas mortas independentes, deixe seus motores
        // mecanicamente livres. Não configure RUN_TO_POSITION neles.
        odoLong.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odoLat.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odoLong.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        odoLat.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        RevHubOrientationOnRobot.LogoFacingDirection logo = DriveConstants.LOGO_UP
                ? RevHubOrientationOnRobot.LogoFacingDirection.UP
                : RevHubOrientationOnRobot.LogoFacingDirection.DOWN;
        RevHubOrientationOnRobot.UsbFacingDirection usb = DriveConstants.USB_FORWARD
                ? RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                : RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(logo, usb)));
        localizer = new TwoWheelLocalizer(odoLong, odoLat, imu);
    }

    public void resetPose(double xMm, double yMm, double headingDegrees) {
        localizer.resetPose(xMm, yMm, headingDegrees);
    }

    /**
     * Vai até X/Y em milímetros e heading em graus. X positivo = direita,
     * Y positivo = frente quando heading é 0; heading positivo = anti-horário.
     * Retorna false se o tempo acabar ou o OpMode for interrompido.
     */
    public boolean goTo(double targetXmm, double targetYmm, double targetHeadingDeg) {
        double startTime = opMode.getRuntime();
        while (opMode.opModeIsActive()
                && opMode.getRuntime() - startTime < DriveConstants.TIMEOUT_SECONDS) {
            TwoWheelLocalizer.Pose pose = localizer.update();
            double errorX = targetXmm - pose.xMm;
            double errorY = targetYmm - pose.yMm;
            double distance = Math.hypot(errorX, errorY);
            double targetHeading = Math.toRadians(targetHeadingDeg);
            double headingError = wrapDegrees(targetHeadingDeg
                    - Math.toDegrees(pose.headingRad));

            opMode.telemetry.addData("X / Y (mm)", "%.0f / %.0f", pose.xMm, pose.yMm);
            opMode.telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(pose.headingRad));
            opMode.telemetry.addData("Erro (mm / deg)", "%.0f / %.1f", distance, headingError);
            opMode.telemetry.update();

            if (distance <= DriveConstants.POSITION_TOLERANCE_MM
                    && Math.abs(headingError) <= DriveConstants.HEADING_TOLERANCE_DEG) {
                stop();
                return true;
            }

            // Erro de campo -> componentes direita/frente no referencial do robô.
            double h = pose.headingRad;
            double rightField = errorX * Math.cos(h) + errorY * Math.sin(h);
            double forwardField = -errorX * Math.sin(h) + errorY * Math.cos(h);
            double right = distance < 1e-6 ? 0.0 : rightField / distance;
            double forward = distance < 1e-6 ? 0.0 : forwardField / distance;
            double translation = clip(distance * DriveConstants.TRANSLATION_KP_PER_MM,
                    0.0, DriveConstants.MAX_TRANSLATION_POWER);
            if (distance > DriveConstants.POSITION_TOLERANCE_MM)
                translation = Math.max(DriveConstants.MIN_POWER, translation);
            double strafe = right * translation;
            double drive = forward * translation;
            double turn = clip(Math.toRadians(headingError) * DriveConstants.HEADING_KP_PER_DEG
                            * (180.0 / Math.PI),
                    -DriveConstants.MAX_TURN_POWER, DriveConstants.MAX_TURN_POWER);
            if (Math.abs(headingError) > DriveConstants.HEADING_TOLERANCE_DEG
                    && Math.abs(turn) < DriveConstants.MIN_POWER)
                turn = Math.copySign(DriveConstants.MIN_POWER, turn);

            setMecanum(strafe, drive, turn);
            opMode.idle();
        }
        stop();
        return false;
    }

    /** Mistura clássica mecanum; X = strafe direita, Y = avançar, turn = anti-horário. */
    private void setMecanum(double x, double y, double turn) {
        double flPower = y + x + turn;
        double frPower = y - x - turn;
        double blPower = y - x + turn;
        double brPower = y + x - turn;
        double scale = Math.max(1.0, Math.max(Math.max(Math.abs(flPower), Math.abs(frPower)),
                Math.max(Math.abs(blPower), Math.abs(brPower))));
        fl.setPower(flPower / scale);
        fr.setPower(frPower / scale);
        bl.setPower(blPower / scale);
        br.setPower(brPower / scale);
    }

    public void stop() {
        fl.setPower(0); fr.setPower(0); bl.setPower(0); br.setPower(0);
    }

    private static double wrapDegrees(double degrees) {
        while (degrees > 180.0) degrees -= 360.0;
        while (degrees < -180.0) degrees += 360.0;
        return degrees;
    }

    private static double clip(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
