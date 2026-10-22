package org.firstinspires.ftc.teamcode.BeefrostTeleOpBLUE;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "BeefrostTeleOp_BLUE")
public class BeefrostTeleOp_BLUE extends OpMode {

    private BeefrostBlueConfig robot;
    private BeefrostMovement_BLUE movement;
    private BeefrostMechanisms_BLUE mechanisms;

    @Override
    public void init() {

        robot = new BeefrostBlueConfig();
        robot.init(hardwareMap);
        movement = new BeefrostMovement_BLUE(robot, this);
        mechanisms = new BeefrostMechanisms_BLUE(robot, this);

        telemetry.addLine("========== BEEFROST ==========");
        telemetry.addLine("Inicializacao concluida");
        telemetry.update();
    }

    @Override
    public void start() {
        movement.iniciarLimelight();
        mechanisms.FlowerOff();
        mechanisms.PolenPosition();
    }

    @Override
    public void loop() {

        movement.atualizar();
        mechanisms.atualizar();
        atualizarTelemetria();
    }

    private void atualizarTelemetria() {

        telemetry.addLine("========== BEEFROST ==========");


        ////////////////////////////////// Shooter /////////////////////////////////////////


        String modo = robot.shooterMode ? "POLEN" : "NECTAR";
        String estado = robot.shooterLigado ? "LIGADO" : "DESLIGADO";
        String trava = robot.shooterTravado ? "TRAVADA" : "LIBERADA";

        double velocidadeAlvo = robot.shooterLigado
                ? (robot.shooterMode ? robot.polenVelocity : robot.nectarVelocity) : 0;

        telemetry.addData("SHOOTER", "%s | %s | Trava: %s", estado, modo, trava);

        telemetry.addData("Velocidade alvo", "%.0f ticks/s", velocidadeAlvo);


        ////////////////////////////////// Limelight ///////////////////////////////////////


        LLResult resultado = movement.getResultadoLimelight();

        String estadoLimelight;

        if (resultado == null) {
            estadoLimelight = "AGUARDANDO";

        } else if (movement.isTagDetectada()) {
            estadoLimelight = "TAG DETECTADA";

        } else if (resultado.isValid()) {
            estadoLimelight = "ALVO SEM TAG";

        } else {
            estadoLimelight = "SEM ALVO";
        }

        telemetry.addData("LIMELIGHT",
                "%s | Assist: %s", estadoLimelight, robot.seguirAprilTag ? "ON" : "OFF");

        if (movement.isTagDetectada()) {
            telemetry.addData(
                    "TX / TY",
                    "%.1f / %.1f",
                    resultado.getTx(),
                    resultado.getTy());
        }

        //////////////////////////////////// Bateria //////////////////////////////////////

        telemetry.addData("BATERIA", "%.2f V", robot.Battery.getVoltage());
        telemetry.addLine("==============================");
        telemetry.update();
    }

    @Override
    public void stop() {

        movement.parar();
        robot.Shooter.setPower(0);
        robot.Intakes.setPower(0);

    }
}
