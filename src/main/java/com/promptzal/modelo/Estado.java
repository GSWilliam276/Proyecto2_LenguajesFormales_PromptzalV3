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
    private final String id;        // "q6"
    private final String etiqueta;  // "transito" (vacio si no lleva)
    private final TipoEstado tipo;

    public Estado(String id, String etiqueta, TipoEstado tipo) {
        this.id = id; this.etiqueta = etiqueta; this.tipo = tipo;
    }
    public String getId() { return id; }
    public String getEtiqueta() { return etiqueta; }
    public TipoEstado getTipo() { return tipo; }
}
