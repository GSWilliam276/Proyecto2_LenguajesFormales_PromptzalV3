/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.modelo;

/**
 *
 * @author eduar
 */
public class Transicion {
    private final String origen, destino, simbolo;

    public Transicion(String origen, String destino, String simbolo) {
        this.origen = origen; this.destino = destino; this.simbolo = simbolo;
    }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getSimbolo() { return simbolo; }
}
