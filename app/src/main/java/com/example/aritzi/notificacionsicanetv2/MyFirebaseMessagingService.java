package com.example.aritzi.notificacionsicanetv2;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


        import android.app.*;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.sqlite.SQLiteDatabase;
        import android.media.RingtoneManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.support.v4.app.NotificationCompat;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;

        import com.firebase.jobdispatcher.FirebaseJobDispatcher;
        import com.firebase.jobdispatcher.GooglePlayDriver;
        import com.firebase.jobdispatcher.Job;
        import com.google.firebase.messaging.FirebaseMessagingService;
        import com.google.firebase.messaging.RemoteMessage;

        import java.util.Iterator;
        import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static  final String TAG = "NOTICIAS";
    private static RemoteMessage remoteMessage;
    private IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(this);
    private boolean incidenciaFinalizada = false;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            this.remoteMessage = remoteMessage;
            if(remoteMessage.getNotification() != null){
                Log.d(TAG, remoteMessage.getNotification().getBody());
            }
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, remoteMessage.getData().toString());
                Bundle bundle = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    bundle.putString(entry.getKey(), entry.getValue());
                }
                this.sendBroadcast(bundle);
                String title ="";
                String body = "";
                if(remoteMessage.getData().containsKey("no_incidencia")){
                    if(incidenciasDbHelper.existeIncidencia(Integer.parseInt(remoteMessage.getData().get("no_incidencia")))){
                        title = "Recordatorio";
                        body = "La incidencia lleva "+remoteMessage.getData().get("tiempo_atencion")+" y no ha sido terminada";
                    }else {
                        title = "Nuevo reporte de incidencia";
                        body = "Toca para ver los detalles";
                        this.almacenaDatos(remoteMessage.getData());
                    }
                        enviarNotificacion(title, body, bundle);
                }else if(remoteMessage.getData().containsKey("no_incidencia_finalizada")){
                    title = "Reporte de incidencia terminada";
                    body = "Toca para ver los detalles";
                    this.actualizaDatos(remoteMessage.getData());
                    enviarNotificacion(title, body, bundle);
                }

            }


    }

    private void scheduleJob() {
        // [START dispatch_job]
        Bundle datos = new Bundle();
        Map<String, String> data = remoteMessage.getData();
        datos.putInt("no_incidencia",Integer.parseInt(data.get("no_incidencia")));
        datos.putString("lugar_incidencia",data.get("lugar_incidencia"));
        datos.putString("domicilio",data.get("docmicilio"));
        datos.putString("reportador",data.get("reportador"));
        datos.putString("tipo_incidencia",data.get("tipo_incidencia"));
        datos.putString("detalles",data.get("detalles"));
        datos.putString("descripcion",data.get("descripcion"));
        datos.putString("estado_atencion",data.get("estado_atencion"));
        datos.putString("fecha",data.get("fecha"));
        datos.putString("hora",data.get("hora"));
        datos.putInt("no_poliza",Integer.parseInt(data.get("no_poliza")));
        datos.putString("empleado",data.get("empleado"));

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .setExtras(datos)
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }
    private boolean almacenaDatos(Map<String, String> data){
        int no_reporte = Integer.parseInt(data.get("no_incidencia"));
        String lugar = data.get("lugar_incidencia");
        String domicilio = data.get("domicilio");
        String reportador = data.get("reportador");
        String tipo = data.get("tipo_incidencia");
        String detalles = data.get("detalles");
        String descripcion = data.get("descripcion");
        String estado = data.get("estado_atencion");
        String fecha = data.get("fecha");
        String hora = data.get("hora");
        int no_poliza = Integer.parseInt(data.get("no_poliza"));
        String empleado = data.get("empleado");
        String rfc_empleado = data.get("rfc_empleado");

        Incidencia incidencia = new Incidencia(no_reporte,lugar,domicilio,reportador,tipo,detalles,descripcion,estado,fecha,hora,empleado,no_poliza,rfc_empleado);
        long exito = incidenciasDbHelper.insertarIncidencia(incidencia);
        if(exito > -1){
            return true;
        }else{
            return false;
        }
    }
    private boolean actualizaDatos(Map<String, String> data){
        int no_reporte = Integer.parseInt(data.get("no_incidencia_finalizada"));
        boolean exito = incidenciasDbHelper.actualizaIncidencia(no_reporte);
        if(exito){
            return true;
        }else{
            return false;
        }
    }
    private void enviarNotificacion(String title,String body, Bundle datos) {
        Intent intent ;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String usuario = preferences.getString(getString(R.string.usuario_key),"no hay");
        if(title.equals("Recordatorio") && datos.get("rfc_empleado").equals(usuario) == true) {
            intent = new Intent(this, Responder_activity.class);
        }else{
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(datos);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.android1)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void sendBroadcast(Bundle datos) {
        Intent intent = new Intent("nueva-notificacion-recibida");
        intent.putExtras(datos);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}