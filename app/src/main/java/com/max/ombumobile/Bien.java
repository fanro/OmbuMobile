package com.max.ombumobile;

/**
 * Created by max on 25/11/2016.
 */

public class Bien {

    private String codigo;
    private String dependencia;
    private String descripcion;
    private String atributo;
    private String serie;

    public Bien(String codigo, String dependencia, String descripcion, String atributo, String serie) {
        this.codigo = codigo;
        this.dependencia = dependencia;
        this.descripcion = descripcion;
        this.atributo = atributo;
        this.serie = serie;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }



}
