package it.ivotek.poor2.client;

import java.util.List;

/**
 * Listener per la scoperta di robot.
 * @author Daniele Ricci
 */
public interface RobotDiscoveryListener {

    void discovered(List<RobotConnectInfo> robots);

}
