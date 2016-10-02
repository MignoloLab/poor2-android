package it.ivotek.poor2.client;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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

    /** Richiede autorizzazione al robot (es. bluetooth pair). */
    void authorize(T robot, RobotConnectListener listener);

    /** Disconnette dal robot corrente. */
    void disconnect();

    /** Ritorna il robot connesso. */
    @Nullable T getConnectedRobot();

    /** Ritorna true se connesso al robot. */
    boolean isConnected();

    /** Ritorna true se possiamo iniziare subito il discovery. */
    boolean canDiscover();

    /** Abilita il discovery (es. attiva il bluetooth). */
    void enableDiscovery(Fragment fragment, int requestCode);

}
