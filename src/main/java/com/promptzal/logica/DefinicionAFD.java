/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.logica;

import com.promptzal.modelo.Estado;
import com.promptzal.modelo.TipoEstado;
import com.promptzal.modelo.Transicion;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author eduar
 */
/**
 * Definicion del AFD de PromptZal como datos. Es la unica fuente de verdad:
 * de aqui salen el codigo DOT (GeneradorAFD) y, en la fase 2, las
 * decisiones del analizador lexico.
 */
public class DefinicionAFD {
    private final List<Estado> estados = new ArrayList<>();
    private final List<Transicion> transiciones = new ArrayList<>();

    public DefinicionAFD() {
        estado("q0", "", TipoEstado.TRANSITO);

        //Rama: simbolo suelto
        estado("q1", "SIMBOLO", TipoEstado.ACEPTACION);
        transicion("q0", "q1", "=");
        transicion("q0", "q1", "+");
        transicion("q0", "q1", "{");
        transicion("q0", "q1", "}");
        transicion("q0", "q1", "(");
        transicion("q0", "q1", ")");
        transicion("q0", "q1", ",");

        //Rama: conector flecha
        estado("q2", "decidiendo", TipoEstado.TRANSITO);
        estado("q3", "CONECTOR", TipoEstado.ACEPTACION);
        estado("q4", "ERROR", TipoEstado.ERROR);
        transicion("q0", "q2", "-");
        transicion("q2", "q3", ">");
        transicion("q2", "q4", "otro");

        //Rama: numero (entero / decimal)
        estado("q5", "ENTERO", TipoEstado.ACEPTACION);
        estado("q6", "transito", TipoEstado.TRANSITO);
        estado("q7", "DECIMAL", TipoEstado.ACEPTACION);
        estado("q21", "ERROR", TipoEstado.ERROR);
        transicion("q0", "q5", "digito");
        transicion("q5", "q5", "digito");
        transicion("q5", "q6", ".");
        transicion("q6", "q7", "digito");
        transicion("q6", "q21", "otro (no digito)");
        transicion("q7", "q7", "digito");

        //Rama: cadena
        estado("q8", "acumulando", TipoEstado.TRANSITO);
        estado("q9", "CADENA", TipoEstado.ACEPTACION);
        estado("q10", "ERROR", TipoEstado.ERROR);
        transicion("q0", "q8", "\"");
        transicion("q8", "q8", "otro");
        transicion("q8", "q9", "\"");
        transicion("q8", "q10", "salto de linea");

        //Rama: directiva
        estado("q11", "acumulando", TipoEstado.TRANSITO);
        estado("q12", "DIRECTIVA", TipoEstado.ACEPTACION);
        estado("q13", "ERROR", TipoEstado.ERROR);
        transicion("q0", "q11", "@");
        transicion("q11", "q11", "letra/digito");
        transicion("q11", "q12", "valida");
        transicion("q11", "q13", "no valida");

        //Rama: comentario (linea y bloque comparten el punto de decision)
        estado("q14", "decidiendo", TipoEstado.TRANSITO);
        estado("q15", "modo linea", TipoEstado.TRANSITO);
        estado("q16", "sin token", TipoEstado.ACEPTACION_SIN_TOKEN);
        estado("q17", "modo bloque", TipoEstado.TRANSITO);
        estado("q18", "sin token", TipoEstado.ACEPTACION_SIN_TOKEN);
        estado("q19", "ERROR", TipoEstado.ERROR);
        estado("q22", "posible cierre", TipoEstado.TRANSITO);
        estado("q23", "ERROR", TipoEstado.ERROR);
        transicion("q0", "q14", "/");
        transicion("q14", "q15", "/");
        transicion("q14", "q17", "*");
        transicion("q14", "q23", "otro");
        transicion("q15", "q15", "otro");
        transicion("q15", "q16", "salto de linea");
        transicion("q15", "q16", "fin archivo");
        transicion("q17", "q17", "otro (no *)");
        transicion("q17", "q22", "*");
        transicion("q17", "q19", "fin archivo");
        transicion("q22", "q18", "/");
        transicion("q22", "q22", "*");
        transicion("q22", "q17", "otro");
        transicion("q22", "q19", "fin archivo");

        //Rama: identificador / palabra reservada / comando / conector
        estado("q20", "ID/RESERVADA", TipoEstado.ACEPTACION);
        transicion("q0", "q20", "letra");
        transicion("q20", "q20", "letra/digito");
    }

    private void estado(String id, String etiqueta, TipoEstado tipo) {
        estados.add(new Estado(id, etiqueta, tipo));
    }

    private void transicion(String origen, String destino, String simbolo) {
        transiciones.add(new Transicion(origen, destino, simbolo));
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }
}
