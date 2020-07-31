package com.example.aritzi.notificacionsicanetv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView no_reporte;
    private TextView cliente;
    private TextView fecha_hora;
    private TextView domicilio;
    private TextView descripcion;
    private TextView reportador;
    private TextView tipo;
    private TextView detalles;
    private TextView estado;
    private TextView no_poliza;
    private TextView empleado;
    private static boolean asignado = false;
    private static String TAG = "MAIN**";
    private String no_incidencia = "";
    private IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(this);
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString(getString(R.string.usuario_key),"no existe").equalsIgnoreCase("no existe")) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }else if(this.getIntent().getExtras().size() == 0) {
            Intent intent = new Intent(this, Incidencias_pendientes.class);
            startActivity(intent);
        }else if(this.getIntent().hasExtra("no_incidencia") == true || this.getIntent().hasExtra("no_incidencia_finalizada") == true) {
            setContentView(R.layout.activity_main);
            no_reporte = (TextView) findViewById(R.id.noReporte);
            cliente = (TextView) findViewById(R.id.cliente);
            domicilio = (TextView) findViewById(R.id.domicilio);
            fecha_hora = (TextView) findViewById(R.id.fecha_hora);
            descripcion = (TextView) findViewById(R.id.descripcion);
            reportador = (TextView) findViewById(R.id.reportador);
            tipo = (TextView) findViewById(R.id.tipo);
            detalles = (TextView) findViewById(R.id.detalles);
            estado = (TextView) findViewById(R.id.estado);
            no_poliza = (TextView) findViewById(R.id.no_poliza);
            empleado = (TextView) findViewById(R.id.empleado_registro);
            Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
    public void muestraDatos(){
        final Intent intent = getIntent();
        Log.d(TAG, String.valueOf(intent.getExtras().get("no_poliza")));
        String estado_atencion = intent.getStringExtra("estado_atencion");
        if(intent.hasExtra("no_incidencia") ) {
            if(intent.getExtras().get("no_incidencia") instanceof Integer) {
                no_incidencia = String.valueOf(intent.getIntExtra("no_incidencia", 0));
                no_poliza.setText(String.valueOf(intent.getIntExtra("no_poliza", 0)));
            }else if(intent.getExtras().get("no_incidencia") instanceof String) {
                no_incidencia = intent.getStringExtra("no_incidencia");
                no_poliza.setText(intent.getStringExtra("no_poliza"));
            }
            boolean existe = incidenciasDbHelper.existeIncidencia(Integer.parseInt(no_incidencia));
            if(existe == false){
                this.guardaIncidencia(intent.getExtras());
            }
        }
        if(intent.hasExtra("no_incidencia_finalizada") && estado_atencion.equalsIgnoreCase("TERMINADO") == true){
            no_incidencia = intent.getStringExtra("no_incidencia_finalizada");
            no_poliza.setText(intent.getStringExtra("no_poliza"));
        }

        no_reporte.setText(no_incidencia);
        estado.setText(estado_atencion);
        cliente.setText(intent.getStringExtra("lugar_incidencia"));
        domicilio.setText(intent.getStringExtra("domicilio"));
        fecha_hora.setText(intent.getStringExtra("fecha") + " " + intent.getStringExtra("hora"));
        reportador.setText(intent.getStringExtra("reportador"));
        empleado.setText(intent.getStringExtra("empleado"));
        tipo.setText(intent.getStringExtra("tipo_incidencia"));
        detalles.setText(intent.getStringExtra("detalles"));
        descripcion.setText(intent.getStringExtra("descripcion"));

        String usuario = preferences.getString(getString(R.string.usuario_key), "");
        Log.d("main_s",estado_atencion+" "+intent.getStringExtra("rfc_empleado"));
        if(intent.getStringExtra("rfc_empleado") != null) {
            if (estado_atencion.equals("TERMINADO") == false && intent.getStringExtra("rfc_empleado").equals(usuario) == true) {
                String tmp = intent.getStringExtra("fecha");

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date fecha_inc = null;
                try {
                    fecha_inc = format.parse(tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date hoy = new Date();

                long diferencia = hoy.getTime() - fecha_inc.getTime();
                long segundos = diferencia / 1000;
                long minutos = segundos / 60;
                long horas = minutos / 60;
                long dias = horas / 24;

                if (dias > 1) {
                    final LinearLayout layout_btn_j = (LinearLayout) findViewById(R.id.layout_button_justificar);

                    if (layout_btn_j.getChildCount() == 0) {
                        Button btn_j;
                        btn_j = new Button(this);
                        btn_j.setId(1233);
                        btn_j.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                        btn_j.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        btn_j.setBackgroundColor(200);
                        btn_j.setText(R.string.btn_justificar);
                        btn_j.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent2 = new Intent(MainActivity.this, Responder_activity.class);
                                intent2.putExtras(intent.getExtras());
                                startActivity(intent2);
                                finish();
                            }
                        });

                        layout_btn_j.addView(btn_j);
                    }
                }
            }
        }

        if(estado_atencion.equals("TERMINADO") == true && intent.hasExtra("soluciones") == true){
            LinearLayout layout_soluciones = (LinearLayout) findViewById(R.id.layout_soluciones);
           TextView periodo_atencion = (TextView) findViewById(R.id.tiempo_tardo);
           periodo_atencion.setTextColor(Color.parseColor("#FF5733"));
            periodo_atencion.setText("El reporte de incidencia tardo "+intent.getStringExtra("duracion_atencion")+" en ser terminada.");
            String[] soluciones_incidencias = intent.getStringExtra("soluciones").split(";");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView title_solucion = new TextView(this);
            title_solucion.setLayoutParams(params);
            title_solucion.setTextSize(20);
            title_solucion.setTextColor(Color.parseColor("#138D75"));
            title_solucion.setText("SOLUCIONES BRINDADAS");
            layout_soluciones.addView(title_solucion);
            for (int j = 0; j < soluciones_incidencias.length; j++){
                TextView view_solucion = new TextView(this);
                view_solucion.setLayoutParams(params);
                view_solucion.setTextColor(Color.parseColor("#21618C"));
                view_solucion.setText(soluciones_incidencias[j].toString());
                view_solucion.setTop(20);
                //view_solucion.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                //view_solucion.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                layout_soluciones.addView(view_solucion);
            }
            Log.d("NO. CHILD LAYOUT",String.valueOf(layout_soluciones.getChildCount()));
        }

       if(estado_atencion.equals("REGISTRADO") == true) {
           final LinearLayout layout_button = (LinearLayout) findViewById(R.id.layout_button_tomar);

           if(layout_button.getChildCount() == 0){
               Button tomar_incidencia;
               tomar_incidencia = new Button(this);
               tomar_incidencia.setId(1234);
               tomar_incidencia.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
               tomar_incidencia.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
               tomar_incidencia.setBackgroundColor(200);
               tomar_incidencia.setText(R.string.texto_button);
               tomar_incidencia.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(MainActivity.this);
                       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                       String usuario = preferences.getString(getString(R.string.usuario_key), "");
                       if (!usuario.equals("")) {
                           asignar_incidencia(usuario, Integer.parseInt(no_incidencia));
                       }

                   }
               });

               layout_button.addView(tomar_incidencia);
           }
       }

    }
    @Override
    protected void onStart() {
        super.onStart();
       // muestraDatos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        muestraDatos();
    }
    protected void asignar_incidencia(final String usuario, final int no_incidencia){
        class Peticion extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection conexion;
                URL url = null;
                OutputStreamWriter peticion = null;
                String respuesta = null;
                String parametros = "usuario="+usuario+"&no_incidencia="+no_incidencia;
                try {
                    url = new URL("http://control.sicanetsc.com/asigna_empleado");
                    conexion = (HttpURLConnection) url.openConnection();
                    conexion.setDoOutput(true);
                    conexion.setRequestMethod("POST");
                    peticion = new OutputStreamWriter(conexion.getOutputStream());
                    peticion.write(parametros);
                    peticion.flush();
                    peticion.close();
                    InputStreamReader isr = new InputStreamReader(conexion.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    // Response from server after login process will be stored in response variable.
                    respuesta = sb.toString();
                    isr.close();
                    reader.close();

                }catch(Exception e){
                    e.printStackTrace();
                }
                return respuesta;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG,s);
                if(s.contains("ASIGNADO")){
                    LinearLayout layout_button = (LinearLayout) findViewById(R.id.layout_button_tomar);
                    incidenciasDbHelper.cambiarEstadoIncidencia(no_incidencia, "ASIGNADO");
                    Toast toast1 = Toast.makeText(getApplicationContext(), "¡Se le ha signado exitosamente la incidecia no." + no_incidencia, Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    estado.setText("ASIGNADO");
                   Button btn_tomar = (Button) layout_button.findViewById(1234);
                   layout_button.removeView(btn_tomar);
                }else if(s.contains("Esta incidencia ya fue asignada.")){
                    LinearLayout layout_button = (LinearLayout) findViewById(R.id.layout_button_tomar);
                    incidenciasDbHelper.cambiarEstadoIncidencia(no_incidencia, "ASIGNADO");
                    Toast toast1 = Toast.makeText(getApplicationContext(), "¡La incidencia ya se le asignado a alguien más!", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    estado.setText("ASIGNADO");
                    Button btn_tomar = (Button) layout_button.findViewById(1234);
                    layout_button.removeView(btn_tomar);
                }

            }
        }
        Peticion peticion = new Peticion();
        peticion.execute(usuario,String.valueOf(no_incidencia));
    }

    public boolean guardaIncidencia(Bundle data){
        IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(this);
        int no_reporte = Integer.parseInt(String.valueOf(data.get("no_incidencia")));
        String lugar = data.getString("lugar_incidencia");
        String domicilio = data.getString("domicilio");
        String reportador = data.getString("reportador");
        String tipo = data.getString("tipo_incidencia");
        String detalles = data.getString("detalles");
        String descripcion = data.getString("descripcion");
        String estado = data.getString("estado_atencion");
        String fecha = data.getString("fecha");
        String hora = data.getString("hora");
        int no_poliza = Integer.parseInt(String.valueOf(data.get("no_poliza")));
        String empleado = data.getString("empleado");
        String rfc_empleado = data.getString("rfc_empleado");

        Incidencia incidencia = new Incidencia(no_reporte,lugar,domicilio,reportador,tipo,detalles,descripcion,estado,fecha,hora,empleado,no_poliza,rfc_empleado);
        long exito = incidenciasDbHelper.insertarIncidencia(incidencia);
        if(exito > -1){
            return true;
        }else{
            return false;
        }
    }
}
