package com.example.aritzi.notificacionsicanetv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG ="Noticias";

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "CODIGO: "+token);
	    //FirebaseMessaging.getInstance().subscribeToTopic("all");
        sendRegistrationToServer(token);

    }
    private void sendRegistrationToServer(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getString(getString(R.string.usuario_key),"0").equalsIgnoreCase("0")) {
                this.sendTokenServer(preferences.getString(getString(R.string.usuario_key),"0"),token);
        }else if (preferences.getString(getString(R.string.usuario_key),"no existe").equalsIgnoreCase("no existe")) {
            Intent intent = new Intent(this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    public static void sendTokenServer(final String usuario, final String token){
        class Peticion extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection conexion;
                URL url = null;
                OutputStreamWriter peticion = null;
                String respuesta = null;
                String usuario = strings[0];
                String contrasena = strings[1];
                String parametros = "usuario="+usuario+"&token="+token;
                try {
                    url = new URL("http://control.sicanetsc.com/guardarTokenFirebase");
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
                Log.d(TAG, "Token: "+s);
            }
        }
        Peticion peticion = new Peticion();
        peticion.execute(usuario,token);
    }
}
