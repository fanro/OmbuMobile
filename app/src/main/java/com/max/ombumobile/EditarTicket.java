package com.max.ombumobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private TextView textView_Fecha;
    private Spinner spinner_estados;
    private EditText editText_ComenTecnico;
    private Ticket ticket;
    private ImageView imagen_Ticket;
    private Bitmap bitmap;
    private static final String ESTADO = "**Nuevo Estado**";
    private Uri uri;

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
        textView_Fecha = (TextView) findViewById(R.id.textView_Fecha);
        imagen_Ticket = (ImageView) findViewById(R.id.imagen_Ticket);
        editText_ComenTecnico.setSelected(false);

        this.setValores(ticket);

        imagen_Ticket.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String ticketNro = ticket.getNumero("completo");
                StorageReference ticketImgRef = FirebaseStorage.getInstance().getReference().child(ticketNro).child(ticketNro + Utils.md5(ticketNro) + ".png");
                ticketImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(uri, "image/png");
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ERR_DOWNLOAD_IMG", "Error descargando la imagen del ticket");
                    }
                });

            }

        });
    }

    private void setValores(Ticket ticket) {
        textView_Ticket.setText(ticket.getNumero("completo") + " - " + ticket.getEstado());
        textView_Prioridad.setText("Prioridad: " + ticket.getPrioridad());
        textView_Fecha.setText("Fecha: " + ticket.getTstamp());
        textView_Cliente.setText("Cliente: " + ticket.getCliente());
        textView_Dependencia.setText("Dependencia: " + ticket.getDependencia());
        textView_Direccion.setText("Dirección: " + ticket.getLugar());
        textView_Incidente.setText("Incidente: " + ticket.getProblema());
        textView_Comentario.setText("Comentario: " + ticket.getComentario());

        final String ticketNro = ticket.getNumero("completo");

        StorageReference ticketImgRef = FirebaseStorage.getInstance().getReference().child(ticketNro).child(ticketNro + Utils.md5(ticketNro) + ".png");

        ticketImgRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagen_Ticket.setImageBitmap(bitmap);
                uri = getImageUri(EditarTicket.this, bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ERR_DOWNLOAD_IMG", "Error descargando la imagen del ticket " + ticketNro);
            }
        });

        spinner_estados = (Spinner) findViewById(R.id.spinner_estados);
        List<String> list = new ArrayList<String>();
        list.add(ESTADO);
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
        if(verificarDatos()){
            RestHandler rh = new RestHandler();
            String[] passing = {"POST", rh.REST_ACTION_EDITAR_TICKET, ticket.getNumero(""), spinner_estados.getSelectedItem().toString(), editText_ComenTecnico.getText().toString(), ticket.getSupervisor()};
            rh.setActivity(this);
            rh.execute(passing);
        } else {
            Toast toast = Toast.makeText(getBaseContext(), "Complete Nuevo Estado/Comentario", Toast.LENGTH_SHORT);
            toast.show();
        }
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean verificarDatos() {
        if (spinner_estados.getSelectedItem().toString().equals(ESTADO) ||
                editText_ComenTecnico.getText().toString().equals("")){
            return false;
        }
        return true;
    }

}
