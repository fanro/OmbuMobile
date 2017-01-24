package com.max.ombumobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeneracionTicketPaso3 extends AppCompatActivity {

    private Button button_AdjuntarFoto;
    private Button button_GenerarTicket;
    private Spinner spinner_Prioridad;
    private static final String PRIORIDAD = "**Prioridad**";
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imagen_Camara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generacion_ticket_paso3);

        inicializarVista();

    }

    protected void inicializarVista(){
        button_AdjuntarFoto = (Button)findViewById(R.id.button_AdjuntarFoto);
        button_GenerarTicket = (Button)findViewById(R.id.button_GenerarTicket);
        spinner_Prioridad = (Spinner)findViewById(R.id.spinner_Prioridad);
        imagen_Camara = (ImageView) findViewById(R.id.imagen_Camara);

        List<String> list = new ArrayList<String>();
        list.add(PRIORIDAD);
        list.add("1 - Muy alta");
        list.add("2 - Alta");
        list.add("3 - Normal");
        list.add("4 - Baja");
        list.add("5- Muy Baja");

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
        // TODO: Hacer generación del ticket (mhruiz)
        String numeroTicket = "123454";


        BitmapDrawable drawable = (BitmapDrawable) imagen_Camara.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapdataArray = baos.toByteArray();

        // TODO: cambiar Bleh
        StorageReference fStorage = FirebaseStorage.getInstance().getReference().child(numeroTicket).child(numeroTicket + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()));
        UploadTask uploadTask = fStorage.putBytes(bitmapdataArray);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // TODO: ver que hacer acá
                Log.e("Inventario","LA SUBI");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO: ver que hacer aca
                Log.e("Inventario", e.getMessage());
            }
        });

    }

}
