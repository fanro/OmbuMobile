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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TicketsTecnico extends AppCompatActivity implements AsyncResponse {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_tecnico);

        traerTicketsTecnico();
    }

    @Override
    public void onRestart() {
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        super.onRestart();
        finish();
        startActivity(getIntent());
        //traerTicketsTecnico();
    }

    private void traerTicketsTecnico() {
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_TICKETS};
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
                JSONArray ticketsArray = new JSONArray(data.getString("tickets"));
                Ticket[] tickets = parsearTickets(ticketsArray);
                final ArrayAdapter<Ticket> adapter = new ArrayAdapter<Ticket>(this, R.layout.list_item_ticket, tickets);
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);


                list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final Ticket ticket = adapter.getItem(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(TicketsTecnico.this);

                        final TextView message = new TextView(TicketsTecnico.this);
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
                        builder.setPositiveButton("Editar Ticket",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        editarTicket(ticket);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Ticket[] parsearTickets(JSONArray data){

        Ticket[] tickets;
        tickets = new Ticket[data.length()];
        try {
        for (int i = 0; i < data.length(); i++) {
            Ticket tic = new Ticket(data.getJSONObject(i).getString("ticket"),
                    data.getJSONObject(i).getString("cliente"),
                    data.getJSONObject(i).getString("dependencia"),
                    data.getJSONObject(i).getString("lugar"),
                    data.getJSONObject(i).getString("problema"),
                    data.getJSONObject(i).getString("comentario"),
                    data.getJSONObject(i).getString("bien"),
                    data.getJSONObject(i).getString("inventario"),
                    data.getJSONObject(i).getString("estado"),
                    data.getJSONObject(i).getString("prioridad"),
                    data.getJSONObject(i).getString("supervisor"));

            tickets[i] = tic;
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public void editarTicket(Ticket ticket) {
        Intent intent = new Intent(this, EditarTicket.class);
        intent.putExtra("Ticket", (Serializable) ticket);
        startActivity(intent);
    }

}
