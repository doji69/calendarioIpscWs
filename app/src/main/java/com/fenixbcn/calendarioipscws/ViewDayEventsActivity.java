package com.fenixbcn.calendarioipscws;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ViewDayEventsActivity extends AppCompatActivity {

    private static final String TAG = "Calendario Ipsc";

    ListView lvDayEvents;
    TextView tvNoEvents;
    List<String> lCadenaEventos = new ArrayList<String>();
    List<String> lCadenaEventosSel = new ArrayList<String>();
    Date selectedDate = new Date();

    List<Object> alEventos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_day_events);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);

        Bundle viewDayEventsActivityVars = getIntent().getExtras();
        lCadenaEventos = viewDayEventsActivityVars.getStringArrayList("lCadenaEventos");
        selectedDate.setTime(viewDayEventsActivityVars.getLong("selectedDate", -1));

        String sSelectedDate = "";
        SimpleDateFormat outputDateFormat = null;

        outputDateFormat = new SimpleDateFormat("dd MMM yyyy");

        sSelectedDate = outputDateFormat.format(selectedDate);

        //Log.d(TAG, "numero de items: " + lCadenaEventos.size());
        //Log.d(TAG, "la fecha pasada: " + selectedDate);

        /* extraer de la lista de eventos los eventos en los que fecha seleccionada y es inicio, fin o
        se encuentra entre el inicio y el fin */

        lCadenaEventosSel = Funciones.getDateEvents(lCadenaEventos, selectedDate);

        //Log.d(TAG, "la lista de items seleccionados: " + lCadenaEventosSel);

        /* fin extraer de la lista de eventos los eventos en los que fecha seleccionada y es inicio, fin o
        se encuentra entre el inicio y el fin */

        if (lCadenaEventosSel.size() == 0) {
            tvNoEvents = (TextView) findViewById(R.id.tvNoEvents);
            tvNoEvents.setText("No hay eventos para este dia");

        } else {
            tvNoEvents = (TextView) findViewById(R.id.tvNoEvents);
            tvNoEvents.setText("");


            final EventoAdapter eventos;

            alEventos.add(new String(sSelectedDate));

            String fechaInicial = null; // fecha en la que empieza el evento
            String fechaFinal = null; // fecha en la que termina el evento
            String titulo = "";

            for (int i = 0; i<lCadenaEventosSel.size(); i++) {

                String eventoTirada = lCadenaEventosSel.get(i);

                //Log.d(TAG, "el items actual: " + eventoTirada);
                String [] vEventoTirada = eventoTirada.split(" - ");

                if (vEventoTirada.length == 5) {
                    fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[3]);
                    fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[4]);
                    titulo = vEventoTirada[0] + "\n" + vEventoTirada[1] + "\n" +vEventoTirada[2];
                } else if (vEventoTirada.length == 4) {
                    fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[2]);
                    fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[3]);
                    titulo = vEventoTirada[0] + "\n" + vEventoTirada[1];
                } else if (vEventoTirada.length == 3) {
                    fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[1]);
                    fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[2]);
                    titulo = vEventoTirada[0];
                }

                alEventos.add (new Evento(titulo, fechaInicial, fechaFinal));

            }

            eventos = new EventoAdapter(this, alEventos);

            lvDayEvents = (ListView) findViewById(R.id.lvDayEvents);
            lvDayEvents.setAdapter(eventos);

            lvDayEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String selectedTitulo = ((Evento)lvDayEvents.getItemAtPosition(i)).titulo;
                    //Log.d(TAG, "el evento es: " + selectedTitulo);


                    LatLng latPositionSel = Funciones.getLocation(selectedTitulo);

                    Double latitud = latPositionSel.latitude;
                    Double longitud = latPositionSel.longitude;
                    float zomm = 20;

                    Uri gmmIntentUri = Uri.parse("geo:" + latitud + "," + longitud + "?z=" + zomm + ",q=" + Uri.encode("CLub tiro"));
                    Log.d(TAG, "la localizacion es: " + gmmIntentUri);

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {

                        Toast.makeText(ViewDayEventsActivity.this, "Por favor, instala Google Maps", Toast.LENGTH_LONG).show();
                    }


                    /*
                    Intent clubsMapsActivityVars = new Intent(getApplication(), ClubsMapsActivity.class);
                    clubsMapsActivityVars.putExtra("selectedTitulo", selectedTitulo);
                    startActivity(clubsMapsActivityVars);
                    */

                }
            });

        }
    }
}
