package it.ivotek.poor2.client;

/**
 * Informazioni di connessione per un robot.
 * @author Daniele Ricci
 */
public abstract class RobotConnectInfo {

    private final String mConnectionString;

    public RobotConnectInfo(String connectionString) {
        mConnectionString = connectionString;
    }

    public String getConnectionString() {
        return mConnectionString;
    }

    public abstract String getDisplayName();

    public abstract boolean isAuthorized();

    @Override
    public String toString() {
        return mConnectionString;
    }

}
