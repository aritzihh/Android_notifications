package com.example.aritzi.notificacionsicanetv2;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Incidencias_pendientes extends AppCompatActivity {
    private ArrayList incidencias_activas;
    private ListView lista_incidencias;
    private MenuItem nombre_usuario;
    private SharedPreferences preferences;
    private static String TAG = "INCIDENCIAS PENDIENTES";
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent inten = new Intent(Incidencias_pendientes.this,MainActivity.class);
            inten.putExtras(intent.getExtras());
            startActivity(inten);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incidencias_pendientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_act_principal);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        lista_incidencias = (ListView) findViewById(R.id.lista_incidencias);

    }

    public void obtenIncidencias(){
        IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(this);
        incidencias_activas = incidenciasDbHelper.getListaIncidencias();
        IncidenciasAdapter adapter;
        adapter = new IncidenciasAdapter(Incidencias_pendientes.this, incidencias_activas);
        lista_incidencias.setAdapter(adapter);
        lista_incidencias.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Intent intent = new Intent(Incidencias_pendientes.this, MainActivity.class);
                Incidencia incidencia = (Incidencia)incidencias_activas.get(position);
                intent.putExtra("no_incidencia",String.valueOf(incidencia.getNoReporte()));
                intent.putExtra("lugar_incidencia",incidencia.getLugar());
                intent.putExtra("descripcion",incidencia.getDescripcion());
                intent.putExtra("domicilio",incidencia.getDomicilio());
                intent.putExtra("reportador",incidencia.getReportador());
                intent.putExtra("tipo_incidencia",incidencia.getTipo());
                intent.putExtra("detalles",incidencia.getDetalles());
                intent.putExtra("no_poliza",String.valueOf(incidencia.getNo_poliza()));
                intent.putExtra("estado_atencion",incidencia.getEstado());
                intent.putExtra("fecha",incidencia.getFecha());
                intent.putExtra("hora",incidencia.getHora());
                intent.putExtra("empleado",incidencia.getEmpleado_que_registro());
                intent.putExtra("rfc_empleado",incidencia.getRfc_empleado());
                startActivity(intent);
            }

        });
        if(this.getIntent().hasExtra("justificado")){
            Toast toast1 = Toast.makeText(getApplicationContext(), "¡Se envio su mensaje!", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.CENTER, 0, 0);
            toast1.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String usuario = preferences.getString(getString(R.string.usuario_key),null);
        menu.findItem(R.id.propietario).setTitle(usuario.toUpperCase());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.propietario:
                return true;
            case R.id.salir:
                preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(getString(R.string.usuario_key));
                editor.clear();
                editor.apply();
                Log.d("USUARIO",preferences.getString(getString(R.string.usuario_key),"no hay"));
                Intent intent = new Intent(this,Login.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        lista_incidencias = (ListView) findViewById(R.id.lista_incidencias);
        obtenIncidencias();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
                new IntentFilter("nueva-notificacion-recibida"));
        obtenIncidencias();
    }
}
