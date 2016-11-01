package com.max.ombumobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class Inventario extends AppCompatActivity {

    /** Called when the activity is first created. */
    private static final String BS_PACKAGE = "com.google.zxing.client.android";
    public static final int REQUEST_CODE = 0x0000c0de;
    private Button button_LeerCodigo;
    private Button button_EscribirCodigo;
    private Button button_AdjuntarFoto;
    private ImageView imagen_Camara;
    private Activity activity;

    // FORMATO NUMERO DE INVENTARIO INVXXXXXXXX
    public static final char [] charFormatoInventario =  {'I','N', 'V', '0', '0', '0', '0','0','0','0','0'};
    public static final int MAXValorLongNumeroINV = 8;
    public static final int MAXValorLongCabeceraINV = 3;
    public static final int MAXValorLongINV = MAXValorLongNumeroINV + MAXValorLongCabeceraINV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        activity = this;
        button_EscribirCodigo = (Button)findViewById(R.id.button_EscribirCodigo);
        button_LeerCodigo = (Button)findViewById(R.id.button_LeerCodigo);
        button_AdjuntarFoto = (Button)findViewById(R.id.button_AdjuntarFoto);
        imagen_Camara = (ImageView) findViewById(R.id.imagen_Camara);


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
                        mostrarToastMensaje(armarCadenaInventario(value));
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

    private void mostrarToastMensaje(String inv) {
        // TODO Auto-generated method stub
        Toast.makeText(this, inv, Toast.LENGTH_LONG).show();
    }

}
