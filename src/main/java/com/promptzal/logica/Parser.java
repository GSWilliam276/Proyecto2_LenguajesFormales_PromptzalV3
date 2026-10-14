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

    public List<ErrorSintactico> getErrores() {
        return errores;
    }
}
