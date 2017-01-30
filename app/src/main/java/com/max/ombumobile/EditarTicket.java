package com.max.ombumobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditarTicket extends AppCompatActivity implements AsyncResponse {

    private TextView textView_Ticket;
    private TextView textView_Prioridad;
    private TextView textView_Cliente;
    private TextView textView_Dependencia;
    private TextView textView_Direccion;
    private TextView textView_Incidente;
    private TextView textView_Comentario;
    private Spinner spinner_estados;
    private EditText editText_ComenTecnico;
    private Ticket ticket;
    private ImageView imagen_Ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ticket);

        Intent intent = getIntent();
        ticket = (Ticket) intent.getSerializableExtra("Ticket");
        textView_Ticket = (TextView) findViewById(R.id.textView_Ticket);
        textView_Prioridad = (TextView) findViewById(R.id.textView_Prioridad);
        textView_Cliente = (TextView) findViewById(R.id.textView_Cliente);
        textView_Dependencia = (TextView) findViewById(R.id.textView_Dependencia);
        textView_Direccion = (TextView) findViewById(R.id.textView_Direccion);
        textView_Incidente = (TextView) findViewById(R.id.textView_Incidente);
        textView_Comentario = (TextView) findViewById(R.id.textView_Comentario);
        editText_ComenTecnico = (EditText) findViewById(R.id.editText_ComenTecnico);
        imagen_Ticket = (ImageView) findViewById(R.id.imagen_Ticket);
        editText_ComenTecnico.setSelected(false);

        this.setValores(ticket);

    }

    private void setValores(Ticket ticket) {
        textView_Ticket.setText(ticket.getNumero("completo") + " - " + ticket.getEstado());
        textView_Prioridad.setText("Prioridad: " + ticket.getPrioridad());
        textView_Cliente.setText("Cliente: " + ticket.getCliente());
        textView_Dependencia.setText("Dependencia: " + ticket.getDependencia());
        textView_Direccion.setText("Dirección: " + ticket.getLugar());
        textView_Incidente.setText("Incidente: " + ticket.getProblema());
        textView_Comentario.setText("Comentario: " + ticket.getComentario());

        spinner_estados = (Spinner) findViewById(R.id.spinner_estados);
        List<String> list = new ArrayList<String>();
        list.add(" ");
        list.add("EN PROCESO");
        list.add("EN ESPERA");
        list.add("CERRADO POR TECNICO");
        list.add("INTERVENCION OPERADOR");
        list.add("INTERVENCION SUPERVISOR");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner_estados.setAdapter(dataAdapter);
    }

    public void editar_ticket(View view){

        //TODO chequear campos

        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_EDITAR_TICKET, ticket.getNumero(""), spinner_estados.getSelectedItem().toString(), editText_ComenTecnico.getText().toString(), ticket.getSupervisor()};
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
                Toast toast = Toast.makeText(getBaseContext(), obj.getString("status") + ": " + obj.getString("message"), Toast.LENGTH_SHORT);
                toast.show();
                // vuelvo
                onBackPressed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
