/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.logica;

import com.promptzal.excepciones.ExcepcionSintactica;
import com.promptzal.modelo.ErrorSintactico;
import com.promptzal.modelo.TipoToken;
import com.promptzal.modelo.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.promptzal.ast.Expresion;
import com.promptzal.ast.Termino;
import com.promptzal.ast.Comando;
import com.promptzal.ast.Conector;
/**
 *
 * @author eduar
 */
public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private final List<ErrorSintactico> errores = new ArrayList<>();

    //Tokens donde el modo panico deja de descartar y retoma el analisis
    private static final Set<String> SINCRONIZACION = Set.of(
            "AGENTE", "LLAVE_C", "COMANDO_IA", "VARIABLE", "EJECUTAR", "EXPORTAR", "EOF");

    public Parser(List<Token> tokens) {
        this.tokens = tokens; //la lista termina siempre con el token EOF
    }

    //Token que se esta mirando, sin consumirlo
    private Token actual() {
        return tokens.get(pos);
    }

    //Sirve para decidir que produccion usar
    private boolean es(TipoToken tipo) {
        return actual().getTipo().equals(tipo.name());
    }

    //Consume el token actual y pasa al siguiente (nunca se pasa del EOF)
    private Token avanzar() {
        Token t = actual();
        if (!es(TipoToken.EOF)) {
            pos++;
        }
        return t;
    }

    //Exige un tipo concreto: si coincide lo consume; si no, reporta y lanza la excepcion
    private Token esperar(TipoToken tipo) {
        if (es(tipo)) {
            return avanzar();
        }
        reportar(tipo.name());
        throw new ExcepcionSintactica();
    }

    //Registra el error con el token encontrado, lo esperado, fila y columna
    private void reportar(String esperado) {
        Token t = actual();
        errores.add(new ErrorSintactico(t.getTipo() + " (" + t.getLexema() + ")",
                esperado, t.getFila(), t.getColumna()));
    }

    //Modo panico: descarta tokens hasta uno de sincronizacion
    private void sincronizar() {
        while (!SINCRONIZACION.contains(actual().getTipo())) {
            pos++;
        }
    }
    
    //<unidad> -> ID | ε
    //Despues de un numero puede venir una palabra que lo describe ("100 palabras").
    //Si el token actual es ID la consume; si no, es la produccion vacia.
    private String unidad() {
        if (es(TipoToken.ID)) {
            return avanzar().getLexema();
        }
        return null; //ε: no consume nada
    }

    //<cargar> -> CARGAR PAR_A CADENA PAR_C
    private Termino cargar() {
        Token inicio = esperar(TipoToken.CARGAR);
        esperar(TipoToken.PAR_A);
        Token archivo = esperar(TipoToken.CADENA);
        esperar(TipoToken.PAR_C);
        return new Termino(Termino.Tipo.CARGAR, archivo.getLexema(), null,
                inicio.getFila(), inicio.getColumna());
    }

    //<termino> -> CADENA | ID | NUMERO <unidad> | <cargar>
    //Mira UN token de adelanto para decidir cual alternativa usar.
    private Termino termino() {
        Token t = actual();
        if (es(TipoToken.CADENA)) {
            avanzar();
            return new Termino(Termino.Tipo.CADENA, t.getLexema(), null, t.getFila(), t.getColumna());
        }
        if (es(TipoToken.ID)) {
            avanzar();
            return new Termino(Termino.Tipo.ID, t.getLexema(), null, t.getFila(), t.getColumna());
        }
        if (es(TipoToken.NUMERO)) {
            avanzar();
            String unidad = unidad();
            return new Termino(Termino.Tipo.NUMERO, t.getLexema(), unidad, t.getFila(), t.getColumna());
        }
        if (es(TipoToken.CARGAR)) {
            return cargar();
        }
        reportar("CADENA, ID, NUMERO o CARGAR");
        throw new ExcepcionSintactica();
    }

    //<expresion> -> <termino> <mas_terminos>
    private Expresion expresion() {
        Expresion e = new Expresion();
        e.agregar(termino());
        masTerminos(e);
        return e;
    }

    //<mas_terminos> -> MAS <termino> <mas_terminos> | ε
    //Si el token actual es MAS sigue; si no, es ε y termina sin error.
    private void masTerminos(Expresion e) {
        if (es(TipoToken.MAS)) {
            avanzar();
            e.agregar(termino());
            masTerminos(e);
        }
    }
    
    //<conectores> -> CONECTOR <expresion> <conectores> | ε
    //Acumula las parejas (palabra, argumento) en la lista que recibe.
    private void conectores(List<Conector> lista) {
        if (es(TipoToken.CONECTOR)) {
            String palabra = avanzar().getLexema();
            Expresion argumento = expresion();
            lista.add(new Conector(palabra, argumento));
            conectores(lista);
        }
    }

    //<comando> -> COMANDO_IA <expresion> <conectores> FLECHA ID
    private Comando comando() {
        Token inicio = esperar(TipoToken.COMANDO_IA);
        Expresion principal = expresion();

        List<Conector> lista = new ArrayList<>();
        conectores(lista);

        esperar(TipoToken.FLECHA);
        Token destino = esperar(TipoToken.ID);

        Comando c = new Comando(inicio.getLexema(), principal, destino.getLexema(),
                inicio.getFila(), destino.getFila());
        for (Conector con : lista) {
            c.agregarConector(con);
        }
        return c;
    }

    public List<ErrorSintactico> getErrores() {
        return errores;
    }
}
