package org.firstinspires.ftc.teamcode.BeeFrostAutonomo.Conf;

public class AutonomoConf {

    // ==========================================
    // NOMES DOS MOTORES
    // ==========================================

    public static final String FRONT_LEFT = "frontLeft";
    public static final String FRONT_RIGHT = "frontRight";
    public static final String BACK_LEFT = "backLeft";
    public static final String BACK_RIGHT = "backRight";


    // ==========================================
    // NOMES DOS ENCÓDERS
    // ==========================================

    public static final String ODO_PARALLEL = "odoParallel";
    public static final String ODO_PERPENDICULAR = "odoPerpendicular";


    // ==========================================
    // IMU
    // ==========================================

    public static final String IMU = "imu";


    // ==========================================
    // ODOMETRIA
    // ==========================================

    // ALTERE PARA O SEU ENCODER
    public static final double TICKS_PER_REV = 2000.0;

    // Diâmetro da roda da odometria em mm
    public static final double ODO_WHEEL_DIAMETER_MM = 35.0;

    // Direção dos encoders
    // Se algum estiver invertido, coloque -1
    public static final double PARALLEL_DIRECTION = 1.0;
    public static final double PERPENDICULAR_DIRECTION = 1.0;


    // ==========================================
    // PID X
    // ==========================================

    public static final double X_KP = 0.003;
    public static final double X_KI = 0.0;
    public static final double X_KD = 0.0002;


    // ==========================================
    // PID Y
    // ==========================================

    public static final double Y_KP = 0.003;
    public static final double Y_KI = 0.0;
    public static final double Y_KD = 0.0002;


    // ==========================================
    // PID HEADING
    // ==========================================

    public static final double HEADING_KP = 1.5;
    public static final double HEADING_KI = 0.0;
    public static final double HEADING_KD = 0.05;


    // ==========================================
    // TOLERÂNCIAS
    // ==========================================

    // Tolerância de posição em mm
    public static final double POSITION_TOLERANCE_MM = 25.0;

    // Tolerância angular em graus
    public static final double HEADING_TOLERANCE_DEG = 3.0;


    // ==========================================
    // LIMITES
    // ==========================================

    public static final double MAX_DRIVE_POWER = 1.0;
    public static final double MAX_TURN_POWER = 1.0;


    // ==========================================
    // CONSTRUTOR PRIVADO
    // ==========================================

    private AutonomoConf() {
    }
}