package it.ivotek.poor2.client;


/**
 * Listener per le connessioni al robot.
 * @author Daniele Ricci
 */
public interface RobotConnectListener {

    void onConnected(RobotConnectInfo robot);

    void onDisconnected(RobotConnectInfo robot);

}
