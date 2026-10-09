package org.firstinspires.ftc.teamcode.Mecanismos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/** Exemplo: origem no início; primeiro vai para frente, depois para a direita e gira. */
@Autonomous(name = "Mecanum GoTo Example", group = "Examples")
public class ExampleGoToAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        MecanumGoTo drive = new MecanumGoTo(this);
        telemetry.addLine("Confira nomes, direções, orientação do Hub e constantes antes de iniciar.");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;

        drive.resetPose(0.0, 0.0, 0.0);
        drive.goTo(0.0, 600.0, 0.0);   // 600 mm à frente
        drive.goTo(400.0, 600.0, 90.0); // 400 mm à direita e heading anti-horário 90°
        drive.goTo(0.0,0.0, 0.0);     // retorna à origem
        drive.stop();
    }
}
