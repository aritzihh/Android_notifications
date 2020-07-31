package com.example.aritzi.notificacionsicanetv2;

import android.content.ContentValues;

public class Incidencia {
    private int no_reporte;
    private String lugar;
    private String domicilio;
    private String reportador;
    private String tipo;
    private String detalles;
    private String descripcion;
    private String estado;
    private String fecha;
    private String hora;
    private String empleado_que_registro;
    private int no_poliza;
    private String rfc_empleado;

    public Incidencia(int no_reporte, String lugar,String domicilio, String reportador, String tipo, String detalles, String descripcion, String estado, String fecha, String hora, String empleado_registro, int no_poliza, String rfc_empleado){
        this.no_reporte = no_reporte;
        this.lugar = lugar;
        this.domicilio = domicilio;
        this.reportador = reportador;
        this.tipo = tipo;
        this.detalles =detalles;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
        this.hora = hora;
        this.empleado_que_registro = empleado_registro;
        this.no_poliza = no_poliza;
        this.rfc_empleado = rfc_empleado;
    }

    public int getNoReporte(){
        return no_reporte;
    }
    public void setNoReporte(int no_reporte){
        this.no_reporte = no_reporte;
    }
    public String getLugar(){
        return lugar;
    }
    public void setLugar(String lugar){
        this.lugar = lugar;
    }
    public String getDomicilio(){
        return domicilio;
    }
    public void setDomicilio(String domicilio){
        this.domicilio = domicilio;
    }
    public String getReportador(){
        return reportador;
    }
    public void setReportador(String reportador){
        this.reportador = reportador;
    }
    public String getDetalles(){
        return detalles;
    }
    public void setDetalles(String detalles){
        this.detalles = detalles;
    }
    public String getTipo(){
        return tipo;
    }
    public void setTipo(String tipo){
        this.tipo = tipo;
    }
    public String getDescripcion(){
        return descripcion;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    public String getEstado(){
        return estado;
    }
    public void setEstado(String estdo){
        this.estado = estado;
    }
    public String getFecha(){
        return fecha;
    }
    public void setFecha(String fecha){
        this.fecha = fecha;
    }
    public String getHora(){
        return hora;
    }
    public void setHora(String hora){
        this.hora = hora;
    }

    public String getEmpleado_que_registro() {
        return empleado_que_registro;
    }

    public void setEmpleado_que_registro(String empleado_que_registro) {
        this.empleado_que_registro = empleado_que_registro;
    }

    public int getNo_poliza() {
        return no_poliza;
    }

    public void setNo_poliza(int no_poliza) {
        this.no_poliza = no_poliza;
    }

    public String getRfc_empleado() {
        return rfc_empleado;
    }

    public void setRfc_empleado(String rfc_empleado) {
        this.rfc_empleado = rfc_empleado;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(IncidenciaContract.IncidenciaEntry.NO_INCIDENCIA, no_reporte);
        values.put(IncidenciaContract.IncidenciaEntry.REPORTADOR,reportador );
        values.put(IncidenciaContract.IncidenciaEntry.DESCRIPCION, descripcion);
        values.put(IncidenciaContract.IncidenciaEntry.FECHA, fecha);
        values.put(IncidenciaContract.IncidenciaEntry.HORA, hora);
        values.put(IncidenciaContract.IncidenciaEntry.NOPOLIZA, no_poliza);
        values.put(IncidenciaContract.IncidenciaEntry.EMPLEADO_REGISTRO, empleado_que_registro);
        values.put(IncidenciaContract.IncidenciaEntry.ESTADO_ATENCION, estado);
        values.put(IncidenciaContract.IncidenciaEntry.TIPO_INCIDENCIA, tipo);
        values.put(IncidenciaContract.IncidenciaEntry.LUGAR_INCIDENCIA, lugar);
        values.put(IncidenciaContract.IncidenciaEntry.DETALLES, detalles);
        values.put(IncidenciaContract.IncidenciaEntry.DOMICILIO, domicilio);
        values.put(IncidenciaContract.IncidenciaEntry.RFC_EMPLEADO, rfc_empleado);
        return values;
    }
}
