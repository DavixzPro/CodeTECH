package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/** Localização planar: dois encoders de roda morta para X/Y e IMU para heading. */
public class TwoWheelLocalizer {
    public static final class Pose {
        public double xMm, yMm, headingRad;
        Pose(double x, double y, double h) { xMm = x; yMm = y; headingRad = h; }
    }

    private final DcMotorEx odoLong, odoLat;
    private final IMU imu;
    private final double mmPerTick;
    private int lastLong, lastLat;
    private double lastYaw, xMm, yMm, headingRad;

    public TwoWheelLocalizer(DcMotorEx odoLong, DcMotorEx odoLat, IMU imu) {
        this.odoLong = odoLong;
        this.odoLat = odoLat;
        this.imu = imu;
        mmPerTick = 2.0 * Math.PI * DriveConstants.ODO_WHEEL_RADIUS_MM
                * DriveConstants.ODO_GEAR_RATIO / DriveConstants.ODO_TICKS_PER_REV;
    }

    /** Chame depois de posicionar o robô no ponto inicial; define pose inicial (0,0,0). */
    public void resetPose(double x, double y, double headingDegrees) {
        lastLong = odoLong.getCurrentPosition();
        lastLat = odoLat.getCurrentPosition();
        imu.resetYaw();
        lastYaw = 0.0;
        xMm = x;
        yMm = y;
        headingRad = Math.toRadians(headingDegrees);
    }

    public Pose update() {
        int currentLong = odoLong.getCurrentPosition();
        int currentLat = odoLat.getCurrentPosition();
        double yaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        double dHeading = wrapRadians(yaw - lastYaw);
        double dLong = (currentLong - lastLong) * mmPerTick;
        double dLat = (currentLat - lastLat) * mmPerTick;
        lastLong = currentLong;
        lastLat = currentLat;
        lastYaw = yaw;

        // Compensa o deslocamento aparente das rodas causado pela rotação do robô.
        double forward = dLong - dHeading * DriveConstants.LONG_ODO_X_MM;
        double right = dLat + dHeading * DriveConstants.LAT_ODO_Y_MM;
        double midHeading = headingRad + dHeading * 0.5;

        // Eixos do campo: X para a direita, Y para a frente no heading 0.
        xMm += right * Math.cos(midHeading) - forward * Math.sin(midHeading);
        yMm += right * Math.sin(midHeading) + forward * Math.cos(midHeading);
        headingRad += dHeading;
        return new Pose(xMm, yMm, headingRad);
    }

    private static double wrapRadians(double angle) {
        while (angle > Math.PI) angle -= 2.0 * Math.PI;
        while (angle < -Math.PI) angle += 2.0 * Math.PI;
        return angle;
    }
}
