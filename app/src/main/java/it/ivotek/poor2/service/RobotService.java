package it.ivotek.poor2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import it.ivotek.poor2.client.BluetoothRobotClient;
import it.ivotek.poor2.client.IRobotClient;
import it.ivotek.poor2.client.RobotConnectInfo;
import it.ivotek.poor2.client.RobotConnectListener;
import it.ivotek.poor2.client.RobotDiscoveryListener;


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
        mClient = (IRobotClient) new BluetoothRobotClient(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        // TODO
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

    public void discover() {
        mClient.discover(mDiscoverListener);
    }

    public void connect(RobotConnectInfo robot) {
        mClient.connect(robot, mConnectListener);
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
