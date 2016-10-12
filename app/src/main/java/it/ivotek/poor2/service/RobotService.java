package it.ivotek.poor2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;

import it.ivotek.poor2.BuildConfig;
import it.ivotek.poor2.client.BluetoothRobotClient;
import it.ivotek.poor2.client.DummyRobotClient;
import it.ivotek.poor2.client.IRobotClient;
import it.ivotek.poor2.client.RobotConnectInfo;
import it.ivotek.poor2.client.RobotConnectListener;
import it.ivotek.poor2.client.RobotDiscoveryListener;
import it.ivotek.poor2.client.RobotSensorsListener;


/**
 * Servizio di connessione al robot.
 * @author Daniele Ricci
 */
public class RobotService extends Service {

    private IRobotClient<RobotConnectInfo> mClient;

    private RobotDiscoveryListener mDiscoverListener;
    private RobotConnectListener mConnectListener;

    @Override
    @SuppressWarnings("unchecked")
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mClient == null) {
            Log.v("Poor", "Device: " + android.os.Build.FINGERPRINT);
            if (android.os.Build.FINGERPRINT.contains("generic")) {
                // emulatore?
                mClient = (IRobotClient) new DummyRobotClient(this);
            }
            else {
                mClient = (IRobotClient) new BluetoothRobotClient(this);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        mClient.cancelDiscovery();
        mClient.disconnect();
    }

    public class LocalBinder extends Binder {

        public RobotService getService() {
            return RobotService.this;
        }

    }

    public void setDiscoverListener(RobotDiscoveryListener listener) {
        mDiscoverListener = listener;
    }

    public void setConnectListener(RobotConnectListener listener) {
        mConnectListener = listener;
    }

    public void setSensorsListener(RobotSensorsListener listener) {
        mClient.setSensorsListener(listener);
    }

    public boolean canDiscover() {
        return mClient.canDiscover();
    }

    public void enableDiscovery(Fragment fragment, int requestCode) {
        mClient.enableDiscovery(fragment, requestCode);
    }

    public void discover() {
        mClient.discover(mDiscoverListener);
    }

    public void cancelDiscovery() {
        mClient.cancelDiscovery();
    }

    public void connect(RobotConnectInfo robot) {
        mClient.connect(robot, mConnectListener);
    }

    public void disconnect() {
        mClient.disconnect();
    }

    public void authorize(RobotConnectInfo robot) {
        mClient.authorize(robot, mConnectListener);
    }

    public RobotConnectInfo getConnectedRobot() {
        return isConnected() ? mClient.getConnectedRobot() : null;
    }

    public boolean isConnected() {
        return mClient.isConnected();
    }

    public void setEnginePower(int value) {
        if (isConnected()) {
            mClient.setEnginePower(value);
        }
    }

    public void setTurn(int value) {
        if (isConnected()) {
            mClient.setTurn(value);
        }
    }

    private static Intent intent(Context context) {
        return new Intent(context, RobotService.class);
    }

    public static void start(Context context) {
        context.startService(intent(context));
    }

    public static boolean bind(Context context, ServiceConnection connection) {
        return context.bindService(intent(context), connection, Context.BIND_AUTO_CREATE);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, RobotService.class));
    }

}
