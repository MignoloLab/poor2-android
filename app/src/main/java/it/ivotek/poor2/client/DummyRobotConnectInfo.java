package it.ivotek.poor2.client;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Informazioni di connessione al robot finto.
 * @author Daniele Ricci
 */
public class DummyRobotConnectInfo extends RobotConnectInfo {

    private final String mAddress;
    private final String mName;

    DummyRobotConnectInfo(String address, String name) {
        super(name != null ? name : address);
        mAddress = address;
        mName = name;
    }

    @Override
    public String getDisplayName() {
        return mName != null ? mName : mAddress;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

}
