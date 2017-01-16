package com.max.ombumobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultadoContactos extends AppCompatActivity implements AsyncResponse{

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_contactos);

        Bundle parametros = getIntent().getExtras();
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_GET_CONT, parametros.getString("apellido"),
                parametros.getString("nombre"), parametros.getString("dependencia")};
        rh.setActivity(this);
        rh.execute(passing);
    }

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
                JSONArray contactosArray = new JSONArray(data.getString("contactos"));
                Contacto[] contactos = parsearContactos(contactosArray);
                final ArrayAdapter<Contacto> adapter = new ArrayAdapter<Contacto>(this, R.layout.list_item_contactos, contactos);
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Contacto con = adapter.getItem(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ResultadoContactos.this);

                        final TextView message = new TextView(ResultadoContactos.this);
                        message.setTextSize(22);
                        message.setLinksClickable(true);
                        message.setSelected(true);
                        message.setMovementMethod(LinkMovementMethod.getInstance());

                        StringBuilder msg = new StringBuilder();
                        msg.append(con.getApellido()+ " " + con.getNombre());
                        msg.append("\n" + con.getEmail());
                        if(con.getTelefono_lab()!=""){
                            msg.append("\n" + con.getTelefono_lab());
                        }
                        if(con.getCelular_lab()!=""){
                            msg.append("\n" + con.getCelular_lab());
                        }
                        msg.append("\n" + con.getDependencia());

                        //Pattern patron = Pattern.compile("/^[0-9]{8,10}$/");  //15XXXXXXXX 5XXXXXXX

                        final SpannableString string = new SpannableString(msg);
                        Linkify.addLinks(string, Linkify.EMAIL_ADDRESSES);

                        //armo links telefonos a manopla, LINKIFY tiene problemas con numeros
                        //argentinos, otra posible solucion es armar la expresion regular de los telefonos
                        if(con.getTelefono_lab()!=""){
                            int inicio = con.getApellido().length() + 1 + con.getNombre().length()+ 1 + con.getEmail().length() + 1;
                            int fin = con.getApellido().length() + 1 + con.getNombre().length() + 1 + con.getEmail().length() + 1 + con.getTelefono_lab().length();
                            URLSpan span = new URLSpan("tel:" + con.getTelefono_lab());
                            string.setSpan(span,inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        if(con.getTelefono_lab()!="" && con.getCelular_lab()!=""){
                            int inicio = con.getApellido().length() + 1 + con.getNombre().length() + 1 + con.getEmail().length()
                                    + 1 + con.getTelefono_lab().length() + 1;
                            int fin = con.getApellido().length() + 1 + con.getNombre().length() + 1 + con.getEmail().length()
                                    + 1 + con.getTelefono_lab().length() + 1 + con.getCelular_lab().length();
                            URLSpan span = new URLSpan("tel:" + con.getCelular_lab());
                            string.setSpan(span,inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        message.setText(string);

                        builder.setTitle("")
                                .setView(message)
                                .setCancelable(false)
                                .setNeutralButton("ACEPTAR",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
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

    private Contacto[] parsearContactos(JSONArray data){

        Contacto[] contactos;
        contactos = new Contacto[data.length()];
        try {
            for (int i = 0; i < data.length(); i++) {
                Contacto con = new Contacto(data.getJSONObject(i).getString("apellido"),
                        data.getJSONObject(i).getString("nombre"),
                        data.getJSONObject(i).getString("email"),
                        data.getJSONObject(i).getString("telefono_lab"),
                        data.getJSONObject(i).getString("celular_lab"),
                        data.getJSONObject(i).getString("dependencia"));

                contactos[i] = con;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactos;
    }
}
