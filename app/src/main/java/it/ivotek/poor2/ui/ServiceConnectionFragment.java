package it.ivotek.poor2.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import it.ivotek.poor2.service.RobotService;


/**
 * Un fragment non grafico che mantiene la connessione al {@link RobotService}.
 * @author Daniele Ricci
 */
public class ServiceConnectionFragment extends Fragment implements ServiceConnection {

    private boolean mBound;
    private RobotService mService;

    public ServiceConnectionFragment() {
    }

    public static ServiceConnectionFragment newInstance() {
        return new ServiceConnectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        RobotService.start(context);
        mBound = RobotService.bind(context, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getContext().unbindService(this);
            mBound = false;
        }
    }

    public RobotService getService() {
        return mService;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((RobotService.LocalBinder) service).getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
        mService = null;
    }
}
