package com.example.aritzi.notificacionsicanetv2;

import android.provider.BaseColumns;

public class IncidenciaContract {
    public static abstract  class IncidenciaEntry implements BaseColumns{
        public static final String TABLE_NAME = "incidencia";

        public static final String NO_INCIDENCIA = "no_incidencia";
        public static final String REPORTADOR = "reportador";
        public static final String DESCRIPCION = "descripcion";
        public static final String FECHA = "fecha";
        public static final String HORA = "hora";
        public static final String NOPOLIZA = "no_poliza";
        public static final String EMPLEADO_REGISTRO = "empleado_registro";
        public static final String ESTADO_ATENCION = "atencion_atencion";
        public static final String TIPO_INCIDENCIA = "tipo_incidencia";
        public static final String LUGAR_INCIDENCIA = "lugar_incidencia";
        public static final String DETALLES = "detalles";
        public static final String DOMICILIO = "domicilio";
        public static final String RFC_EMPLEADO = "rfc_empleado";
    }
}
