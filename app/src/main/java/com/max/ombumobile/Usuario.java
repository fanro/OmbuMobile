package com.max.ombumobile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by max on 26/10/2016.
 */

public class Usuario {

    public static final String RIGHT_TECNICO = "ombu.soporte.tecnico";
    private String user_id;
    private String nombre;
    private String session_id;
    private String[] grupos;
    private String[] derechos;
    private static Usuario usr = null;

    //	Singleton, un unico usuario logeado
    public static Usuario getInstance(){
        if (usr == null) {
            usr = new Usuario();
        }
        return usr;
    }

    public void load(JSONObject json) throws JSONException {
        this.setUser_id(json.getString("user_id"));
        this.setNombre(json.getString("nombre"));
        this.setSession_id(json.getString("session_id"));
        this.setDerechos(json.getString("rights").split(","));
    }

    public boolean esTecnico(){
        return  Arrays.asList(this.derechos).contains(RIGHT_TECNICO);
    }

    // Empty constructor
    public Usuario(){
    }

    // limpio valores del usuario
    public void logoutUsr(){
        this.user_id = null;
        this.nombre = null;
        this.session_id = null;
        this.derechos = null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String[] getDerechos() {
        return derechos;
    }

    public void setDerechos(String[] derechos) {
        this.derechos = derechos;
    }


    public String getPerfil() {
        return "Usuario: " + this.nombre + "\n" +
                "Legajo: " + this.user_id  + "\n" +
                "Session: ********************" + this.session_id.substring(19,25) ;
    }
}
