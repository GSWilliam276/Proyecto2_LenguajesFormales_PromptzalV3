/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.logica;

import com.promptzal.ast.Agente;
import com.promptzal.ast.Comando;
import com.promptzal.ast.Conector;
import com.promptzal.ast.Expresion;
import com.promptzal.ast.Programa;
import com.promptzal.ast.Referencia;
import com.promptzal.ast.Termino;
import com.promptzal.ast.Variable;
import com.promptzal.modelo.ErrorSemantico;
import com.promptzal.modelo.Simbolo;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author eduar
 */
public class AnalizadorSemantico {
    private final Programa programa;
    private final TablaSimbolos tabla = new TablaSimbolos();
    private final List<ErrorSemantico> errores = new ArrayList<>();

    public AnalizadorSemantico(Programa programa) {
        this.programa = programa;
    }

    //Recorre el arbol en el orden del programa: primero los agentes (que llenan
    //la tabla), y despues EJECUTAR y EXPORTAR, que consultan lo ya declarado.
    public void analizar() {
        for (Agente a : programa.getAgentes()) {
            analizarAgente(a);
        }
        validarEjecuciones();
        validarExportaciones();
    }

    private void analizarAgente(Agente a) {
        boolean esNuevo = tabla.declarar(a.getNombre(), Simbolo.Tipo.AGENTE,
                TablaSimbolos.GLOBAL, a.getFila());
        if (!esNuevo) {
            error("Agente duplicado: ya existe un agente llamado '" + a.getNombre() + "'", a.getFila());
            return; //no se analiza su cuerpo para no mezclar sus simbolos con los del original
        }

        for (Variable v : a.getVariables()) {
            //Primero se valida el valor y despues se declara: "variable x = x" es un error
            validarExpresion(v.getValor(), a.getNombre(), false);
            tabla.declarar(v.getNombre(), Simbolo.Tipo.VARIABLE, a.getNombre(), v.getFila());
        }

        for (Comando c : a.getComandos()) {
            analizarComando(c, a.getNombre());
        }
    }

    private void analizarComando(Comando c, String ambito) {
        //En GENERAR, si el primer argumento es un ID,
        //indica el tipo de contenido a generar ("GENERAR codigo"), no es una variable
        List<Termino> terminos = c.getPrincipal().getTerminos();
        boolean saltarPrimero = c.getTipo().equals("GENERAR")
                && !terminos.isEmpty()
                && terminos.get(0).getTipo() == Termino.Tipo.ID;

        validarExpresion(c.getPrincipal(), ambito, saltarPrimero);

        for (Conector con : c.getConectores()) {
            //COMO indica un formato y, en TRADUCIR y CLASIFICAR, EN lleva un idioma o categorias:
            //son parametros que describen, no variables, asi que no se validan como declaradas
            boolean parametro = con.getPalabra().equals("COMO")
                    || (con.getPalabra().equals("EN")
                        && (c.getTipo().equals("TRADUCIR") || c.getTipo().equals("CLASIFICAR")));
            if (!parametro) {
                validarExpresion(con.getArgumento(), ambito, false);
            }
        }

        //El destino de "->" declara la variable, despues de validar lo que el comando usa
        tabla.declarar(c.getDestino(), Simbolo.Tipo.RESULTADO, ambito, c.getFilaDestino());
    }

    //Todo ID usado en una expresion tiene que estar declarado antes, en el mismo agente.
    //La "unidad" de "100 palabras" no es un Termino, sino un campo del numero,
    //por eso nunca se valida como variable (segunda excepcion del enunciado).
    private void validarExpresion(Expresion e, String ambito, boolean saltarPrimero) {
        List<Termino> terminos = e.getTerminos();
        for (int i = 0; i < terminos.size(); i++) {
            Termino t = terminos.get(i);
            if (t.getTipo() != Termino.Tipo.ID) {
                continue;
            }
            if (i == 0 && saltarPrimero) {
                continue;
            }
            if (!tabla.existe(t.getValor(), ambito)) {
                error("Identificador no declarado: '" + t.getValor()
                        + "' en el agente '" + ambito + "'", t.getFila());
            }
        }
    }

    //EJECUTAR solo puede referirse a un agente que exista
    private void validarEjecuciones() {
        for (Referencia r : programa.getEjecuciones()) {
            if (tabla.buscar(r.getNombre(), TablaSimbolos.GLOBAL) == null) {
                error("EJECUTAR hace referencia a un agente que no existe: '"
                        + r.getNombre() + "'", r.getFila());
            }
        }
    }

    //EXPORTAR solo puede referirse a variables que existan en algun agente ejecutado
    private void validarExportaciones() {
        for (Referencia r : programa.getExportaciones()) {
            boolean encontrada = false;
            for (Referencia ejecucion : programa.getEjecuciones()) {
                if (tabla.existe(r.getNombre(), ejecucion.getNombre())) {
                    encontrada = true;
                    break;
                }
            }
            if (!encontrada) {
                error("EXPORTAR hace referencia a '" + r.getNombre()
                        + "', que no existe en ningun agente ejecutado", r.getFila());
            }
        }
    }

    private void error(String descripcion, int fila) {
        errores.add(new ErrorSemantico(descripcion, fila));
    }

    public TablaSimbolos getTabla() {
        return tabla;
    }

    public List<ErrorSemantico> getErrores() {
        return errores;
    }
}
