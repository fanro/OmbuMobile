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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class VerTicket extends AppCompatActivity {

    private TextView textView_Ticket;
    private TextView textView_Prioridad;
    private TextView textView_Cliente;
    private TextView textView_Dependencia;
    private TextView textView_Direccion;
    private TextView textView_Incidente;
    private TextView textView_Comentario;
    private TextView textView_Fecha;
    private Ticket ticket;
    private ImageView imagen_Ticket;
    private Bitmap bitmap;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ticket);

        this.inicializarVista();
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

    private void inicializarVista(){
        Intent intent = getIntent();
        ticket = (Ticket) intent.getSerializableExtra("Ticket");
        textView_Ticket = (TextView) findViewById(R.id.textView_Ticket);
        textView_Prioridad = (TextView) findViewById(R.id.textView_Prioridad);
        textView_Cliente = (TextView) findViewById(R.id.textView_Cliente);
        textView_Dependencia = (TextView) findViewById(R.id.textView_Dependencia);
        textView_Direccion = (TextView) findViewById(R.id.textView_Direccion);
        textView_Incidente = (TextView) findViewById(R.id.textView_Incidente);
        textView_Comentario = (TextView) findViewById(R.id.textView_Comentario);
        textView_Fecha = (TextView) findViewById(R.id.textView_Fecha);
        imagen_Ticket = (ImageView) findViewById(R.id.imagen_Ticket);
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
                uri = getImageUri(VerTicket.this, bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ERR_DOWNLOAD_IMG", "Error descargando la imagen del ticket " + ticketNro);
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
