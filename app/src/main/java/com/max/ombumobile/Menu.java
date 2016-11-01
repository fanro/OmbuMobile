package com.max.ombumobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    private Button button_nuevoTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        button_nuevoTicket = (Button)findViewById(R.id.button_nuevoTicket);
    }

    //	ignoro boton back android, se debe desloguear el usuario
    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                this.cerrarApp();
                return true;
            case R.id.perfil:
                this.showPerfil();
                return true;
            case R.id.info:
                this.showInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // user logeado
    private void showPerfil() {
        Usuario usr = Usuario.getInstance(getBaseContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msj = usr.getPerfil();
        builder.setMessage(msj)
                .setTitle("Perfil")
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // info app
    private void showInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("OMBU Mobile - Versión 1.0")
                .setTitle("Información")
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // cierro app, limpio user
    private void cerrarApp() {
        Usuario usr = Usuario.getInstance(getBaseContext());
        usr.logoutUsr();
        super.onBackPressed();
    }

    //  Nuevo ticket
    public void nuevoTicket(View view) {
        Intent intent = new Intent(this, Inventario.class);
        startActivity(intent);
        }

}
