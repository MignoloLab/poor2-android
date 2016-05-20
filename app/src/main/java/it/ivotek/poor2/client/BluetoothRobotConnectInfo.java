package it.ivotek.poor2.client;

import android.bluetooth.BluetoothClass;

/**
 * Informazioni di connessione al robot per bluetooth.
 * @author Daniele Ricci
 */
public class BluetoothRobotConnectInfo extends RobotConnectInfo {

    private final String mAddress;
    private final String mName;
    private final BluetoothClass mBluetoothClass;

    BluetoothRobotConnectInfo(String address, String name, BluetoothClass btClass) {
        super(buildConnectionString(address));
        mAddress = address;
        mName = name;
        mBluetoothClass = btClass;
    }

    private static String buildConnectionString(String address) {
        return "bluetooth://[" + address + "]";
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

    public BluetoothClass getBluetoothClass() {
        return mBluetoothClass;
    }

    @Override
    public String toString() {
        return mName != null ? mName : mAddress;
    }

}
