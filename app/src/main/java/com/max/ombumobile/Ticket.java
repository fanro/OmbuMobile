package com.max.ombumobile;

import java.io.Serializable;

public class Ticket implements Serializable{

    private String numero;
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

    public Ticket(String numero, String cliente, String dependencia, String lugar,
                  String problema, String comentario, String bien, String inventario,
                  String estado, String prioridad, String supervisor) {
        this.numero = numero;
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
        res +=  getPrioridad() + "\n";
        res +=  cliente + "\n";
        res +=  dependencia + "\n";
        res +=  lugar + "\n";

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
}
