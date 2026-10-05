/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.logica;

import com.promptzal.modelo.Token;
import com.promptzal.modelo.ErrorLexico;
import java.util.ArrayList;
import java.util.List;
import com.promptzal.modelo.Estado;
import com.promptzal.modelo.TipoEstado;
import com.promptzal.modelo.Transicion;
/**
 * Analizador lexico manual para PromptZal.
 * Cada metodo esta comentado con su correspondencia exacta al AFD
 * formal disenado en recursos/afd/afd.dot (estados q0 a q19).
 *
 * @author eduar
 */
public class AnalizadorLexico {
    //Atributos
    private String texto;
    private int posicion;
    private int fila;
    private int columna;
    private int contadorTokens;

    private List<Token> listaTokens;
    private List<ErrorLexico> listaErrores;
    private final DefinicionAFD afd = new DefinicionAFD();

    //Listas de palabras conocidas, para clasificar contra ellas
    private static final String[] PALABRAS_RESERVADAS = {"AGENTE", "contexto", "variable", "EJECUTAR", "EXPORTAR"};
    private static final String[] COMANDOS_IA = {"PREGUNTAR", "GENERAR", "RESUMIR", "ANALIZAR", "TRADUCIR", "CLASIFICAR", "EXTRAER", "CARGAR"};
    private static final String[] CONECTORES_PALABRA = {"SOBRE", "DESDE", "EN", "COMO"};

    //Constructor
    public AnalizadorLexico(String texto) {
        this.texto = texto;
        this.posicion = 0;
        this.fila = 1;
        this.columna = 1;
        this.contadorTokens = 0;
        this.listaTokens = new ArrayList<>();
        this.listaErrores = new ArrayList<>();
    }
    
    private char espiar() {
        //Mira el caracter en la posicion siguiente
        if (posicion + 1 < texto.length()) {
            return texto.charAt(posicion + 1);
        }
        return '\0'; //Caracter nulo, indica que no hay siguiente caracter
    }
    
    private void avanzar() {
        //Mueve el puntero una posicion hacia adelante, actualiza fila/columna 
        //correctamente segun lo que se acaba de dejar atras
        if (texto.charAt(posicion) == '\n') {
            fila++;
            columna = 1;
        } else {
            columna++;
        }
        posicion++;
    }
    
    //Rama identificador / palabra reservada / comando / conector de palabra.
    //AFD: q0 --letra--> q_id (estado de aceptacion, tipo decidido al vuelo
    //por clasificarPalabra). El lazo q_id --letra/digito--> q_id corresponde
    //al while de abajo.
    private void leerPalabra() {
        int filaInicio = fila;
        int columnaInicio = columna;
        StringBuilder palabra = new StringBuilder();

        //Mientras el caracter actual sea letra, digito o guion bajo, se sigue acumulando
        while (posicion < texto.length() && esCaracterDePalabra(texto.charAt(posicion))) {
            palabra.append(texto.charAt(posicion));
            avanzar();
        }

        String lexema = palabra.toString();
        String tipo = clasificarPalabra(lexema);

        contadorTokens++;
        Token token = new Token(contadorTokens, lexema, tipo, filaInicio, columnaInicio);
        listaTokens.add(token);
    }

    //Determina si un caracter puede formar parte de una palabra (identificador,
    //palabra reservada, comando o conector)
    private boolean esCaracterDePalabra(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private String clasificarPalabra(String palabra) {
        //Respeta el orden de prioridad
        if (contiene(PALABRAS_RESERVADAS, palabra)) {
            return "PALABRA_RESERVADA";
        }
        if (contiene(COMANDOS_IA, palabra)) {
            return "COMANDO_IA";
        }
        if (contiene(CONECTORES_PALABRA, palabra)) {
            return "CONECTOR";
        }
        return "IDENTIFICADOR";
    }

    private boolean contiene(String[] arreglo, String valor) {
        //Recorre el arreglo comparando uno por uno
        for (String s : arreglo) {
            if (s.equals(valor)) {
                return true;
            }
        }
        return false;
    }
    
    //Rama cadena. AFD: q0 --"--> q8 (transito, NO aceptacion) --otro--> q8 (lazo)
    //q8 --"--> q9 (aceptacion: token CADENA)
    //q8 --salto de linea--> q10 (estado de error: "Cadena sin cerrar")
    private void leerCadena() {
        int filaInicio = fila;
        int columnaInicio = columna;
        StringBuilder cadena = new StringBuilder();

        avanzar(); //Se salta la comilla de apertura, no se guarda como parte del contenido

        boolean cerrada = false;
        while (posicion < texto.length()) {
            char actual = texto.charAt(posicion);
        
            if (actual == '"') {
                avanzar(); //Se salta la comilla de cierre tambien
                cerrada = true;
                break;
            }
            if (actual == '\n') {
                //La cadena no puede cruzar un salto de linea sin cerrarse
                break;
            }
        
            cadena.append(actual);
            avanzar();
        }

        if (cerrada) {
            contadorTokens++;
            Token token = new Token(contadorTokens, cadena.toString(), "CADENA", filaInicio, columnaInicio);
            listaTokens.add(token);
        } else {
            ErrorLexico error = new ErrorLexico(cadena.toString(), "Cadena sin cerrar", filaInicio, columnaInicio);
            listaErrores.add(error);
        }
    }
    
    //Rama directiva. AFD: q0 --@--> q11 (transito) --letra/digito--> q11 (lazo)
    //Al salir del lazo: q11 --valida--> q12 (aceptacion: token DIRECTIVA)
    //                    q11 --no valida--> q13 (estado de error)
    //La validacion contra {modelo, rol, formato} se resuelve "al vuelo"
    //con esDirectivaValida(), no es una transicion por simbolo del AFD.
    private void leerDirectiva() {
        int filaInicio = fila;
        int columnaInicio = columna;
        StringBuilder directiva = new StringBuilder();
    
        avanzar(); //Se salta el simbolo @, no se guarda como parte del contenido
    
        while (posicion < texto.length() && esCaracterDePalabra(texto.charAt(posicion))) {
            directiva.append(texto.charAt(posicion));
            avanzar();
        }
    
        String lexema = directiva.toString();
    
        //Si no es una directiva valida (incluyendo el caso de quedar vacia),
        //se reporta como error lexico en vez de generar un token
        if (!esDirectivaValida(lexema)) {
            ErrorLexico error = new ErrorLexico("@" + lexema, "Directiva no reconocida", filaInicio, columnaInicio);
            listaErrores.add(error);
            return;
        }
    
        contadorTokens++;
        Token token = new Token(contadorTokens, lexema, "DIRECTIVA", filaInicio, columnaInicio);
        listaTokens.add(token);
    }

    private boolean esDirectivaValida(String directiva) {
        String[] directivasValidas = {"modelo", "rol", "formato"};
        return contiene(directivasValidas, directiva);
    }
    
    //Dispatcher principal: representa el estado inicial q0 del AFD completo.
    //Cada rama de este if/else es la transicion que sale de q0 segun el
    //primer caracter leido, delegando el resto del recorrido al metodo
    //correspondiente (ver comentarios de cada uno para sus estados internos).
    public void analizar() {
        while (posicion < texto.length()) {
            char actual = texto.charAt(posicion);

            if (actual == ' ' || actual == '\t' || actual == '\r' || actual == '\n') {
                //Espacios en blanco y saltos de linea se ignoran, solo se avanza
                avanzar();
            } else if (Character.isLetter(actual) || actual == '_') {
                leerPalabra();
            } else if (actual == '@') {
                leerDirectiva();
            } else if (actual == '"') {
                leerCadena();
            } else if (Character.isDigit(actual)) {
                reconocer();
            } else if (actual == '/') {
                reconocer();
            } else if (actual == '-') {
                leerConectorFlecha();
            } else if (actual == '=' || actual == '+' || actual == '{' || actual == '}' || actual == '(' || actual == ')' || actual == ',') {
                leerSimboloSuelto(actual);
            } else {
                //No encaja en ninguna categoria valida: caracter no reconocido
                int filaError = fila;
                int columnaError = columna;
                ErrorLexico error = new ErrorLexico(String.valueOf(actual), "Caracter no reconocido", filaError, columnaError);
                listaErrores.add(error);
                avanzar();
            }
        }
    }
    
    //Rama conector flecha. AFD: q0 --guion--> q2 (transito, decidiendo)
    //q2 --mayor que--> q3 (aceptacion: token CONECTOR "->")
    //q2 --otro caracter--> q4 (estado de error: "Caracter no reconocido")
    private void leerConectorFlecha() {
        //Maneja el caso de -, que puede ser -> o error
        int filaInicio = fila;
        int columnaInicio = columna;

        if (espiar() == '>') {
            avanzar(); // salta el -
            avanzar(); // salta el >
            contadorTokens++;
            Token token = new Token(contadorTokens, "->", "CONECTOR", filaInicio, columnaInicio);
            listaTokens.add(token);
        } else {
            ErrorLexico error = new ErrorLexico("-", "Caracter no reconocido", filaInicio, columnaInicio);
            listaErrores.add(error);
            avanzar();
        }
    }
    
    //Rama simbolo suelto. AFD: q0 --(=,+,{,},(,),,)--> q1 (aceptacion unica,
    //compartida por los siete simbolos). Sin lazo, sin acumulacion: la
    //clasificacion OPERADOR/DELIMITADOR se decide al vuelo segun cual
    //simbolo especifico disparo la transicion.
    private void leerSimboloSuelto(char simbolo) {
        //Maneja los simbolos de un solo caracter
        int filaInicio = fila;
        int columnaInicio = columna;
        String tipo = (simbolo == '=' || simbolo == '+') ? "OPERADOR" : "DELIMITADOR";

        avanzar();
        contadorTokens++;
        Token token = new Token(contadorTokens, String.valueOf(simbolo), tipo, filaInicio, columnaInicio);
        listaTokens.add(token);
    }
    
    //Motor: recorre el AFD consultando DefinicionAFD en cada caracter.
    private void reconocer() {
        int filaInicio = fila;
        int columnaInicio = columna;
        StringBuilder lexema = new StringBuilder();
        String estado = "q0";

        while (true) {
            int c = (posicion < texto.length()) ? texto.charAt(posicion) : -1;
            Transicion t = afd.mover(estado, c);
            if (t == null) {
                break; //no hay camino: el estado actual decide que pasa
            }

            //Regla de consumo: "otro" hacia aceptacion o error NO consume,
            //porque ese caracter es del siguiente token. Tampoco se consume el fin de archivo.
            Estado destino = afd.getEstado(t.getDestino());
            boolean consume = c != -1
                    && !(t.getSimbolo().equals("otro") && destino.getTipo() != TipoEstado.TRANSITO);
            if (consume) {
                lexema.append((char) c);
                avanzar();
            }
            estado = t.getDestino();
        }

        //El tipo del estado final decide el resultado
        Estado fin = afd.getEstado(estado);
        switch (fin.getTipo()) {
            case ACEPTACION:
                contadorTokens++;
                listaTokens.add(new Token(contadorTokens, lexema.toString(), fin.getEtiqueta(), filaInicio, columnaInicio));
                break;
            case ERROR:
                listaErrores.add(new ErrorLexico(lexema.toString(), fin.getMensaje(), filaInicio, columnaInicio));
                break;
            default:
                break; //aceptacion sin token (comentario): se descarta
        }
    }
    
    //Getters
    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public List<ErrorLexico> getListaErrores() {
        return listaErrores;
    }
}
