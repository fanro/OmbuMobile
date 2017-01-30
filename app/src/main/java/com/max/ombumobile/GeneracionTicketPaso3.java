package com.max.ombumobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeneracionTicketPaso3 extends AppCompatActivity  implements AsyncResponse {

    private Button button_AdjuntarFoto;
    private Button button_GenerarTicket;
    private Spinner spinner_Prioridad;
    private static final String PRIORIDAD = "**Prioridad**";
    private static final String PRIORIDAD_1 = "1 - Muy alta";
    private static final String PRIORIDAD_2 = "2 - Alta";
    private static final String PRIORIDAD_3 = "3 - Normal";
    private static final String PRIORIDAD_4 = "4 - Baja";
    private static final String PRIORIDAD_5 = "5 - Muy Baja";
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imagen_Camara;
    private EditText editText_ComentarioTicket;
    private NuevoTicket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generacion_ticket_paso3);

        inicializarVista();

    }

    protected void inicializarVista(){

        Intent intent = getIntent();
        ticket = (NuevoTicket) intent.getSerializableExtra("NuevoTicket");
        button_AdjuntarFoto = (Button)findViewById(R.id.button_AdjuntarFoto);
        button_GenerarTicket = (Button)findViewById(R.id.button_GenerarTicket);
        spinner_Prioridad = (Spinner)findViewById(R.id.spinner_Prioridad);
        imagen_Camara = (ImageView) findViewById(R.id.imagen_Camara);
        editText_ComentarioTicket = (EditText) findViewById(R.id.editText_ComentarioTicket);

        List<String> list = new ArrayList<String>();
        list.add(PRIORIDAD);
        list.add(PRIORIDAD_1);
        list.add(PRIORIDAD_2);
        list.add(PRIORIDAD_3);
        list.add(PRIORIDAD_4);
        list.add(PRIORIDAD_5);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner_Prioridad.setAdapter(dataAdapter);

        button_AdjuntarFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imagen_Camara.setImageBitmap(photo);
        }
    }

    public void generarTicket(View view) {

        if(verificarDatos()){
            ticket.setPrioridad(getPrioridad());
            ticket.setComentario(editText_ComentarioTicket.getText().toString());
            crearTicket();
        } else {
            Toast toast = Toast.makeText(getBaseContext(), "Complete Prioridad/Comentario", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void crearTicket() {
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_NUEVO_TICKET, ticket.getIncidente(), ticket.getComentario(), ticket.getBienDescripcion(),
                                ticket.getInventariado(), ticket.getNroInventario(), ticket.getPrioridad()};
        rh.setActivity(this);
        rh.execute(passing);
    }

    private void subirFoto(String nroTicket){
        String numeroTicket = nroTicket;

        BitmapDrawable drawable = (BitmapDrawable) imagen_Camara.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bitmapdataArray = baos.toByteArray();

        StorageReference fStorage = FirebaseStorage.getInstance().getReference().child(numeroTicket).child(numeroTicket + Utils.md5(numeroTicket) + ".png");
        UploadTask uploadTask = fStorage.putBytes(bitmapdataArray);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // TODO: ver que hacer acá
                Log.e(getString(R.string.app_name),"LA SUBI");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO: ver que hacer aca
                Log.e(getString(R.string.app_name), e.getMessage());
            }
        });
    }

    private boolean verificarDatos() {
        if (spinner_Prioridad.getSelectedItem().toString().equals(PRIORIDAD) ||
                editText_ComentarioTicket.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    private String getPrioridad(){
        switch (spinner_Prioridad.getSelectedItem().toString()){
            case PRIORIDAD_1:
                return "1";
            case PRIORIDAD_2:
                return "2";
            case PRIORIDAD_3:
                return "3";
            case PRIORIDAD_4:
                return "4";
            case PRIORIDAD_5:
                return "5";
            default:
                return "";
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
                JSONObject data = new JSONObject(obj.getString("data"));
                // tengo foto??
                if (imagen_Camara.getDrawable() != null){
                    subirFoto(data.getString("ticket"));
                }
                cerrarActivity(data.getString("ticket"));
            } else{
                Toast toast = Toast.makeText(getBaseContext(), obj.getString("message") , Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void cerrarActivity(String ticket){
        Intent data = new Intent();
        String text = ticket;
        //---set the data to pass back---
        data.setData(Uri.parse(text));
        setResult(RESULT_OK, data);
        //---close the activity---
        finish();
    }

}
