package com.max.ombumobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Inventario extends AppCompatActivity implements AsyncResponse {

    /** Called when the activity is first created. */
    private static final String BS_PACKAGE = "com.google.zxing.client.android";
    public static final int REQUEST_CODE = 0x0000c0de;
    private Button button_LeerCodigo;
    private Button button_EscribirCodigo;
    private Button button_AdjuntarFoto;
    private TextView textView_Inventario;
    private TextView textView_Bien;
    private ImageView imagen_Camara;
    private Activity activity;
    private String numero_inventario;

    // FORMATO NUMERO DE INVENTARIO INVXXXXXXXX
    public static final char [] charFormatoInventario =  {'I','N', 'V', '0', '0', '0', '0','0','0','0','0'};
    public static final int MAXValorLongNumeroINV = 8;
    public static final int MAXValorLongCabeceraINV = 3;
    public static final int MAXValorLongINV = MAXValorLongNumeroINV + MAXValorLongCabeceraINV;

    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        activity = this;
        button_EscribirCodigo = (Button)findViewById(R.id.button_EscribirCodigo);
        button_LeerCodigo = (Button)findViewById(R.id.button_LeerCodigo);
        button_AdjuntarFoto = (Button)findViewById(R.id.button_AdjuntarFoto);
        imagen_Camara = (ImageView) findViewById(R.id.imagen_Camara);
        textView_Inventario = (TextView) findViewById(R.id.textView_Inventario);
        textView_Bien = (TextView) findViewById(R.id.textView_Bien);

        this.ocultarCampos();

        button_AdjuntarFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        button_LeerCodigo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
                intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm.viendo sólo el código de barras");
                String targetAppPackage = findTargetAppPackage(intentScan);
                if (targetAppPackage == null) {
                    showDownloadDialog();
                } else startActivityForResult(intentScan, REQUEST_CODE);
            }
        });

        button_EscribirCodigo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                //ocultarCamposResultado(); // puede ver datos cargados
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                //alert.setTitle(Constantes.IngresarInventario);
                alert.setMessage("Ingresar Número de Inventario");

                // Set an EditText view to get user input
                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(MAXValorLongNumeroINV);
                input.setFilters(filters);
                alert.setView(input);

                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        //mostrarToastMensaje(armarCadenaInventario(value));
                        verInventario(armarCadenaInventario(value));
                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }

    private String findTargetAppPackage(Intent intent) {
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableApps != null) {
            for (ResolveInfo availableApp : availableApps) {
                String packageName = availableApp.activityInfo.packageName;
                if (BS_PACKAGE.contains(packageName)) {
                    return packageName;
                }
            }
        }
        return null;
    }

    private AlertDialog showDownloadDialog() {
        final String DEFAULT_TITLE = "Instalar Barcode Scanner?";
        final String DEFAULT_MESSAGE =
                "Esta aplicación necesita Barcode Scanner. Quiere instalarla?";
        final String DEFAULT_YES = "Si";
        final String DEFAULT_NO = "No";

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://details?id=" + BS_PACKAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    // Hmm, market is not installed
                    Toast.makeText(Inventario.this, "Android market no esta instalado,no puedo instalar Barcode Scanner", Toast.LENGTH_LONG).show();
                }
            }
        });
        downloadDialog.setNegativeButton(DEFAULT_NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        return downloadDialog.show();
    }

    private String armarCadenaInventario(String numero){
        char [] res = charFormatoInventario.clone(); // ojo leak
        char[] num = numero.toCharArray();
        int largo = numero.length();
        int comienzo = MAXValorLongINV - largo;
        for(int i = 0; i< largo; i++ ){
            res[comienzo] = num[i];
            comienzo++;
        }
        return String.valueOf(res);
    }

    private void setearValorInventario(String inventario) {
        textView_Inventario.setText(armarCadenaInventario(inventario));
    }

    private void mostrarToastMensaje(String inv) {
        // TODO Auto-generated method stub
        Toast.makeText(this, inv, Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imagen_Camara.setImageBitmap(photo);
        }

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                /** INFORMACION DE MAS QUE BRINDA EL ESCANEO, COMO FORMATO, ORIETACION, ETC.*/
		          /*String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
		          byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
		          int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
		          Integer orientation = intentOrientation == Integer.MIN_VALUE ? null : intentOrientation;
		          String errorCorrectionLevel = intent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");
		          Toast.makeText(this, contents, Toast.LENGTH_LONG).show();*/

                //RESULTADO
                // ver si lo que esta en contents es valido
                if(verificarResultadoScan(contents)){
                    this.mostrarCampos();
                    //saco el INV al numero de inventario
                    this.verInventario(contents);
                }
                else{
                    Toast.makeText(this, "Formato no valido", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void verInventario(String inv){
        this.limpiarInventario(inv);
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_INVENTARIO, this.numero_inventario};
        rh.setActivity(this);
        rh.execute(passing);
    }

    private boolean verificarResultadoScan(String res) {
        if(res.contains("INV"))
            return true;
        return false;
    }

    private void llenarCamposConBien(Bien bien) {
        textView_Inventario.setText(armarCadenaInventario(bien.getCodigo()));
        textView_Bien.setText(
                  bien.getDescripcion() + " \n"
                + bien.getAtributo() + " \n"
                + bien.getSerie() + " \n"
                + bien.getDependencia());

    }

    private void limpiarInventario(String inv) {
        int indice = 0;
        char inventario[] = inv.toCharArray();
        for(int i=0;i<inv.length();i++){
            if(inventario[i] != '0' && inventario[i] != 'I' && inventario[i] != 'N' && inventario[i] != 'V'){
                indice = i;
                break;
            }
        }
        numero_inventario = inv.substring(indice);
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
                Bien bien = new Bien(data.getString("codigo"), data.getString("dependencia"),
                        data.getString("descripcion"),data.getString("atributo"), data.getString("serie"));
                llenarCamposConBien(bien);
            } else{
                mostrarToastMensaje("Inventario Incorrecto");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void ocultarCampos() {
        textView_Inventario.setVisibility(TextView.INVISIBLE);
    }

    private void mostrarCampos() {
        textView_Inventario.setVisibility(TextView.VISIBLE);
    }


    //  Tickets Tecnico
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
