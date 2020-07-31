package com.example.aritzi.notificacionsicanetv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class Login extends Activity {
    private Button login;
    private EditText usuario;
    private EditText password;
    private TextView alert_login;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("USUARIO",preferences.getString(getString(R.string.usuario_key),"no hay"));
        if (!preferences.getString(getString(R.string.usuario_key),"no existe").equalsIgnoreCase("no existe")) {
            /*MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
            myFirebaseInstanceIDService.onTokenRefresh();*/
            Intent intent = new Intent(Login.this,Incidencias_pendientes.class);
            startActivity(intent);
        }else if (preferences.getString(getString(R.string.usuario_key),"no existe").equalsIgnoreCase("no existe")) {
            setContentView(R.layout.login);
            login = (Button) findViewById(R.id.button_login);
            usuario = (EditText) findViewById(R.id.usuario);
            password = (EditText) findViewById(R.id.contrasena);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String usuario_cuenta = usuario.getText().toString();
                    String contrasena_cuenta = password.getText().toString();
                    peticionServidor(usuario_cuenta, contrasena_cuenta);
                }
            });
        }
    }

    protected void peticionServidor(final String usuario, String contrasena){
        class Peticion extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection conexion = null;
                URL url = null;
                OutputStreamWriter peticion = null;
                String respuesta = null;
                String usuario = strings[0];
                String contrasena = strings[1];
                String parametros = "usuario="+usuario+"&contrasena="+contrasena;
                try {
                    url = new URL("http://control.sicanetsc.com/session_movil");
                    conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Length", String.valueOf(parametros.getBytes().length));
                    conexion.getOutputStream().write(parametros.getBytes());
                    int status = conexion.getResponseCode();
                    Log.d("estatus",String.valueOf(status));
                    InputStream ips;
                    if (status != HttpURLConnection.HTTP_OK)
                        ips = conexion.getErrorStream();
                    else
                        ips = conexion.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ips);
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
                }finally {
                    if(conexion!=null)
                        conexion.disconnect();
                }
                return respuesta;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("JSON",s);

                if(s.contains("true") == true){
                    String token = FirebaseInstanceId.getInstance().getToken();
                    MyFirebaseInstanceIDService.sendTokenServer(usuario,token);
                    alert_login = (TextView)findViewById(R.id.alert_login);
                    alert_login.setText("");
                    preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.usuario_key),usuario);
                    editor.commit();
                    try{
                        JSONObject json = new JSONObject(s);
                        if(json.has("incidencias")) {
                            JSONArray jsonArray = json.getJSONArray("incidencias");
                            almacenaDatos(jsonArray);
                        }
                    }catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    }
                    Intent intent = new Intent(Login.this,Incidencias_pendientes.class);
                    startActivity(intent);
                    finish();
                }else if(s.contains("false") == true){
                    alert_login = (TextView)findViewById(R.id.alert_login);
                    alert_login.setText(R.string.advertencia_datos_login);
                }
            }
        }
        Peticion peticion = new Peticion();
        peticion.execute(usuario,contrasena);
    }
    private void almacenaDatos(JSONArray data){
        IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(this);
        for (int i = 0; i < data.length(); i++) {
            try{
                JSONObject array = data.getJSONObject(i);
                int no_reporte = Integer.parseInt(array.getString("no_incidencia"));
                String descripcion = array.getString("descripcion");
                String fecha = array.getString("fecha");
                String hora = array.getString("hora");
                String reportador = array.getString("nombre_reportador")+" "+array.getString("apellido_paterno_reportador")+" "+array.getString("apellido_materno_reportador");
                int no_poliza = Integer.parseInt(array.getString("no_poliza"));
                String empleado = array.getString("empleado");
                String estado = array.getString("estado_atencion");
                String tipo = array.getString("tipo_incidencia");
                String lugar = array.getString("lugar_incidencia");
                String detalles = array.getString("detalles_incidencia");
                String domicilio = array.getString("domicilio");
                String rfc_empleado = array.getString("empleado_acargo");

                Incidencia incidencia = new Incidencia(no_reporte, lugar, domicilio, reportador, tipo, detalles, descripcion, estado, fecha, hora, empleado, no_poliza, rfc_empleado);
                incidenciasDbHelper.insertarIncidencia(incidencia);
            }catch (JSONException ex){
                Log.e("JSON Parser", "Error parsing data " + ex.toString());
            }
        }
    }
}
