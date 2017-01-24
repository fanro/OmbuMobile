package com.max.ombumobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.max.ombumobile.Inventario.MAXValorLongINV;
import static com.max.ombumobile.Inventario.MAXValorLongNumeroINV;
import static com.max.ombumobile.Inventario.charFormatoInventario;

public class GeneracionTicketPaso1 extends AppCompatActivity implements AsyncResponse {

    private static final String BS_PACKAGE = "com.google.zxing.client.android";
    public static final int REQUEST_CODE_BARCODE = 0x0000c0de;
    private Button button_LeerCodigo;
    private Button button_EscribirCodigo;
    private Button button_Siguiente;
    private RadioGroup radioGroup_Group;
    private RadioButton radioButton_Inventariado;
    private RadioButton radioButton_NoInventariado;
    private Activity activity;
    private String numero_inventario;
    private TextView textView_Inventario;
    private TextView textView_Bien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generacion_ticket_paso1);

        inicializarVista();
    }

    private void inicializarVista() {
        activity = this;
        button_EscribirCodigo = (Button)findViewById(R.id.button_EscribirCodigo);
        button_LeerCodigo = (Button)findViewById(R.id.button_LeerCodigo);
        button_Siguiente = (Button)findViewById(R.id.button_Siguiente);
        radioButton_Inventariado = (RadioButton)findViewById(R.id.radioButton_Inventariado);
        radioButton_NoInventariado = (RadioButton)findViewById(R.id.radioButton_NoInventariado);
        radioGroup_Group = (RadioGroup)findViewById(R.id.radioGroup_Group);
        textView_Inventario = (TextView)findViewById(R.id.textView_Inventario);
        textView_Bien = (TextView)findViewById(R.id.textView_Bien);
        button_EscribirCodigo.setEnabled(false);
        button_LeerCodigo.setEnabled(false);
        button_Siguiente.setEnabled(false);
        this.ocultarCampos();


        button_LeerCodigo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
                intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm.viendo sólo el código de barras");
                String targetAppPackage = findTargetAppPackage(intentScan);
                if (targetAppPackage == null) {
                    showDownloadDialog();
                } else startActivityForResult(intentScan, REQUEST_CODE_BARCODE);
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

        radioGroup_Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(radioButton_Inventariado.isChecked()){
                    habilitarOpcionesInventario();
                }
                if(radioButton_NoInventariado.isChecked()){
                    habilitarOpcionesNoInventario();
                }
            }
        });

    }

    private void habilitarOpcionesNoInventario() {
        ocultarCampos();
        button_EscribirCodigo.setEnabled(false);
        button_LeerCodigo.setEnabled(false);
        button_Siguiente.setEnabled(true);
    }

    private void habilitarOpcionesInventario() {
        ocultarCampos();
        button_EscribirCodigo.setEnabled(true);
        button_LeerCodigo.setEnabled(true);
        button_Siguiente.setEnabled(false);
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
                    Toast.makeText(GeneracionTicketPaso1.this, "Android market no esta instalado,no puedo instalar Barcode Scanner", Toast.LENGTH_LONG).show();
                }
            }
        });
        downloadDialog.setNegativeButton(DEFAULT_NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        return downloadDialog.show();
    }

    private void verInventario(String inv){
        this.limpiarInventario(inv);
        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_INVENTARIO, this.numero_inventario};
        rh.setActivity(this);
        rh.execute(passing);
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
                button_Siguiente.setEnabled(true);
            } else{
                mostrarToastMensaje("Inventario Incorrecto");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void mostrarToastMensaje(String inv) {
        // TODO Auto-generated method stub
        Toast.makeText(this, inv, Toast.LENGTH_LONG).show();
    }

    private void llenarCamposConBien(Bien bien) {
        mostrarCampos();
        textView_Inventario.setText(armarCadenaInventario(bien.getCodigo()));
        textView_Bien.setText(
                bien.getDescripcion() + " \n"
                        + bien.getAtributo() + " \n"
                        + bien.getSerie() + " \n"
                        + bien.getDependencia());
    }

    private void ocultarCampos() {
        textView_Inventario.setVisibility(TextView.INVISIBLE);
        textView_Bien.setText("");
    }

    private void mostrarCampos() {
        textView_Inventario.setVisibility(TextView.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_BARCODE) {
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

    private boolean verificarResultadoScan(String res) {
        if(res.contains("INV"))
            return true;
        return false;
    }

    public void siguientePaso(View view){
        Intent intent = new Intent(GeneracionTicketPaso1.this, GeneracionTicketPaso2.class);
        startActivity(intent);
    }
}
