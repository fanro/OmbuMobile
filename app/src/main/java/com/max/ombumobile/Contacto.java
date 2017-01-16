package com.max.ombumobile;

public class Contacto {

    private String apellido;
    private String nombre;
    private String email;
    private String telefono_lab;
    private String celular_lab;
    private String dependencia;

    public Contacto(String apellido, String nombre, String email, String telefono_lab, String celular_lab, String dependencia) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.email = email;
        this.telefono_lab = telefono_lab;
        this.celular_lab = celular_lab;
        this.dependencia = dependencia;
    }

    @Override
    public String toString() {

        String res = apellido + " "+ nombre + "\n" +
                email;

        if(telefono_lab!=""){
            res+= "\n" + telefono_lab;
        }

        if(celular_lab!=""){
            res+= "\n" + celular_lab;
        }

        return res+= "\n" + dependencia;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono_lab() {
        return telefono_lab;
    }

    public void setTelefono_lab(String telefono_lab) {

        /*Character[] tel = new Character[telefono_lab.length()];
        for (int i=0; i<telefono_lab.length(); i++){
            if(telefono_lab.charAt(i)=='0' && telefono_lab.charAt(i)=='1' && telefono_lab.charAt(i)=='2'&& telefono_lab.charAt(i)=='3'
                    && telefono_lab.charAt(i)=='4'&& telefono_lab.charAt(i)=='5' && telefono_lab.charAt(i)=='6' && telefono_lab.charAt(i)=='7'
                    && telefono_lab.charAt(i)=='8'&& telefono_lab.charAt(i)=='9'){
                tel[i]=telefono_lab.charAt(i);
            }
        }*/

        this.telefono_lab = telefono_lab.replace("-–— \\n", "");
    }

    public String getCelular_lab() {
        return celular_lab;
    }

    public void setCelular_lab(String celular_lab) {
        this.celular_lab = celular_lab.replace("-–— \\n", "");
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }


}



