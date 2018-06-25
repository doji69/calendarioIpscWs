package com.fenixbcn.calendarioipscws;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ClubsMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Calendario Ipsc";

    private GoogleMap mMap;
    private final int googleRequestCode = 10;
    private final int googleErrorCode = 1;
    private  LatLng latPosition;
    String selectedTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs_maps);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        getSupportActionBar().setTitle("calendario Ipsc Cat");


        Bundle clubsMapsActivityVars = getIntent().getExtras();
        selectedTitulo = clubsMapsActivityVars.getString("selectedTitulo");

        Log.d(TAG, "el titlulo selecionado es: " + selectedTitulo);

        //int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());


        if (status == ConnectionResult.SUCCESS) {

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


        } else {

            //Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status,(Activity)getApplicationContext(),googleRequest);
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this,googleErrorCode,googleRequestCode);
            errorDialog.show();

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(googleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettingsMap = mMap.getUiSettings();
        uiSettingsMap.setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera

        LatLng latPositionSel = getLocation();

        if (latPositionSel != null) {

            mMap.addMarker(new MarkerOptions().position(latPositionSel).title("Club tiro"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latPositionSel));

            float zoomMapLevel = 14;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latPositionSel,zoomMapLevel));

        } else {
            Toast.makeText(this, "no hay mapa", Toast.LENGTH_SHORT).show();
        }
    }

    public LatLng getLocation() {

        Boolean nombreClubExists;
        String [] nombresClubs = {"Barcelona", "Granollers", "Jordi Tarragó","Lleida","Mataró","Montsia",
                "Osona","Platja d'Aro","Sabadell","Terrassa","Vilassar","R.T.A.A.","Hontanares de Eresma",
                "As Pontes","Huesca","Valdemoro"};
        LatLng latPosition = null;

        for (int i = 0; i < nombresClubs.length; i++) {

            nombreClubExists = selectedTitulo.contains(nombresClubs[i]);
            if (nombreClubExists==true) {

                switch (nombresClubs[i]) {
                    case "Granollers":
                        latPosition = new LatLng(41.6173887, 2.2704919);
                        break;
                    case "Barcelona":
                        latPosition = new LatLng(41.3695149, 2.1701805);
                        break;
                    case "Jordi Tarragó":
                        latPosition = new LatLng(41.1633502, 1.2416613);
                        break;
                    case "Lleida":
                        latPosition = new LatLng(41.6034722, 0.6058056);
                        break;
                    case "Mataró":
                        latPosition = new LatLng(41.576215, 2.420951);
                        break;
                    case "Montsia":
                        latPosition = new LatLng(40.685412, 0.543492);
                        break;
                    case "Osona":
                        latPosition = new LatLng(41.973305, 2.271611);
                        break;
                    case "Terrassa":
                        latPosition = new LatLng(41.59458, 2.03766);
                        break;
                    case "Vilassar":
                        latPosition = new LatLng(41.50611, 2.38046);
                        break;
                    case "R.T.A.A.":
                        latPosition = new LatLng(41.461502, -0.704428);
                        break;
                    case "Hontanares de Eresma":
                        latPosition = new LatLng(40.9965688, -4.1976809);
                        break;
                    case "As Pontes":
                        latPosition = new LatLng(43.4100612, -7.8611117);
                        break;
                    case "Huesca":
                        latPosition = new LatLng(42.1717392, -0.4046484);
                        break;
                    case "Valdemoro":
                        latPosition = new LatLng(40.1751297, -3.6524834);
                        break;
                    default:
                        latPosition = null;

                }
            }
        }

        return latPosition;
    }
}
