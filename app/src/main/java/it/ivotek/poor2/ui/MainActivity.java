package it.ivotek.poor2.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import it.ivotek.poor2.R;
import it.ivotek.poor2.client.RobotConnectInfo;
import it.ivotek.poor2.service.RobotService;


public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener,
    ServiceConnectionFragment.OnServiceConnectionFragmentListener,
    ConnectFragment.OnConnectionFragmentListener,
    ControlFragment.OnControlFragmentListener,
    SensorsFragment.OnSensorsFragmentListener {

    private static final int NAV_CONTROL = 1;
    private static final int NAV_SENSORS = 2;

    private TextView mDrawerStatus;
    /** Usato in {@link #onSaveInstanceState(Bundle)} per recuperare l'ultima schermata mostrata. */
    private int mLastScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // textview dello stato di connessione nel drawer
        NavigationView nav = (NavigationView) drawer.findViewById(R.id.nav_view);
        View header = nav.getHeaderView(0);
        mDrawerStatus = (TextView) header.findViewById(R.id.textView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            mLastScreen = savedInstanceState.getInt("lastScreen");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content);
        int lastScreen = 0;
        if (f instanceof ControlFragment) {
            lastScreen = NAV_CONTROL;
        }
        else if (f instanceof SensorsFragment) {
            lastScreen = NAV_SENSORS;
        }
        outState.putInt("lastScreen", lastScreen);
    }

    private ServiceConnectionFragment getConnectionFragment() {
        ServiceConnectionFragment f = (ServiceConnectionFragment) getSupportFragmentManager()
            .findFragmentByTag("connection");
        if (f == null) {
            f = ServiceConnectionFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                .add(f, "connection")
                .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
        return f;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // crea fragment connessione al servizio
        getConnectionFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();

        if (id == R.id.nav_control) {
            if (service.isConnected()) {
                goToControl();
            }
        }
        else if (id == R.id.nav_sensors) {
            if (service.isConnected()) {
                goToSensors();
            }
        }
        else if (id == R.id.nav_manage) {
            // TODO
        }
        else if (id == R.id.nav_disconnect) {
            service.disconnect();
            // FIXME questo non dovrebbe essere chiamato in automatico dal servizio in teoria?
            onDisconnected();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Siamo connessi al servizio di connessione al robot.
     * Possiamo popolare la finestra con qualcosa.
     */
    @Override
    public void onServiceConnected() {
        // crea fragment connessione al servizio
        ServiceConnectionFragment f = getConnectionFragment();
        RobotService service = f.getService();

        if (service.isConnected()) {
            onConnected(service.getConnectedRobot());
        }
        else {
            onDisconnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        // non dovrebbe accadere!
        finish();
    }

    private void setDrawerStatus(CharSequence text) {
        mDrawerStatus.setText(text);
    }

    private void setConnectedTo(String name) {
        setDrawerStatus(getString(R.string.drawer_status_connected, name));
    }

    void goToControl() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_control);
        // apri il fragment di controllo
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, ControlFragment.newInstance())
            .commit();
    }

    void goToSensors() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_sensors);
        // apri il fragment dei sensori
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, SensorsFragment.newInstance())
            .commit();
    }

    @Override
    public void onConnected(final RobotConnectInfo connectInfo) {
        Log.v("Poor2", "connected to " + connectInfo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.getMenu().findItem(R.id.nav_disconnect).setVisible(true);
                setConnectedTo(connectInfo.toString());
                switch (mLastScreen) {
                    case NAV_SENSORS:
                        goToSensors();
                        break;
                    case NAV_CONTROL:
                    default:
                        goToControl();
                }
            }
        });
    }

    /** Disconnessione volontaria o dal robot. */
    @Override
    public void onDisconnected() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_disconnect).setVisible(false);
        setDrawerStatus(getString(R.string.drawer_status_disconnected));
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, ConnectFragment.newInstance())
            .commit();
    }
}
