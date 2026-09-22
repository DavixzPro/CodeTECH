package org.firstinspires.ftc.teamcode.StormBreakerAutonomo;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "SOS_Autonomo")
public class SOS_Autonomo extends AutonomoTest {

    @Override
    public void runOpMode() {
        configurarRobo();
        waitForStart();
        if (opModeIsActive()) {
            ligarShooter(0.9);
            andarTras(0.8, 100);
            esperar(3);
            ligarHelpWheels(1);
            ligarIntake(1);
            esperar(2);
            pararTudo();
        }
    }
}