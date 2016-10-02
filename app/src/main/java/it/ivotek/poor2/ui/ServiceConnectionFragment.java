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
    private OnServiceConnectionFragmentListener mListener;

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
        if (context instanceof OnServiceConnectionFragmentListener) {
            mListener = (OnServiceConnectionFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + " must implement " + OnServiceConnectionFragmentListener.class.getSimpleName());
        }

        RobotService.start(context);
        mBound = RobotService.bind(context, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        if (mListener != null)
            mListener.onServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
        mService = null;
        if (mListener != null)
            mListener.onServiceDisconnected();
    }

    /**
     * Interfaccia da implementare per l'Activity per essere notificati
     * della connessione al servizio di connessione.
     */
    public interface OnServiceConnectionFragmentListener {
        void onServiceConnected();
        void onServiceDisconnected();
    }

}
