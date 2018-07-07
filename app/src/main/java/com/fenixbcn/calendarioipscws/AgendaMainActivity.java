package com.fenixbcn.calendarioipscws;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class AgendaMainActivity extends AppCompatActivity {

    private static final String TAG = "Calendario Ipsc";

    List<String> lCadenaEventos = new ArrayList<String>();
    List<String> lCadenaEventosOrdered = new ArrayList<String>();
    List<String> lCadenaEventosOrderedHeader = new ArrayList<String>();
    ListView lvAllEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);

        Bundle viewDayEventsActivityVars = getIntent().getExtras();
        lCadenaEventos = viewDayEventsActivityVars.getStringArrayList("lCadenaEventos");

        lCadenaEventosOrdered = Funciones.orderEventsByDate(lCadenaEventos);
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(TextUtils.join(",", lCadenaEventosOrdered));
        lCadenaEventosOrderedHeader = Funciones.orderEventsByDateWithHeader(lCadenaEventosOrdered);
        //TextView textView = (TextView) findViewById(R.id.tvPrueba);
        //textView.setText(TextUtils.join(",", lCadenaEventosOrderedHeader));

        List<Object> alEventos = new ArrayList<>();
        EventoAdapter eventos = null;

        String  fechaInicial = null; // fecha en la que empieza el evento
        String fechaFinal = null; // fecha en la que termina el evento
        String titulo = "";

        for (int i = 0; i<lCadenaEventosOrderedHeader.size(); i++) {

            String eventoTirada = lCadenaEventosOrderedHeader.get(i);
            //Log.d(TAG, "el items actual: " + eventoTirada);
            String [] vEventoTirada = eventoTirada.split(" - ");

            if (vEventoTirada.length == 5) {
                fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[3]);
                fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[4]);
                titulo = vEventoTirada[0] + "\n" + vEventoTirada[1] + "\n" +vEventoTirada[2];
                alEventos.add (new Evento(titulo, fechaInicial, fechaFinal));
            } else if (vEventoTirada.length == 4) {
                fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[2]);
                fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[3]);
                titulo = vEventoTirada[0] + "\n" + vEventoTirada[1];
                alEventos.add (new Evento(titulo, fechaInicial, fechaFinal));
            } else if (vEventoTirada.length == 3) {
                fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[1]);
                fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[2]);
                titulo = vEventoTirada[0];
                alEventos.add (new Evento(titulo, fechaInicial, fechaFinal));
            } else if (vEventoTirada.length == 1) {
                alEventos.add (new String(vEventoTirada[0]));
            }
        }

        //TextView textView = (TextView) findViewById(R.id.tvPrueba);
        //textView.setText(TextUtils.join(",", alEventos));

        eventos = new EventoAdapter(this, alEventos);

        lvAllEvents = (ListView) findViewById(R.id.lvAllEvents);
        lvAllEvents.setAdapter(eventos);

        lvAllEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            String selectedTitulo = ((Evento)lvAllEvents.getItemAtPosition(i)).titulo;
            //Log.d(TAG, "el evento es: " + selectedTitulo);

            LatLng latPositionSel = Funciones.getLocation(selectedTitulo);

            Double latitud = latPositionSel.latitude;
            Double longitud = latPositionSel.longitude;
            float zomm = 20;

            // coloca el club en la app de google maps
            Uri gmmIntentUri = Uri.parse("geo:" + latitud + "," + longitud + "?z=" + zomm + ",q=" + Uri.encode("CLub tiro"));

            Log.d(TAG, "la localizacion es: " + gmmIntentUri);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {

                Toast.makeText(AgendaMainActivity.this, "Por favor, instala Google Maps", Toast.LENGTH_LONG).show();
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
