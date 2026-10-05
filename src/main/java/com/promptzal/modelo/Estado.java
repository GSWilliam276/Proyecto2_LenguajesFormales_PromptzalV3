/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.modelo;

/**
 *
 * @author eduar
 */
public class Estado {
    private final String id;        //"q6"
    private final String etiqueta;  //"transito" (vacio si no lleva)
    private final TipoEstado tipo;
    private final String mensaje;   //solo para estados ERROR

    public Estado(String id, String etiqueta, TipoEstado tipo, String mensaje) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public Estado(String id, String etiqueta, TipoEstado tipo) {
        this(id, etiqueta, tipo, "");
    }

    public String getId() { return id; }
    public String getEtiqueta() { return etiqueta; }
    public TipoEstado getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
}
