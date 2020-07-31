package com.example.aritzi.notificacionsicanetv2;
        import android.os.Bundle;
        import android.util.Log;
        import com.firebase.jobdispatcher.JobParameters;
        import com.firebase.jobdispatcher.JobService;

        import java.util.Map;

public class MyJobService extends JobService {
    private IncidenciasDbHelper incidenciasDbHelper = new IncidenciasDbHelper(this);
    private static String TAG = "MAIN**";
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG,"EN JOBSERVICE");
        Bundle data = job.getExtras();
        int no_reporte = data.getInt("no_incidencia");
        String lugar = data.getString("lugar_incidencia");
        String domicilio = data.getString("domicilio");
        String reportador = data.getString("reportador");
        String tipo = data.getString("tipo_incidencia");
        String detalles = data.getString("detalles");
        String descripcion = data.getString("descripcion");
        String estado = data.getString("estado_atencion");
        String fecha = data.getString("fecha");
        String hora = data.getString("hora");
        int no_poliza = data.getInt("no_poliza");
        String empleado = data.getString("empleado");
        String rfc_empleado = data.getString("rfc_empleado");

        Incidencia incidencia = new Incidencia(no_reporte,lugar,domicilio,reportador,tipo,detalles,descripcion,estado,fecha,hora,empleado,no_poliza,rfc_empleado);
        incidenciasDbHelper.insertarIncidencia(incidencia);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
