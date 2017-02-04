package com.max.ombumobile;

import java.io.Serializable;

public class NuevoTicket implements Serializable {

    private String incidente;
    private String comentario;
    private String bienDescripcion;
    private String inventariado;
    private String nroInventario;
    private String prioridad;

    public String getIncidente() {
        return incidente;
    }

    public String getComentario() {
        return comentario;
    }

    public String getBienDescripcion() {
        return bienDescripcion;
    }

    public String getInventariado() {
        return inventariado;
    }

    public String getNroInventario() {
        return nroInventario;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setIncidente(String incidente) {
        this.incidente = incidente;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setBienDescripcion(String bienDescripcion) {
        this.bienDescripcion = bienDescripcion;
    }

    public void setInventariado(String inventariado) {
        this.inventariado = inventariado;
    }

    public void setNroInventario(String nroInventario) {
        this.nroInventario = nroInventario;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

}
