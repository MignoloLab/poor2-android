package it.ivotek.poor2.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import it.ivotek.poor2.util.Bluetooth;

/**
 * Client per robot via bluetooth RFCOMM.
 * @author Daniele Ricci
 */
public class BluetoothRobotClient implements
        IRobotClient<BluetoothRobotConnectInfo>,
        Bluetooth.DiscoveryCallback, Bluetooth.CommunicationCallback {

    private static final String MESSAGE_MOVE = "*%03d%03d%03d%03d";

    private final Context mContext;
    private final Bluetooth mBluetooth;
    private final MessageParser mMessageParser;
    private final List<RobotConnectInfo> mScanned;

    private boolean mFirstTime;
    private int mEngineValue;
    private int mTurnValue;

    private RobotDiscoveryListener mDiscoverListener;
    private RobotConnectListener mConnectListener;
    private RobotSensorsListener mSensorsListener;
    private BluetoothRobotConnectInfo mRobot;

    public BluetoothRobotClient(Context context) {
        mContext = context;
        mBluetooth = new Bluetooth(context);
        mBluetooth.setDiscoveryCallback(this);
        mBluetooth.setCommunicationCallback(this);
        mScanned = new ArrayList<>();
        mMessageParser = new MessageParser();
        mFirstTime = true;
    }

    @Override
    public boolean canDiscover() {
        return mBluetooth.isBluetoothEnabled();
    }

    @Override
    public void enableDiscovery(Fragment fragment, int requestCode) {
        mBluetooth.requestEnableBluetooth(fragment, requestCode);
    }

    @Override
    public void discover(RobotDiscoveryListener listener) {
        if (mDiscoverListener != null)
            throw new IllegalArgumentException("Another discovery is in progress");

        // imposta tutto e fai partire lo scan
        mDiscoverListener = listener;
        mScanned.clear();
        if (mFirstTime) {
            mFirstTime = false;

            // prima volta: ritorna device abbinati
            List<BluetoothDevice> paired = mBluetooth.getPairedDevices();
            if (paired != null && paired.size() > 0) {
                for (BluetoothDevice dev : paired) {
                    onDevice(dev);
                }
                // simula discovery con i device abbinati
                onFinish();
                return;
            }
        }

        // non abbiamo nulla oppure non e' la prima volta
        // avvia discovery reale
        mBluetooth.scanDevices();
    }

    @Override
    public void cancelDiscovery() {
        mDiscoverListener = null;
    }

    @Override
    public void connect(BluetoothRobotConnectInfo robot, RobotConnectListener listener) {
        if (mConnectListener != null)
            throw new IllegalArgumentException("Another connection is active or in progress");

        mConnectListener = listener;
        mRobot = robot;
        mBluetooth.connectToAddress(robot.getAddress());
    }

    @Override
    public void authorize(BluetoothRobotConnectInfo robot, RobotConnectListener listener) {
        if (mConnectListener != null)
            throw new IllegalArgumentException("Another connection is active or in progress");

        mConnectListener = listener;
        mRobot = robot;
        mBluetooth.pair(mBluetooth.getRemoteDevice(robot.getAddress()));
    }

    @Override
    public void disconnect() {
        if (mConnectListener != null) {
            mBluetooth.disconnect();
        }
    }

    @Nullable
    @Override
    public BluetoothRobotConnectInfo getConnectedRobot() {
        return mRobot;
    }

    @Override
    public boolean isConnected() {
        return mRobot != null && mBluetooth.isConnected();
    }

    @Override
    public void onFinish() {
        final RobotDiscoveryListener l = mDiscoverListener;
        if (l != null) {
            mDiscoverListener = null;
            l.discovered(mScanned);
            mScanned.clear();
        }
    }

    @Override
    public void onDevice(BluetoothDevice device) {
        // converti device in connectInfo e aggiungi alla lista
        String address = device.getAddress();
        String name = device.getName();
        BluetoothClass btClass = device.getBluetoothClass();
        int bondState = device.getBondState();
        mScanned.add(new BluetoothRobotConnectInfo(address, name, btClass, bondState));
    }

    @Override
    public void onPair(BluetoothDevice device) {
        if (mConnectListener != null) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                final RobotConnectListener listener = mConnectListener;
                // abbiamo finito qui
                mConnectListener = null;
                listener.onAuthorized(mRobot);
            }
        }
    }

    @Override
    public void onUnpair(BluetoothDevice device) {
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        final RobotConnectListener l = mConnectListener;
        final BluetoothRobotConnectInfo robot = mRobot;
        if (l != null) {
            l.onConnected(robot);
        }
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        final RobotConnectListener l = mConnectListener;
        final BluetoothRobotConnectInfo robot = mRobot;
        mConnectListener = null;
        mRobot = null;
        if (l != null) {
            l.onDisconnected(robot);
        }
    }

    @Override
    public void onMessage(String message) {
        Log.v("Poor", "RECV: " + message);
        if (mMessageParser.feedData(message) && mSensorsListener != null) {
            mSensorsListener.onSensorsData(mMessageParser);
        }
    }

    @Override
    public void setSensorsListener(RobotSensorsListener listener) {
        mSensorsListener = listener;
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {
        final RobotConnectListener l = mConnectListener;
        final BluetoothRobotConnectInfo robot = mRobot;
        mConnectListener = null;
        mRobot = null;
        if (l != null) {
            l.onConnectError(robot, message);
        }
    }

    @Override
    public void setEnginePower(int value) {
        mEngineValue = value;
        updateMovement();
    }

    @Override
    public void setTurn(int value) {
        mTurnValue = value;
        updateMovement();
    }

    private void updateMovement() {
        if (!isConnected())
            return;

        // avanti/indietro
        int forward, backward;
        if (mEngineValue < 0) {
            forward = 0;
            backward = Math.abs(mEngineValue);
        }
        else {
            forward = Math.abs(mEngineValue);
            backward = 0;
        }

        // sinistra/destra
        int left, right;
        if (mTurnValue < 0) {
            left = Math.abs(mTurnValue);
            right = 0;
        }
        else {
            left = 0;
            right = Math.abs(mTurnValue);
        }

        // invia messaggio al robot
        String msg = String.format(Locale.US, MESSAGE_MOVE, forward, backward, right, left);
        Log.d("Poor", "SEND: " + msg);
        mBluetooth.send(msg);
    }

}
