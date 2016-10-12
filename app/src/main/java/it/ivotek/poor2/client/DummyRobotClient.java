package it.ivotek.poor2.client;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Client dummy che logga le azioni richieste e spara dati a caso dai sensori.
 * @author Daniele Ricci
 */
public class DummyRobotClient implements
        IRobotClient<DummyRobotConnectInfo> {

    private final Context mContext;

    private RobotDiscoveryListener mDiscoverListener;
    private RobotConnectListener mConnectListener;
    private DummyRobotConnectInfo mRobot;

    public DummyRobotClient(Context context) {
        mContext = context;
    }

    @Override
    public boolean canDiscover() {
        return true;
    }

    @Override
    public void enableDiscovery(Fragment fragment, int requestCode) {
        // niente
    }

    @Override
    public void discover(RobotDiscoveryListener listener) {
        if (mDiscoverListener != null)
            throw new IllegalArgumentException("Another discovery is in progress");

        // imposta tutto e fai partire lo scan
        mDiscoverListener = listener;
        List<RobotConnectInfo> scanned = new ArrayList<>(1);
        scanned.add(new DummyRobotConnectInfo("localhost", "DUMMY"));
        mDiscoverListener.discovered(scanned);
    }

    @Override
    public void cancelDiscovery() {
        mDiscoverListener = null;
    }

    @Override
    public void connect(DummyRobotConnectInfo robot, RobotConnectListener listener) {
        if (mConnectListener != null)
            throw new IllegalArgumentException("Another connection is active or in progress");

        mConnectListener = listener;
        mRobot = robot;
        mConnectListener.onConnected(robot);
    }

    @Override
    public void authorize(DummyRobotConnectInfo robot, RobotConnectListener listener) {
        throw new UnsupportedOperationException("authorization not required");
    }

    @Override
    public void disconnect() {
        if (mConnectListener != null) {
            final RobotConnectInfo robot = mRobot;
            mRobot = null;
            mConnectListener.onDisconnected(robot);
        }
    }

    @Nullable
    @Override
    public DummyRobotConnectInfo getConnectedRobot() {
        return mRobot;
    }

    @Override
    public boolean isConnected() {
        return mRobot != null;
    }

    @Override
    public void setEnginePower(int value) {
        Log.d("DUMMY", "moving " + value);
    }

    @Override
    public void setTurn(int value) {
        Log.d("DUMMY", "turning " + value);
    }

    @Override
    public void setSensorsListener(RobotSensorsListener listener) {
        // TODO
    }

}
