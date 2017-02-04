package com.max.ombumobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BusquedaContactos extends AppCompatActivity implements AsyncResponse {

    private EditText editText_apellido;
    private EditText editText_nombre;
    private AutoCompleteTextView editText_dependencia;
    private String[] dependencias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_contactos);

        editText_apellido = (EditText) findViewById(R.id.editText_apellido);
        editText_nombre = (EditText) findViewById(R.id.editText_nombre);
        editText_dependencia = (AutoCompleteTextView) findViewById(R.id.editText_dependencia);

        if (dependencias == null){
            inicializarAutocomplete();
        }
    }

    public void buscarContacto(View view) {

//        if(!this.validacionEditText(editText_apellido) && !this.validacionEditText(editText_nombre)
//                && !this.validacionEditText(editText_dependencia)){
//            Toast toast = Toast.makeText(getBaseContext(), Constantes.LlenarCampos, Toast.LENGTH_SHORT);
//            toast.show();
//        }else{
            Intent intent = new Intent(this, ResultadoContactos.class);
            Bundle datos = new Bundle();
            datos.putString("apellido",editText_apellido.getText().toString());
            datos.putString("nombre",editText_nombre.getText().toString());
            datos.putString("dependencia",editText_dependencia.getText().toString());
            intent.putExtras(datos);
            startActivity(intent);
//        }

    }

    private void inicializarAutocomplete() {
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_GET_DEPEN};
        rh.setActivity(this);
        rh.execute(passing);
    }

    public void processFinish(String output){
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
                JSONArray dependenciasArray = new JSONArray(data.getString("dependencias"));
                dependencias = new String[dependenciasArray.length()];
                for (int i = 0; i < dependenciasArray.length(); i++){
                    dependencias[i] = dependenciasArray.getJSONObject(i).getString("nombre");
                }
                setDependencias();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDependencias() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_depen, dependencias);
        editText_dependencia.setAdapter(adapter);

        editText_dependencia.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3){
                // sacamos el teclado
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText_dependencia.getWindowToken(), 0);
            }
        });
    }
}
