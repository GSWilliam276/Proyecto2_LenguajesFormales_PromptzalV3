/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.logica;

import com.promptzal.modelo.Simbolo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduar
 */
public class TablaSimbolos {
    public static final String GLOBAL = "global";

    private final List<Simbolo> simbolos = new ArrayList<>();

    //Registra un simbolo. Devuelve false (y no lo agrega) si ya existe uno
    //con ese nombre en ese mismo ambito
    public boolean declarar(String nombre, Simbolo.Tipo tipo, String ambito, int linea) {
        if (existe(nombre, ambito)) {
            return false;
        }
        simbolos.add(new Simbolo(nombre, tipo, ambito, linea));
        return true;
    }

    //Para saber si un simbolo con tal nombre existe en este ambito
    public boolean existe(String nombre, String ambito) {
        return buscar(nombre, ambito) != null;
    }

    public Simbolo buscar(String nombre, String ambito) {
        for (Simbolo s : simbolos) {
            if (s.getNombre().equals(nombre) && s.getAmbito().equals(ambito)) {
                return s;
            }
        }
        return null;
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }
}
