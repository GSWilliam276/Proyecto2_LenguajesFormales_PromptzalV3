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
import com.promptzal.ast.Agente;
import com.promptzal.ast.Variable;
import com.promptzal.ast.Directiva;
import com.promptzal.ast.Programa;
import com.promptzal.ast.Referencia;
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
        String encontrado = t.getLexema().isEmpty() ? t.getTipo() : t.getTipo() + " (" + t.getLexema() + ")";
        errores.add(new ErrorSintactico(encontrado, esperado, t.getFila(), t.getColumna()));
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

    //<contexto> -> CONTEXTO IGUAL CADENA
    private String contexto() {
        esperar(TipoToken.CONTEXTO);
        esperar(TipoToken.IGUAL);
        return esperar(TipoToken.CADENA).getLexema();
    }

    //<variables> -> VARIABLE ID IGUAL <expresion> <variables> | ε
    //Cero o mas declaraciones. Si una falla, se recupera y se sigue con la siguiente.
    private void variables(Agente a) {
        if (es(TipoToken.VARIABLE)) {
            try {
                avanzar();
                Token nombre = esperar(TipoToken.ID);
                esperar(TipoToken.IGUAL);
                Expresion valor = expresion();
                a.agregarVariable(new Variable(nombre.getLexema(), valor,
                        nombre.getFila(), nombre.getColumna()));
            } catch (ExcepcionSintactica e) {
                sincronizar();
            }
            variables(a);
        }
    }

    //Un intento de parsear un comando; si falla, se recupera y el agente sigue
    private void comandoSeguro(Agente a) {
        try {
            a.agregarComando(comando());
        } catch (ExcepcionSintactica e) {
            sincronizar();
        }
    }

    //<comandos> -> <comando> <mas_comandos>
    //Al menos un comando: no hay alternativa vacia, comando() exige COMANDO_IA.
    private void comandos(Agente a) {
        comandoSeguro(a);
        masComandos(a);
    }

    //<mas_comandos> -> <comando> <mas_comandos> | ε
    private void masComandos(Agente a) {
        if (es(TipoToken.COMANDO_IA)) {
            comandoSeguro(a);
            masComandos(a);
        }
    }

    //<agente> -> AGENTE ID LLAVE_A <contexto> <variables> <comandos> LLAVE_C
    private Agente agente() {
        esperar(TipoToken.AGENTE);
        Token nombre = esperar(TipoToken.ID);
        esperar(TipoToken.LLAVE_A);

        Agente a = new Agente(nombre.getLexema(), nombre.getFila());
        int erroresAntes = errores.size();
        try {
            a.setContexto(contexto());
        } catch (ExcepcionSintactica e) {
            sincronizar();
        }
        variables(a);
        comandos(a);

        //Recuperacion: tras un error dentro del agente pueden quedar variables o comandos
        //sueltos; se siguen analizando para no abandonar el resto del programa.
        //Cada vuelta consume al menos un token, asi que el ciclo termina.
        while (es(TipoToken.VARIABLE) || es(TipoToken.COMANDO_IA)) {
            if (errores.size() == erroresAntes) {
                reportar("LLAVE_C"); //no habia error previo: el orden del agente es invalido
            }
            variables(a);
            if (es(TipoToken.COMANDO_IA)) {
                comandoSeguro(a);
                masComandos(a);
            }
        }

        try {
            esperar(TipoToken.LLAVE_C);
        } catch (ExcepcionSintactica e) {
            sincronizar();
            if (es(TipoToken.LLAVE_C)) {
                avanzar(); //la llave que cierra el agente tambien se consume
            }
        }
        return a;
    }
        
    //<directivas> -> DIRECTIVA CADENA <directivas> | ε
    private void directivas(Programa p) {
        if (es(TipoToken.DIRECTIVA)) {
            try {
                Token d = avanzar();
                Token valor = esperar(TipoToken.CADENA);
                p.agregarDirectiva(new Directiva(d.getLexema(), valor.getLexema(), d.getFila()));
            } catch (ExcepcionSintactica e) {
                sincronizar();
            }
            directivas(p);
        }
    }

    //Un intento de parsear un agente; si falla, se recupera y el programa sigue
    private void agenteSeguro(Programa p) {
        try {
            p.agregarAgente(agente());
        } catch (ExcepcionSintactica e) {
            sincronizar();
            if (es(TipoToken.LLAVE_C)) {
                avanzar();
            }
        }
    }

    //<agentes> -> <agente> <mas_agentes>   (al menos un agente)
    private void agentes(Programa p) {
        agenteSeguro(p);
        masAgentes(p);
    }

    //<mas_agentes> -> <agente> <mas_agentes> | ε
    private void masAgentes(Programa p) {
        if (es(TipoToken.AGENTE)) {
            agenteSeguro(p);
            masAgentes(p);
        }
    }

    private void ejecucionSegura(Programa p) {
        try {
            esperar(TipoToken.EJECUTAR);
            Token id = esperar(TipoToken.ID);
            p.agregarEjecucion(new Referencia(id.getLexema(), id.getFila(), id.getColumna()));
        } catch (ExcepcionSintactica e) {
            sincronizar();
        }
    }

    //<ejecuciones> -> EJECUTAR ID <mas_ejecuciones>   (al menos una)
    private void ejecuciones(Programa p) {
        ejecucionSegura(p);
        masEjecuciones(p);
    }

    //<mas_ejecuciones> -> EJECUTAR ID <mas_ejecuciones> | ε
    private void masEjecuciones(Programa p) {
        if (es(TipoToken.EJECUTAR)) {
            ejecucionSegura(p);
            masEjecuciones(p);
        }
    }

    //<exportar> -> EXPORTAR ID <mas_ids>   (exactamente un EXPORTAR)
    private void exportar(Programa p) {
        try {
            esperar(TipoToken.EXPORTAR);
            Token id = esperar(TipoToken.ID);
            p.agregarExportacion(new Referencia(id.getLexema(), id.getFila(), id.getColumna()));
            masIds(p);
        } catch (ExcepcionSintactica e) {
            sincronizar();
        }
    }

    //<mas_ids> -> COMA ID <mas_ids> | ε
    private void masIds(Programa p) {
        if (es(TipoToken.COMA)) {
            avanzar();
            Token id = esperar(TipoToken.ID);
            p.agregarExportacion(new Referencia(id.getLexema(), id.getFila(), id.getColumna()));
            masIds(p);
        }
    }

    //<programa> -> <directivas> <agentes> <ejecuciones> <exportar> EOF
    public Programa programa() {
        Programa p = new Programa();
        directivas(p);
        agentes(p);
        ejecuciones(p);
        exportar(p);
        if (!es(TipoToken.EOF)) {
            reportar("EOF"); //quedaron tokens sueltos despues del EXPORTAR
        }
        return p;
    }

    public List<ErrorSintactico> getErrores() {
        return errores;
    }
}
