package it.ivotek.poor2.ui;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import it.ivotek.poor2.R;
import it.ivotek.poor2.client.RobotConnectInfo;
import it.ivotek.poor2.client.RobotConnectListener;
import it.ivotek.poor2.service.RobotService;


public class ControlFragment extends Fragment implements RobotConnectListener {

    private OnControlFragmentListener mListener;
    private Joystick mMovementSeek;
    private Joystick mTurnSeek;
    private ToggleButton mSlowMode;

    public ControlFragment() {
    }

    public static ControlFragment newInstance() {
        return new ControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMovementSeek = (Joystick) view.findViewById(R.id.seek1);
        mMovementSeek.setMotionConstraint(Joystick.MotionConstraint.VERTICAL);
        mMovementSeek.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
            }

            @Override
            public void onDrag(float degrees, float offset) {
                // modalita' tartaruga - dividi per due
                if (isSlowMode())
                    offset /= 2;

                // trasforma offset in percentuale
                // degrees: -180 -> 180
                // offset: 0 -> 1
                setMovement(offset * (degrees / Math.abs(degrees)));
            }

            @Override
            public void onUp() {
                setMovement(0);
            }
        });

        mTurnSeek = (Joystick) view.findViewById(R.id.seek2);
        mTurnSeek.setMotionConstraint(Joystick.MotionConstraint.HORIZONTAL);
        mTurnSeek.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
            }

            @Override
            public void onDrag(float degrees, float offset) {
                // modalita' tartaruga - dividi per due
                if (isSlowMode())
                    offset /= 2;

                // trasforma offset in percentuale
                // degrees: -180 -> 180
                // offset: 0 -> 1
                setTurn(offset * (degrees != 0 ? (degrees / Math.abs(degrees)) : 1));
            }

            @Override
            public void onUp() {
                setTurn(0);
            }
        });

        mSlowMode = (ToggleButton) view.findViewById(R.id.toggle_slow);

        ((GestureOverlayView) view).setGestureVisible(false);
        final GestureDetectorCompat gst = new GestureDetectorCompat(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isDownSwipe(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                    ((MainActivity) getActivity()).showToolbar();
                    return true;
                }
                return false;
            }

            private boolean isDownSwipe(float x1, float y1, float x2, float y2) {
                double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
                return (angle < -45 && angle>= -135);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gst.onTouchEvent(event);
            }
        });
    }

    private boolean isSlowMode() {
        return mSlowMode.isChecked();
    }

    /** @param value Valore [-1, 1] per il movimento avanti/indietro. */
    void setMovement(float value) {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setEnginePower(convertSeekValue(value));
    }

    /** @param value Valore [-1, 1] per il controllo dello sterzo (-1=sinistra, 1=destra). */
    void setTurn(float value) {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setTurn(convertSeekValue(value));
    }

    /** Converte un valore [-1, 1] in [-255, 255]. */
    private int convertSeekValue(float value) {
        return (int) Math.floor(value * 255);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // FIXME dipendenza dalla classe MainActivity
        ((MainActivity) getActivity()).hideToolbar();
        Toast.makeText(getActivity(), R.string.toolbar_hide_warning, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnControlFragmentListener) {
            mListener = (OnControlFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + " must implement " + OnControlFragmentListener.class.getSimpleName());
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
        resetMovement();
        clearListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetMovement();
        setListener();
    }

    private void setListener() {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setConnectListener(this);
    }

    private void clearListener() {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setConnectListener(null);
    }

    /** Resetta a movimento e sterzata neutri. */
    private void resetMovement() {
        setMovement(0);
        setTurn(0);
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

    /**
     * Interfaccia da implementare per l'Activity per essere notificati
     * di quello che succede col robot.
     */
    public interface OnControlFragmentListener {
        void onDisconnected();
    }
}
