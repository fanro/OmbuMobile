package com.max.ombumobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TicketsUsuario extends AppCompatActivity implements AsyncResponse {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_usuario);

        Bundle parametros = getIntent().getExtras();
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_GET_TICKETS_USR, parametros.getString("modo")};
        rh.setActivity(this);
        rh.execute(passing);

    }

    public void processFinish(String output){

        JSONObject obj = null;
        String message = "";
        try {
            obj = new JSONObject(output);
            Log.d(getString(R.string.app_name), obj.toString());
        } catch (Throwable t) {
            Log.e(getString(R.string.app_name), "Could not parse malformed JSON: \"" + output + "\"");
        }

        try {
            if(obj.getString("status").equals( "OK")){
                // guardo msj para mostrar, si no encuentra los campos
                // del json, tira exception
                message = obj.getString("message");
                JSONObject data = new JSONObject(obj.getString("data"));
                JSONArray ticketsArray = new JSONArray(data.getString("tickets"));
                Ticket[] tickets = Ticket.parsearTickets(ticketsArray);
                renderTickets(tickets);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getBaseContext(), message , Toast.LENGTH_SHORT);
            toast.show();
            //vuelvo
            super.onBackPressed();
        }

    }

    private void renderTickets(Ticket[] tickets){

        final ArrayAdapter<Ticket> adapter = new ArrayAdapter<Ticket>(this, R.layout.list_item_ticket, tickets);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Ticket ticket = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(TicketsUsuario.this);

                final TextView message = new TextView(TicketsUsuario.this);
                message.setTextSize(20);
                message.setLinksClickable(true);
                message.setSelected(true);
                message.setMovementMethod(LinkMovementMethod.getInstance());

                StringBuilder msg = new StringBuilder();
                msg.append(ticket.getNumero("completo")+ "\n");
                msg.append("Problema:\n" + ticket.getProblema()+ "\n\n");
                msg.append("Comentario:\n" + ticket.getComentario()+ "\n\n");
                message.setText(msg);

                builder.setTitle("")
                        .setView(message)
                        .setCancelable(false);

                builder.setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setPositiveButton("Ver ticket",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                verTicket(ticket);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    public void verTicket(Ticket ticket) {
        Intent intent = new Intent(this, VerTicket.class);
        intent.putExtra("Ticket", (Serializable) ticket);
        startActivity(intent);
    }
}
