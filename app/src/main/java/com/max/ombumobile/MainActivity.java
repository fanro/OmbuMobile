package com.max.ombumobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    Button button_login;
    private EditText editText_Usr;
    private EditText editText_Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_login = (Button) findViewById(R.id.button_login);
        editText_Usr = (EditText) findViewById(R.id.editText_Usr);
        editText_Pass = (EditText) findViewById(R.id.editText_Pass);
    }

    public void loginCheck(View view){

        // sacamos el teclado
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText_Pass.getWindowToken(), 0);

        RestHandler rh = new RestHandler();
        String[] passing = {"POST", rh.REST_ACTION_LOGIN, editText_Usr.getText().toString(), editText_Pass.getText().toString()};
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
            Toast toast = Toast.makeText(getBaseContext(), obj.getString("status") + ": " + obj.getString("message"), Toast.LENGTH_SHORT);
            toast.show();
            if(obj.getString("status").equals( "OK")){
                Usuario usr = Usuario.getInstance();
                JSONObject data = new JSONObject(obj.getString("data"));
                usr.load(data);
                menuPrincipal(usr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void menuPrincipal(Usuario usr){
        if(usr.esTecnico()){
            // El usuario es Tecnico, pregutar como se quiere logear
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Perfil")
                    //.setCancelable(false)
                    .setMessage("Como desea usar la aplicación?")
                    .setPositiveButton("Técnico",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainActivity.this, MenuTecnico.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("Usuario",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this, Menu.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            // Usuario general
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
        }
        //editText_Pass.setText("");

    }
}
