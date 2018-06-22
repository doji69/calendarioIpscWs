package com.fenixbcn.calendarioipscws;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarMainActivity extends AppCompatActivity {

    private static final String TAG = "Calendario Ipsc";
    private TextView tvOutputText;
    private CaldroidFragment calendario;
    private Button btnAgenda;
    ProgressDialog mProgress;
    List<String> lCadenaEventos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);

        tvOutputText = (TextView) findViewById(R.id.tvOutputText);

        calendario = new CaldroidFragment(); // crea la instancia del calendario tipo Caldroid

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            calendario.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        } else { // If activity is created from fresh
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY); // Monday

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            //args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            calendario.setArguments(args);
        }

        calendario.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {

                final Long selectedDate;

                selectedDate = date.getTime();

                //Toast.makeText(CalendarMainActivity.this, date.toString(), Toast.LENGTH_LONG).show();
                Intent viewDayEventsActivityVars = new Intent(getApplication(), ViewDayEventsActivity.class);
                viewDayEventsActivityVars.putStringArrayListExtra("lCadenaEventos", (ArrayList<String>) lCadenaEventos);
                viewDayEventsActivityVars.putExtra("selectedDate", selectedDate);
                startActivity(viewDayEventsActivityVars);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Obteniendo datos ...");

        btnAgenda = (Button) findViewById(R.id.btnAgenda);
        btnAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(CalendarMainActivity.this, "boton de vista de agenda pendiente de programar", Toast.LENGTH_SHORT).show();
                Intent agendaMainActivityVars = new Intent(getApplication(), AgendaMainActivity.class);
                agendaMainActivityVars.putStringArrayListExtra("lCadenaEventos", (ArrayList<String>) lCadenaEventos);
                startActivity(agendaMainActivityVars);
            }
        });

        // llamada a la funcion asyntask para capturar el resultado del webservice
        new jtGetEvents().execute("http://www.corsaformacio.com/IpscCalendar/calendarioIpscWs.php");



    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (calendario != null) {
            calendario.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (calendario != null) {
            calendario.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    /**
     *
     * @param result
     * Funcion llamada desde onPostExecute y que pinta los dias que hay un evento.
     * Esta funcion se ejecuta antes de asignar el calendario al layout
     */
    private void setCustomResourceForDates(List<String > result) {

        // hay que recuperar las fechas de inicio y fin de todos los eventos y marcar las fechas que estan en la lista.
        // si el evento dura un solo dia se marca el día, pero si dura más de un día se marca principio y final

        //Log.d(TAG, "el valor de result es: " + result.get(0));
        //Log.d(TAG, "el numero de valores es: " + result.size());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = null; // fecha del sistema
        Date fechaInicial = null; // fecha en la que empieza el evento
        Date fechaFinal = null; // fecha en la que termina el evento

        int diasInicio = 0; // guardamos la diferencia de dias entre la fecha actual y la de inicio del evento para saber que dia hay que colorear
        int diasFin = 0; // guardamos la diferencia de dias entre la fecha actual y la de fin del evento para saber que dia hay que colorear

        try {
            fechaActual = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i=0;i<result.size();i++) {

            String eventoTirada = result.get(i);
            String [] vEventoTirada = eventoTirada.split(" - ");

            try {
                if (vEventoTirada.length == 5) {
                    fechaInicial = dateFormat.parse(vEventoTirada[3]);
                    fechaFinal = dateFormat.parse(vEventoTirada[4]);
                } else if (vEventoTirada.length == 4) {
                    fechaInicial = dateFormat.parse(vEventoTirada[2]);
                    fechaFinal = dateFormat.parse(vEventoTirada[3]);
                } else if (vEventoTirada.length == 3) {
                    fechaInicial = dateFormat.parse(vEventoTirada[1]);
                    fechaFinal = dateFormat.parse(vEventoTirada[2]);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Log.d(TAG, "la fecha actual es : " + fechaActual);
            //Log.d(TAG, "la fecha inicio evento es : " + fechaInicial);
            //Log.d(TAG, "la fecha fin evento es : " + fechaFinal);

            diasInicio = (int) ((fechaInicial.getTime()-fechaActual.getTime())/86400000);
            diasFin = (int) ((fechaFinal.getTime()-fechaActual.getTime())/86400000);

            //Log.d(TAG, "la diferencia de dias al inicio es : " + diasInicio);
            //Log.d(TAG, "la diferencia de dias al final es : " + diasFin);

            // pintamos todos los dias dedes la fecha de inicio a la fecha de fin del evento
            int difDias = diasFin-diasInicio;
            //Log.d(TAG, "la diferencia de dias es : " + difDias);
            for (int j=0; j<=difDias;j++) {
                Calendar cal = Calendar.getInstance(); // hay que llamar al getInstance para cada fecha que se quiere colorear
                cal.add(Calendar.DATE, diasInicio+j);
                Date colorFechaPintar = cal.getTime();
                if (calendario != null) {
                    ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
                    calendario.setBackgroundDrawableForDate(blue, colorFechaPintar);
                    calendario.setTextColorForDate(R.color.white, colorFechaPintar);

                }
            }
        }
    }

    public class jtGetEvents extends AsyncTask<String , Void, List<String>> {


        @Override
        protected List<String> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject tiradas = new JSONObject(finalJson);
                JSONArray eventos = tiradas.getJSONArray("tiradas");

                //Log.d(TAG, "la lista de eventos es: " + eventos);
                //StringBuffer finalBuffereData = new StringBuffer();
                List<String> getEvents = new ArrayList<String>(); // variable de retorno

                //Log.d(TAG, "el tamaño del array es: " + eventos.length());

                for (int i=0; i<eventos.length(); i++) {

                    JSONObject evento = eventos.getJSONObject(i);

                    //Log.d(TAG, "el evento actual: " + evento);

                    String titulo = evento.getString("summary");
                    String fechaInicio = evento.getString("start");
                    String fechaFin = evento.getString("end");

                    //Log.d(TAG, "titulo: " + titulo + " fechaInicio: " + fechaInicio + "fechaFin: "+ fechaFin);

                    String cadena = titulo + " - " + fechaInicio + " - "+ fechaFin;

                    getEvents.add(cadena);
                }

                return getEvents;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            tvOutputText.setText("");
            mProgress.show();

        }

        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                tvOutputText.setText("No hay eventos.");
            } else {
                lCadenaEventos = output;
                setCustomResourceForDates(output); // colorea el calendario segun la cadena de eventos pasada por parametro. Esta funcion tiene que estar por encima de la asynTask
                //tvOutputText.setText(TextUtils.join(",", output));
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.lyCalendario, calendario);
                t.commit();

            }
        }
    }
}
