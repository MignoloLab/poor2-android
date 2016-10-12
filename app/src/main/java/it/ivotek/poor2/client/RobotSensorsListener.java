package it.ivotek.poor2.client;


/**
 * Listener per i dati dei sensori del robot.
 * @author Daniele Ricci
 */
public interface RobotSensorsListener {

    void onSensorsData(RobotSensorsData data);

}
