package org.firstinspires.ftc.teamcode.BeeFrostAutonomo.Conf;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Starter autonomous: rotate a mecanum robot until a selected Limelight AprilTag
 * is centered, then shoot one preloaded game piece. Adapt hardware names and
 * shooter/feeder values to your robot before enabling this OpMode.
 */
@Autonomous(name = "CodeTECH: Limelight Align + Shoot", group = "CodeTECH")
public class LimelightAlignAndShootAuto extends LinearOpMode {
    // Match these names to the Robot Configuration on the Driver Station.
    private static final String FRONT_LEFT_NAME = "FL";
    private static final String BACK_LEFT_NAME = "BL";
    private static final String FRONT_RIGHT_NAME = "FR";
    private static final String BACK_RIGHT_NAME = "BR";
    private static final String SHOOTER_NAME = "shooter";
    private static final String FEEDER_NAME = "feeder";

    // Change these to the pipeline and field target used by your season/game.
    private static final int APRILTAG_PIPELINE = 0;
    private static final int TARGET_TAG_ID = -1; // -1 accepts any visible AprilTag.

    // Tune on the actual robot. tx is horizontal angular error in degrees.
    private static final double TX_TOLERANCE_DEG = 1.5;
    private static final double TURN_KP = 0.018;
    private static final double MAX_TURN_POWER = 0.25;
    private static final int REQUIRED_STABLE_FRAMES = 5;
    private static final long ALIGN_TIMEOUT_MS = 5000;
    private static final long MAX_RESULT_AGE_MS = 500;

    // Example values only: DcMotorEx velocity uses encoder ticks per second;
    // servo positions are [0, 1]. Calibrate both for your shooter/feed system.
    private static final double SHOOTER_TICKS_PER_SECOND = 1500;
    private static final double FEED_RETRACTED = 0.15;
    private static final double FEED_EXTENDED = 0.55;
    private static final long SPINUP_MS = 1200;
    private static final long FEED_MS = 450;

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotorEx shooter;
    private Servo feeder;
    private Limelight3A limelight;
    private static final String LIMELIGHT_NAME = "limelight";
    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, FRONT_LEFT_NAME);
        frontRight = hardwareMap.get(DcMotor.class, FRONT_RIGHT_NAME);
        backLeft = hardwareMap.get(DcMotor.class, BACK_LEFT_NAME);
        backRight = hardwareMap.get(DcMotor.class, BACK_RIGHT_NAME);
        shooter = hardwareMap.get(DcMotorEx.class, SHOOTER_NAME);
        feeder = hardwareMap.get(Servo.class, FEEDER_NAME);
        limelight = hardwareMap.get(Limelight3A.class, LIMELIGHT_NAME);

        // Adjust motor directions to your wiring; these are common mecanum defaults.
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        stopDrive();
        feeder.setPosition(FEED_RETRACTED);

        limelight.pipelineSwitch(APRILTAG_PIPELINE);
        limelight.start();

        telemetry.addLine("Pronto. Confirme a área livre e pressione PLAY.");
        telemetry.addData("Limelight pipeline", APRILTAG_PIPELINE);
        telemetry.addData("Target ID", TARGET_TAG_ID == -1 ? "qualquer AprilTag" : TARGET_TAG_ID);
        telemetry.update();
        waitForStart();

        try {
            if (opModeIsActive() && alignToTag()) {
                stopDrive();
                if (opModeIsActive()) shootOne();
            }
        } finally {
            stopDrive();
            shooter.setPower(0);
            feeder.setPosition(FEED_RETRACTED);
            limelight.stop();
        }
    }

    private boolean alignToTag() {
        long deadline = System.currentTimeMillis() + ALIGN_TIMEOUT_MS;
        int stableFrames = 0;
        double lastTx = Double.NaN;

        while (opModeIsActive() && System.currentTimeMillis() < deadline) {
            LLResult result = limelight.getLatestResult();
            LLResultTypes.FiducialResult target = findTarget(result);
            boolean fresh = result != null
                    && result.isValid()
                    && result.getStaleness() <= MAX_RESULT_AGE_MS;

            if (!fresh || target == null) {
                stopDrive();
                stableFrames = 0;
                telemetry.addLine("Sem AprilTag válido/recente; robô parado.");
            } else {
                // tx > 0 means target is to the right. This sign assumes positive
                // turnPower rotates the robot clockwise; verify with wheels raised.
                double tx = target.getTargetXDegrees();
                double turnPower = clip(TURN_KP * tx, -MAX_TURN_POWER, MAX_TURN_POWER);
                if (Math.abs(tx) <= TX_TOLERANCE_DEG) {
                    stopDrive();
                    stableFrames++;
                } else {
                    stableFrames = 0;
                    turnInPlace(turnPower);
                }

                telemetry.addData("Tag ID", target.getFiducialId());
                telemetry.addData("tx (deg)", "%.2f", tx);
                telemetry.addData("turn", "%.2f", turnPower);
                telemetry.addData("stable frames", stableFrames);
                lastTx = tx;
            }

            telemetry.addData("alignment timeout in ms", Math.max(0, deadline - System.currentTimeMillis()));
            telemetry.addData("last tx", Double.isNaN(lastTx) ? "n/a" : String.format("%.2f", lastTx));
            telemetry.update();

            if (stableFrames >= REQUIRED_STABLE_FRAMES) return true;
            sleep(20);
        }

        stopDrive();
        telemetry.addLine("Alinhamento não confirmado; disparo cancelado.");
        telemetry.update();
        return false;
    }

    private LLResultTypes.FiducialResult findTarget(LLResult result) {
        if (result == null || !result.isValid()) return null;
        for (LLResultTypes.FiducialResult candidate : result.getFiducialResults()) {
            if (TARGET_TAG_ID == -1 || candidate.getFiducialId() == TARGET_TAG_ID) {
                return candidate;
            }
        }
        return null;
    }

    private void turnInPlace(double power) {
        // Mecanum yaw: left wheels one way, right wheels the other.
        frontLeft.setPower(-power);
        backLeft.setPower(-power);
        frontRight.setPower(power);
        backRight.setPower(power);
    }

    private void stopDrive() {
        if (frontLeft != null) frontLeft.setPower(0);
        if (frontRight != null) frontRight.setPower(0);
        if (backLeft != null) backLeft.setPower(0);
        if (backRight != null) backRight.setPower(0);
    }

    private void shootOne() {
        shooter.setVelocity(SHOOTER_TICKS_PER_SECOND);
        telemetry.addData("Shooter", "spinning at %.0f ticks/s", SHOOTER_TICKS_PER_SECOND);
        telemetry.update();
        sleep(SPINUP_MS);
        if (!opModeIsActive()) return;

        feeder.setPosition(FEED_EXTENDED);
        sleep(FEED_MS);
        feeder.setPosition(FEED_RETRACTED);
        sleep(250);
        shooter.setPower(0);
    }

    private static double clip(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
