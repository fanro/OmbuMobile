package com.max.ombumobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeneracionTicketPaso2 extends AppCompatActivity implements AsyncResponse{

    private Spinner spinner_Area;
    private Spinner spinner_Seccion;
    private Spinner spinner_Problema;
    private Button button_Siguiente;
    private Boolean area = true;
    private Boolean seccion = false;
    private Boolean problema = false;
    private static final String AREA = "**Área**";
    private static final String SECCION = "**Sección**";
    private static final String PROBLEMA = "**Problema**";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generacion_ticket_paso2);

        spinner_Area = (Spinner)findViewById(R.id.spinner_Area);
        spinner_Seccion = (Spinner)findViewById(R.id.spinner_Seccion);
        spinner_Problema = (Spinner)findViewById(R.id.spinner_Problema);
        button_Siguiente = (Button)findViewById(R.id.button_Siguiente);

        spinner_Area.setEnabled(true);
        spinner_Seccion.setEnabled(false);
        spinner_Problema.setEnabled(false);
        button_Siguiente.setEnabled(false);
        inicializarSpinner();

        spinner_Area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                String selected = arg0.getItemAtPosition(pos).toString();
                if(!selected.equals(AREA)){
                    spinner_Area.setEnabled(false);
                    spinner_Seccion.setEnabled(true);
                    area = false;
                    seccion = true;
                    inicializarSpinner();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        spinner_Seccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                String selected = arg0.getItemAtPosition(pos).toString();
                if(!selected.equals(SECCION)){
                    spinner_Seccion.setEnabled(false);
                    spinner_Problema.setEnabled(true);
                    seccion = false;
                    problema = true;
                    button_Siguiente.setEnabled(true);
                    inicializarSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void inicializarSpinner() {
        RestHandler rh = new RestHandler();
        String [] passing = new String[5];
        passing[0] = "POST";
        passing[1] = rh.REST_ACTION_GET_INC;
        if(area){
            passing[2] = "true";
            passing[3] = "";
            passing[4] = "";
        }
        if(seccion){
            passing[2] = "";
            passing[3] = spinner_Area.getSelectedItem().toString();
            passing[4] = "";
        }
        if(problema){
            passing[2] = "";
            passing[3] = "";
            passing[4] = spinner_Seccion.getSelectedItem().toString();
        }
        rh.setActivity(this);
        rh.execute(passing);
    }

    @Override
    public void processFinish(String output) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(output);
            Log.d(getString(R.string.app_name), obj.toString());
        } catch (Throwable t) {
            Log.e(getString(R.string.app_name), "Could not parse malformed JSON: \"" + output + "\"");
        }

        try {
            if(obj.getString("status").equals( "OK")){
                JSONObject data = new JSONObject(obj.getString("data"));
                JSONArray incidentesArray = new JSONArray(data.getString("incidentes"));
                String[] incidentes = parsearIncidentes(incidentesArray);
                llenarSpinner(incidentes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void llenarSpinner(String[] incidentes) {
        if(this.area){
            llenarSpinnerData(spinner_Area, incidentes);
            return;
        }
        if(this.seccion){
            llenarSpinnerData(spinner_Seccion, incidentes);
            return;
        }
        if(this.problema){
            llenarSpinnerData(spinner_Problema, incidentes);
            return;
        }
    }

    private void llenarSpinnerData(Spinner spinner, String[] incidentes) {
        List<String> list = new ArrayList<String>();

        if(area){
            list.add(AREA);
        }
        if(seccion){
            list.add(SECCION);
        }
        if(problema){
            list.add(PROBLEMA);
        }

        for (int i = 0; i < incidentes.length; i++) {
            list.add(incidentes[i]);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }


    public static String[] parsearIncidentes(JSONArray data){
        String[] incidentes;
        incidentes = new String[data.length()];
        try {
            for (int i = 0; i < data.length(); i++) {
                incidentes[i] = data.getJSONObject(i).getString("descripcion");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return incidentes;
    }

    public void siguientePaso(View view){

        if(verificarDatos()){
            Intent intent = new Intent(GeneracionTicketPaso2.this, GeneracionTicketPaso3.class);
            startActivity(intent);
        }
        else {
            Toast toast = Toast.makeText(getBaseContext(), "Seleccione un problema válido", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean verificarDatos() {
        if (spinner_Problema.getSelectedItem().toString().equals(PROBLEMA)){
            return false;
        }
            return true;
    }

}
