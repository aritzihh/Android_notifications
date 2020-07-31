package com.example.aritzi.notificacionsicanetv2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IncidenciasAdapter extends BaseAdapter {
    private Context context;
    private ArrayList datos;

    public IncidenciasAdapter(Context context, ArrayList datos){
        this.context = context;
        this.datos = datos;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int i) {
        return datos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View item = convertView;
        if (convertView == null) {
            LayoutInflater inf = LayoutInflater.from(context);
            item = inf.inflate(R.layout.layout_item, null);
        }


        Incidencia incidencia = (Incidencia)datos.get(position);

        Log.d(String.valueOf(incidencia.getNoReporte()),incidencia.getLugar());
        TextView titulo = (TextView) item.findViewById(R.id.title_item);
        titulo.setText(incidencia.getLugar());

        TextView subtitulo = (TextView) item.findViewById(R.id.subtitle_item);
        subtitulo.setText(incidencia.getFecha()+" "+incidencia.getHora());

        return item;
    }
}