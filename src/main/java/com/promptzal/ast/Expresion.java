/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.ast;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author eduar
 */
public class Expresion {
    private final List<Termino> terminos = new ArrayList<>();

    public void agregar(Termino t) {
        terminos.add(t);
    }

    public List<Termino> getTerminos() {
        return terminos;
    }

    //Linea de la expresion = la de su primer termino (para reportar errores semanticos)
    public int getFila() {
        return terminos.isEmpty() ? 0 : terminos.get(0).getFila();
    }

    //Une los terminos con " + ", igual que se escribio en el programa
    public String aTexto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terminos.size(); i++) {
            if (i > 0) {
                sb.append(" + ");
            }
            sb.append(terminos.get(i).aTexto());
        }
        return sb.toString();
    }
}
