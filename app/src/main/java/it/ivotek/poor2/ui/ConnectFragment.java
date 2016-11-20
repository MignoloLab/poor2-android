package it.ivotek.poor2.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import it.ivotek.poor2.R;
import it.ivotek.poor2.client.RobotConnectInfo;
import it.ivotek.poor2.client.RobotConnectListener;
import it.ivotek.poor2.client.RobotDiscoveryListener;
import it.ivotek.poor2.service.RobotService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnConnectionFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFragment extends Fragment implements
        RobotDiscoveryListener, RobotConnectListener {

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;

    private OnConnectionFragmentListener mListener;

    private Button mConnectButton;
    private TextView mStatusText;

    private boolean mConnecting;

    public ConnectFragment() {
    }

    public static ConnectFragment newInstance() {
        return new ConnectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mStatusText = (TextView) view.findViewById(R.id.txt_status);
        mConnectButton = (Button) view.findViewById(R.id.btn_connect);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });
    }

    private void enableControls(boolean enabled) {
        mConnectButton.setEnabled(enabled);
    }

    private void setStatus(int resId) {
        setStatus(getText(resId));
    }

    private void setStatus(CharSequence text) {
        mStatusText.setText(text);
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

    public void onButtonPressed() {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();

        if (mConnecting) {
            doDisconnect();
        }
        else {
            enableControls(false);
            setStatus(R.string.status_discovering);

            service.setDiscoverListener(this);
            service.setConnectListener(this);
            if (service.canDiscover()) {
                service.discover();
            }
            else {
                service.enableDiscovery(this, REQUEST_BLUETOOTH_ENABLE);
            }
        }
    }

    public void doDisconnect() {
        doDisconnect(null);
    }

    public void doDisconnect(final String message) {
        mConnecting = false;
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        if (service.isConnected()) {
            service.disconnect();
        }

        service.setDiscoverListener(null);
        service.setConnectListener(null);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message != null) {
                    setStatus(message);
                }
                else {
                    setStatus(R.string.text_click_to_connect);
                }
                mConnectButton.setText(R.string.btn_connect);
                enableControls(true);
            }
        });
    }

    public void doCancelDiscovery() {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.cancelDiscovery();
        doDisconnect();
    }

    public void doConnect(RobotConnectInfo robot) {
        enableControls(true);
        mConnectButton.setText(R.string.btn_cancel);
        setStatus(R.string.status_connecting);

        mConnecting = true;
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.connect(robot);
    }

    public void doAuthorize(RobotConnectInfo robot) {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.authorize(robot);
    }

    @Override
    public void discovered(List<RobotConnectInfo> robots) {
        final ArrayAdapter<RobotConnectInfo> adapter = new ArrayAdapter<>(getContext(),
            android.R.layout.select_dialog_item);
        for (RobotConnectInfo robot : robots)
            adapter.add(robot);

        new AlertDialog.Builder(getContext())
            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RobotConnectInfo robot = adapter.getItem(which);
                    if (robot.isAuthorized())
                        doConnect(robot);
                    else
                        doAuthorize(robot);
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    doCancelDiscovery();
                }
            })
            // TODO i18n
            .setTitle("Connect to robot")
            .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConnectionFragmentListener) {
            mListener = (OnConnectionFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + " must implement " + OnConnectionFragmentListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAuthorized(RobotConnectInfo robot) {
        Log.v("Poor2", "robot was authorized: " + robot);
        doConnect(robot);
    }

    @Override
    public void onConnected(RobotConnectInfo robot) {
        mConnecting = false;
        if (mListener != null) {
            mListener.onConnected(robot);
        }
    }

    @Override
    public void onDisconnected(RobotConnectInfo connectInfo) {
        mConnecting = false;
        if (mListener != null) {
            mListener.onDisconnected();
        }
    }

    @Override
    public void onConnectError(RobotConnectInfo robot, String message) {
        // TODO i18n
        doDisconnect("Error: " + message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                ServiceConnectionFragment f = getConnectionFragment();
                RobotService service = f.getService();
                service.discover();
            }
            else {
                // TODO i18n
                Toast.makeText(getContext(), "Operation canceled by the user.",
                    Toast.LENGTH_SHORT).show();
                // TODO revert tutto
            }
        }
    }

    /**
     * Interfaccia da implementare per l'Activity per essere notificati
     * dell'avvenuta connessione.
     */
    public interface OnConnectionFragmentListener {
        void onConnected(RobotConnectInfo connectInfo);
        void onDisconnected();
    }
}
