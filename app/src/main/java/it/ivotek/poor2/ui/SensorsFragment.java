package it.ivotek.poor2.ui;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.ivotek.poor2.R;
import it.ivotek.poor2.client.RobotConnectInfo;
import it.ivotek.poor2.client.RobotConnectListener;
import it.ivotek.poor2.client.RobotSensorsData;
import it.ivotek.poor2.client.RobotSensorsListener;
import it.ivotek.poor2.service.RobotService;


public class SensorsFragment extends Fragment implements RobotConnectListener, RobotSensorsListener {

    private OnSensorsFragmentListener mListener;

    private TextView mUltrasoundLeft;
    private TextView mUltrasoundCenter;
    private TextView mUltrasoundRight;
    private TextView mCompass;

    public SensorsFragment() {
    }

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mUltrasoundLeft = (TextView) view.findViewById(R.id.ultrasound_left);
        mUltrasoundCenter = (TextView) view.findViewById(R.id.ultrasound_center);
        mUltrasoundRight = (TextView) view.findViewById(R.id.ultrasound_right);
        mCompass = (TextView) view.findViewById(R.id.compass);
    }

    // FIXME codice duplicato in MainActivity
    private ServiceConnectionFragment getConnectionFragment() {
        ServiceConnectionFragment f = (ServiceConnectionFragment) getFragmentManager()
            .findFragmentByTag("connection");
        if (f == null) {
            f = ServiceConnectionFragment.newInstance();
            getFragmentManager().beginTransaction()
                .add(f, "connection")
                .commit();
            getFragmentManager().executePendingTransactions();
        }
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSensorsFragmentListener) {
            mListener = (OnSensorsFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + " must implement " + OnSensorsFragmentListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        clearListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        setListener();
    }

    private void setListener() {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setConnectListener(this);
        service.setSensorsListener(this);
    }

    private void clearListener() {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setConnectListener(null);
        service.setSensorsListener(null);
    }

    @Override
    public void onAuthorized(RobotConnectInfo robot) {
        // non usato
    }

    @Override
    public void onConnected(RobotConnectInfo robot) {
        // non usato
    }

    @Override
    public void onDisconnected(RobotConnectInfo robot) {
        if (mListener != null)
            mListener.onDisconnected();
    }

    @Override
    public void onConnectError(RobotConnectInfo robot, String message) {
        // non usato
    }

    @Override
    public void onSensorsData(final RobotSensorsData data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUltrasoundLeft.setText(String.valueOf(data.getUltrasoundLeft()));
                mUltrasoundCenter.setText(String.valueOf(data.getUltrasoundCenter()));
                mUltrasoundRight.setText(String.valueOf(data.getUltrasoundRight()));
                mCompass.setText(String.format(Locale.US, "%.2f°", data.getCompass()));
            }
        });
    }

    /**
     * Interfaccia da implementare per l'Activity per essere notificati
     * di quello che succede col robot.
     */
    public interface OnSensorsFragmentListener {
        void onDisconnected();
    }
}
