package com.example.aritzi.notificacionsicanetv2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Responder_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = this.getIntent();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView lno_incidencia = (TextView)findViewById(R.id.reporte_no);
        lno_incidencia.setText(intent.getExtras().get("no_incidencia").toString());
        final EditText mensaje = (EditText)findViewById(R.id.mensaje);
        TextView explicacion = (TextView) findViewById(R.id.explicacion);
        String info = intent.getExtras().get("lugar_incidencia").toString()+"("+intent.getExtras().getString("fecha")+")\nDetalles: "+intent.getExtras().getString("detalles")
                        +"\n"+intent.getExtras().getString("descripcion");
        explicacion.setText(info);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String texto_mensaje = mensaje.getText().toString();
                TextView lno_incidencia = (TextView)findViewById(R.id.reporte_no);
                final String no_incidencia = lno_incidencia.getText().toString();
                enviar_justificacion(texto_mensaje,no_incidencia);
            }
        });
    }
    protected void enviar_justificacion(final String mensaje,final String no_incidencia){
        class Peticion extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection conexion;
                URL url = null;
                OutputStreamWriter peticion = null;
                String respuesta = null;
                String parametros = "mensaje="+mensaje+"&no_incidencia="+no_incidencia;
                try {
                    url = new URL("http://control.sicanetsc.com/recibir_respuesta");
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
                if(s.contains("true")){
                    Intent intent = new Intent(Responder_activity.this, Incidencias_pendientes.class);
                    intent.putExtra("justificado","yes");
                    startActivity(intent);
                    finish();
                }
            }
        }
        Peticion peticion = new Peticion();
        peticion.execute(mensaje,no_incidencia);
    }
}
