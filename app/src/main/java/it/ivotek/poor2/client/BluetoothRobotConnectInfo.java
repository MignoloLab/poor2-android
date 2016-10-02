package it.ivotek.poor2.client;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Informazioni di connessione al robot per bluetooth.
 * @author Daniele Ricci
 */
public class BluetoothRobotConnectInfo extends RobotConnectInfo {

    private final String mAddress;
    private final String mName;
    private final BluetoothClass mBluetoothClass;
    private final boolean mBonded;

    BluetoothRobotConnectInfo(String address, String name, BluetoothClass btClass, int bondState) {
        super(buildConnectionString(address));
        mAddress = address;
        mName = name;
        mBluetoothClass = btClass;
        mBonded = bondState == BluetoothDevice.BOND_BONDED;
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
    public boolean isAuthorized() {
        return mBonded;
    }

    @Override
    public String toString() {
        return mName != null ? mName : mAddress;
    }

}
