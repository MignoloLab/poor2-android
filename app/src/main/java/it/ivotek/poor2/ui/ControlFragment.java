package it.ivotek.poor2.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import it.ivotek.poor2.R;
import it.ivotek.poor2.service.RobotService;


public class ControlFragment extends Fragment {

    private OnControlFragmentListener mListener;
    private SeekBar mMovementSeek;
    private SeekBar mTurnSeek;

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
        mMovementSeek = (SeekBar) view.findViewById(R.id.seek_movement);
        mMovementSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setMovement(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // riporta la seek a neutro
                seekBar.setProgress(50);
            }
        });
        mTurnSeek = (SeekBar) view.findViewById(R.id.seek_turn);
        mTurnSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTurn(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // riporta la seek a neutro
                seekBar.setProgress(50);
            }
        });
    }

    /** @param value Valore 0-100 per il movimento avanti/indietro. */
    void setMovement(int value) {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setEnginePower(convertSeekValue(value));
    }

    /** @param value Valore 0-100 per il controllo dello sterzo (0=sinistra, 100=destra). */
    void setTurn(int value) {
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();
        service.setTurn(convertSeekValue(value));
    }

    /** Converte un valore 0-100 in [-255, 255]. */
    private int convertSeekValue(int value) {
        if (value == 50)
            // inutile fare calcoli...
            return 0;

        int comp = value * 512 / 100;
        return comp - 256 + (value > 50 ? -1 : 1);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        resetMovement();
    }

    /** Resetta a movimento e sterzata neutri. */
    private void resetMovement() {
        // il listener si occupera' di inviare i dati al servizio
        mTurnSeek.setProgress(50);
        mMovementSeek.setProgress(50);
    }

    /**
     * Interfaccia da implementare per l'Activity per essere notificati
     * di quello che succede col robot.
     */
    public interface OnControlFragmentListener {
        void onDisconnected();
    }
}
