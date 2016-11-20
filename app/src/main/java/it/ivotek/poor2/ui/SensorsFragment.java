package it.ivotek.poor2.ui;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private TextView mAccelerationX;
    private TextView mAccelerationY;
    private TextView mAccelerationZ;
    private TextView mTiltX;
    private TextView mTiltY;
    private TextView mTiltZ;
    private TextView mHumidity;
    private TextView mTemperature;
    private TextView mPowerIn;
    private TextView mOpticalLeft;
    private TextView mOpticalCenterLeft;
    private TextView mOpticalCenterRight;
    private TextView mOpticalRight;
    private TextView mPhotoresistorLeft;
    private TextView mPhotoresistorCenter;
    private TextView mPhotoresistorRight;

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
        mAccelerationX = (TextView) view.findViewById(R.id.accelerationX);
        mAccelerationY = (TextView) view.findViewById(R.id.accelerationY);
        mAccelerationZ = (TextView) view.findViewById(R.id.accelerationZ);
        mTiltX = (TextView) view.findViewById(R.id.tiltX);
        mTiltY = (TextView) view.findViewById(R.id.tiltY);
        mTiltZ = (TextView) view.findViewById(R.id.tiltZ);
        mHumidity = (TextView) view.findViewById(R.id.humidity);
        mTemperature = (TextView) view.findViewById(R.id.temperature);
        mPowerIn = (TextView) view.findViewById(R.id.powerIn);
        mOpticalLeft = (TextView) view.findViewById(R.id.optical_left);
        mOpticalCenterLeft = (TextView) view.findViewById(R.id.optical_center_left);
        mOpticalCenterRight = (TextView) view.findViewById(R.id.optical_center_right);
        mOpticalRight = (TextView) view.findViewById(R.id.optical_right);
        mPhotoresistorLeft = (TextView) view.findViewById(R.id.photoresistor_left);
        mPhotoresistorCenter = (TextView) view.findViewById(R.id.photoresistor_center);
        mPhotoresistorRight = (TextView) view.findViewById(R.id.photoresistor_right);
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
                mUltrasoundLeft.setText(formatValue(data.getUltrasoundLeft()));
                mUltrasoundCenter.setText(formatValue(data.getUltrasoundCenter()));
                mUltrasoundRight.setText(formatValue(data.getUltrasoundRight()));
                mCompass.setText(formatValue("%.2f°", data.getCompass()));
                mAccelerationX.setText(formatValue(data.getAccelerationX()));
                mAccelerationY.setText(formatValue(data.getAccelerationY()));
                mAccelerationZ.setText(formatValue(data.getAccelerationZ()));
                mTiltX.setText(formatValue(data.getTiltX()));
                mTiltY.setText(formatValue(data.getTiltY()));
                mTiltZ.setText(formatValue(data.getTiltZ()));
                mHumidity.setText(formatValue("%d%%", data.getHumidity()));
                mTemperature.setText(formatValue("%d°", data.getTemperature()));
                mPowerIn.setText(formatValue("%.2f V", data.getPowerIn()));
                mOpticalLeft.setText(formatValue(data.getOpticalLeft()));
                mOpticalCenterLeft.setText(formatValue(data.getOpticalCenterLeft()));
                mOpticalCenterRight.setText(formatValue(data.getOpticalCenterRight()));
                mOpticalRight.setText(formatValue(data.getOpticalRight()));
                mPhotoresistorLeft.setText(formatValue("%.2f V", data.getPhotoresistorLeft()));
                mPhotoresistorCenter.setText(formatValue("%.2f V", data.getPhotoresistorCenter()));
                mPhotoresistorRight.setText(formatValue("%.2f V", data.getPhotoresistorRight()));
            }
        });
    }

    private String formatValue(Object value) {
        return value != null ? String.valueOf(value) : "ERR";
    }

    private String formatValue(String fmt, Object value) {
        return value != null ? String.format(Locale.US, fmt, value) : "ERR";
    }

    /**
     * Interfaccia da implementare per l'Activity per essere notificati
     * di quello che succede col robot.
     */
    public interface OnSensorsFragmentListener {
        void onDisconnected();
    }
}
