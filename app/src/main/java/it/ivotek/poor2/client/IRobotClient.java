package it.ivotek.poor2.client;


/**
 * Interfaccia per connessione al robot generica.
 * @author Daniele Ricci
 */
public interface IRobotClient<T extends RobotConnectInfo> {

    /** Ricerca i robot nelle vicinanze. */
    void discover(RobotDiscoveryListener listener);

    /** Annulla la ricerca lanciata. */
    void cancelDiscovery();

    /** Connette al robot. */
    void connect(T robot, RobotConnectListener listener);

    /** Disconnette dal robot corrente. */
    void disconnect();

}
