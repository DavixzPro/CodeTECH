package org.firstinspires.ftc.teamcode.Mecanismos;

/** Ajuste estes valores para o seu robô. Distâncias em milímetros. */
public final class DriveConstants {
    private DriveConstants() { }

    // Nomes exatamente como aparecem na configuração do Robot Controller.
    public static final String FRONT_LEFT = "frontLeft";
    public static final String FRONT_RIGHT = "frontRight";
    public static final String BACK_LEFT = "backLeft";
    public static final String BACK_RIGHT = "backRight";
    public static final String ODO_LONG = "odoLong";   // roda mede avanço/recuo
    public static final String ODO_LAT = "odoLat";     // roda mede direita/esquerda
    public static final String IMU = "imu";

    // Encoder counts por volta do eixo, raio efetivo da roda e redução encoder:eixo.
    // Ex.: encoder no eixo do motor com redução 1:1 -> GEAR_RATIO = 1.0.
    public static final double ODO_TICKS_PER_REV = 8192.0;
    public static final double ODO_WHEEL_RADIUS_MM = 24.0;
    public static final double ODO_GEAR_RATIO = 1.0;

    // Coordenadas dos centros das rodas odométricas relativas ao centro do robô.
    // X positivo para a direita; Y positivo para a frente. As rodas devem ser
    // instaladas de modo que a longitudinal meça Y e a lateral meça X.
    public static final double LONG_ODO_X_MM = 0.0;
    public static final double LAT_ODO_Y_MM = 0.0;

    // Orientação física do Hub. Corrija para a montagem real antes de rodar.
    public static final boolean LOGO_UP = true;
    public static final boolean USB_FORWARD = true;

    public static final double POSITION_TOLERANCE_MM = 25.0;
    public static final double HEADING_TOLERANCE_DEG = 2.0;
    public static final double MAX_TRANSLATION_POWER = 0.65;
    public static final double MAX_TURN_POWER = 0.45;
    public static final double TRANSLATION_KP_PER_MM = 0.003;
    public static final double HEADING_KP_PER_DEG = 0.012;
    public static final double MIN_POWER = 0.08;
    public static final double TIMEOUT_SECONDS = 5.0;
}
