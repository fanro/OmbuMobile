package com.max.ombumobile;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class Ticket implements Serializable{

    private String numero;
    private String tstamp;
    private String cliente;
    private String dependencia;
    private String lugar;
    private String problema;
    private String comentario;
    private String bien;
    private String inventario;
    private String prioridad;
    private String estado;
    private String supervisor;
    private static final String[] prioridades = {"Muy Alta","Alta", "Normal","Baja", "Muy Baja"};
    private static final String[] estados_tickets = {"EN PROCESO", "EN ESPERA", "CERRADO POR TECNICO", "INTERVENCION OPERADOR", "INTERVENCION SUPERVISOR"};

    // FORMATO NUMERO DE INVENTARIO SETXXXXXXXX
    public static final char [] charFormatoTicket =  {'S','E', 'T', '0', '0', '0', '0','0','0','0','0'};
    public static final int MAXValorLongNumeroSET = 8;
    public static final int MAXValorLongCabeceraSET = 3;
    public static final int MAXValorLongSET = MAXValorLongNumeroSET + MAXValorLongCabeceraSET;

    public Ticket(String numero, String tstamp, String cliente, String dependencia, String lugar,
                  String problema, String comentario, String bien, String inventario,
                  String estado, String prioridad, String supervisor) {
        this.numero = numero;
        this.tstamp = tstamp;
        this.cliente = cliente;
        this.dependencia = dependencia;
        this.lugar = lugar;
        this.problema = problema;
        this.comentario = comentario;
        this.bien = bien;
        this.inventario = inventario;
        this.prioridad = prioridad;
        this.estado = estado;
        this.supervisor = supervisor;
    }

    @Override
    public String toString() {
        String res = getNumero("completo") + " - " + estado + "\n";
        res += tstamp + "\n";
        res += getPrioridad() + "\n";
        res += cliente + "\n";
        res += dependencia + "\n";
        res += lugar + "\n";

        return res;
    }

    public String getNumero(String modo) {
        if (modo.equals("completo")){
        return armarCadenaTicket(numero);
        }
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTstamp() {
        return tstamp;
    }

    public void setTstamp(String tstamp) {
        this.tstamp = tstamp;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getBien() {
        return bien;
    }

    public void setBien(String bien) {
        this.bien = bien;
    }

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridades[this.indexPrioridades()];
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }


    private String armarCadenaTicket(String numero){
        char [] res = charFormatoTicket.clone(); // ojo leak
        char[] num = numero.toCharArray();
        int largo = numero.length();
        int comienzo = MAXValorLongSET - largo;
        for(int i = 0; i< largo; i++ ){
            res[comienzo] = num[i];
            comienzo++;
        }
        return String.valueOf(res);
    }

    private int indexPrioridades(){
        switch (prioridad){
            case "1": return 0;
            case "2": return 1;
            case "3": return 2;
            case "4": return 3;
            case "5": return 4;
        }
        return 1;
    }

    public static Ticket[] parsearTickets(JSONArray data){

        Ticket[] tickets;
        tickets = new Ticket[data.length()];
        try {
            for (int i = 0; i < data.length(); i++) {
                Ticket tic = new Ticket(data.getJSONObject(i).getString("ticket"),
                        data.getJSONObject(i).getString("tstamp"),
                        data.getJSONObject(i).getString("cliente"),
                        data.getJSONObject(i).getString("dependencia"),
                        data.getJSONObject(i).getString("lugar"),
                        data.getJSONObject(i).getString("problema"),
                        data.getJSONObject(i).getString("comentario"),
                        data.getJSONObject(i).getString("bien"),
                        data.getJSONObject(i).getString("inventario"),
                        data.getJSONObject(i).getString("estado"),
                        data.getJSONObject(i).getString("prioridad"),
                        data.getJSONObject(i).getString("supervisor"));

                tickets[i] = tic;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tickets;
    }
}
