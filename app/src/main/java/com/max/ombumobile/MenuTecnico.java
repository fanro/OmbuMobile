package com.max.ombumobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuTecnico extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tecnico);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //	ignoro boton back android, se debe desloguear el usuario
    @Override
    public void onBackPressed() {
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
        Usuario usr = Usuario.getInstance();
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
        Usuario usr = Usuario.getInstance();
        SqliteHandler db = new SqliteHandler(this);
        db.deleteSession(usr.getSession_id());
        usr.logoutUsr();
        super.onBackPressed();
    }

    //  Tickets Tecnico
    public void ticketsTecnico(View view) {
        Intent intent = new Intent(this, TicketsTecnico.class);
        startActivity(intent);
    }

    //  Agenda
    public void agenda(View view) {
        Intent intent = new Intent(this, BusquedaContactos.class);
        startActivity(intent);
    }
}
