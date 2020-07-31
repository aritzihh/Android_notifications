package com.example.aritzi.notificacionsicanetv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class IncidenciasDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sicanets_control";

    public IncidenciasDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + IncidenciaContract.IncidenciaEntry.TABLE_NAME + "("
                                + IncidenciaContract.IncidenciaEntry.NO_INCIDENCIA+" INTEGER PRIMARY KEY, "
                                + IncidenciaContract.IncidenciaEntry.REPORTADOR + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.DESCRIPCION + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.FECHA + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.HORA + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.NOPOLIZA+" INTEGER , "
                                + IncidenciaContract.IncidenciaEntry.EMPLEADO_REGISTRO + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.ESTADO_ATENCION + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.TIPO_INCIDENCIA + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.LUGAR_INCIDENCIA + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.DETALLES + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.DOMICILIO + " TEXT NOT NULL, "
                                + IncidenciaContract.IncidenciaEntry.RFC_EMPLEADO + " TEXT  "
                                + " );"
                                );
    }
    public long insertarIncidencia( Incidencia incidencia){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.insert(IncidenciaContract.IncidenciaEntry.TABLE_NAME, null,incidencia.toContentValues());
    }

    public ArrayList<Incidencia> getListaIncidencias(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Incidencia> lista_incidencias =  new ArrayList<Incidencia>();
        Cursor cursor = sqLiteDatabase.query(IncidenciaContract.IncidenciaEntry.TABLE_NAME,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                            );
        while(cursor.moveToNext()){
            lista_incidencias.add(this.creaIncidencia(cursor));
        }
        return lista_incidencias;
    }
    public Incidencia creaIncidencia(Cursor c){
        int no_reporte = c.getInt(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.NO_INCIDENCIA));
        String reportador = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.REPORTADOR));
        String descripcion = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.DESCRIPCION));
        String fecha = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.FECHA));
        String hora = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.HORA));
        int no_poliza = c.getInt(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.NOPOLIZA));
        String empleado = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.EMPLEADO_REGISTRO));
        String estado = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.ESTADO_ATENCION));
        String tipo = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.TIPO_INCIDENCIA));
        String lugar = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.LUGAR_INCIDENCIA));
        String detalles = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.DETALLES));
        String domicilio = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.DOMICILIO));
        String rfc_empleado = c.getString(c.getColumnIndex(IncidenciaContract.IncidenciaEntry.RFC_EMPLEADO));

        Incidencia incidencia = new Incidencia(no_reporte,lugar,domicilio,reportador,tipo,detalles,descripcion,estado,fecha,hora,empleado,no_poliza,rfc_empleado);
        return  incidencia;
    }

    public boolean cambiarEstadoIncidencia(int no_incidencia,String nuevoEstado){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(IncidenciaContract.IncidenciaEntry.ESTADO_ATENCION,nuevoEstado);
        String selection = IncidenciaContract.IncidenciaEntry.NO_INCIDENCIA +" = "+no_incidencia;
        int filas_afectadas = sqLiteDatabase.update(IncidenciaContract.IncidenciaEntry.TABLE_NAME,
                                                    values,
                                                    selection,
                                                    null
                                                    );
        if(filas_afectadas > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean actualizaIncidencia(int no_incidencia){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = IncidenciaContract.IncidenciaEntry.NO_INCIDENCIA + " = " + no_incidencia;
        int rows_affected = db.delete(IncidenciaContract.IncidenciaEntry.TABLE_NAME,
                    selection,
                    null
                );
        if(rows_affected > 0){
            return true;
        }else{
            return false;
        }
    }
    public boolean existeIncidencia(int no_incidencia){
        boolean existe = false;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(IncidenciaContract.IncidenciaEntry.TABLE_NAME,
                                                null,
                                                IncidenciaContract.IncidenciaEntry.NO_INCIDENCIA+"="+String.valueOf(no_incidencia),
                                                null,
                                                null,
                                                null,
                                                null
                );

        if(cursor.getCount() > 0){
            existe = true;
        }
        return existe;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
