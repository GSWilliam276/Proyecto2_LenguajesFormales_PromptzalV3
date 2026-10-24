package com.promptzal.logica;

import com.promptzal.modelo.Token;
import com.promptzal.modelo.TipoToken;
import com.promptzal.modelo.ErrorLexico;
import java.util.ArrayList;
import java.util.List;

%%

%public
%class AnalizadorLexicoJFlex
%unicode
%line
%column
%type Token
%function siguienteToken
%state COMENTARIO

%{
    private int contadorTokens = 0;
    private final List<ErrorLexico> errores = new ArrayList<>();
    private int filaComentario, columnaComentario;

    //JFlex cuenta fila y columna desde 0; el proyecto las pide desde 1
    private Token token(TipoToken tipo, String lexema) {
        contadorTokens++;
        return new Token(contadorTokens, lexema, tipo.name(), yyline + 1, yycolumn + 1);
    }

    private void error(String lexema, String descripcion) {
        errores.add(new ErrorLexico(lexema, descripcion, yyline + 1, yycolumn + 1));
    }

    public List<ErrorLexico> getErrores() {
        return errores;
    }
%}

LETRA  = [\p{L}_]
ALNUM  = [\p{L}\p{N}_]
DIGITO = [0-9]

%%

<YYINITIAL> {

    //Espacios y comentarios: se descartan, nunca llegan al parser
    [ \t\r\n]+      { }
    "//" [^\n]*     { }
    "/*"            { filaComentario = yyline + 1;
                      columnaComentario = yycolumn + 1;
                      yybegin(COMENTARIO); }

    //Directivas. Si no es una de las tres, el error mas largo la atrapa
    "@modelo" | "@rol" | "@formato"   { return token(TipoToken.DIRECTIVA, yytext()); }
    "@" {ALNUM}*                      { error(yytext(), "Directiva no reconocida"); }

    //Palabras de estructura y comandos (antes que ID: a igual largo gana la primera regla)
    "AGENTE"        { return token(TipoToken.AGENTE, yytext()); }
    "contexto"      { return token(TipoToken.CONTEXTO, yytext()); }
    "variable"      { return token(TipoToken.VARIABLE, yytext()); }
    "EJECUTAR"      { return token(TipoToken.EJECUTAR, yytext()); }
    "EXPORTAR"      { return token(TipoToken.EXPORTAR, yytext()); }
    "CARGAR"        { return token(TipoToken.CARGAR, yytext()); }

    "PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER"
                    { return token(TipoToken.COMANDO_IA, yytext()); }

    "SOBRE" | "DESDE" | "EN" | "COMO"
                    { return token(TipoToken.CONECTOR, yytext()); }

    {LETRA} {ALNUM}*   { return token(TipoToken.ID, yytext()); }

    //Numeros. "12." es un error y el match mas largo lo hace ganar sobre "12"
    {DIGITO}+ "." {DIGITO}+   { return token(TipoToken.NUMERO, yytext()); }
    {DIGITO}+                 { return token(TipoToken.NUMERO, yytext()); }
    {DIGITO}+ "."             { error(yytext(), "Caracter no reconocido"); }

    //Cadenas: el lexema se guarda sin las comillas
    \" [^\"\n]* \"  { String s = yytext();
                      return token(TipoToken.CADENA, s.substring(1, s.length() - 1)); }
    \" [^\"\n]*     { error(yytext().substring(1), "Cadena sin cerrar"); }

    //Simbolos
    "->"            { return token(TipoToken.FLECHA, yytext()); }
    "="             { return token(TipoToken.IGUAL, yytext()); }
    "+"             { return token(TipoToken.MAS, yytext()); }
    "{"             { return token(TipoToken.LLAVE_A, yytext()); }
    "}"             { return token(TipoToken.LLAVE_C, yytext()); }
    "("             { return token(TipoToken.PAR_A, yytext()); }
    ")"             { return token(TipoToken.PAR_C, yytext()); }
    ","             { return token(TipoToken.COMA, yytext()); }

    //Cualquier otro caracter (incluye "-" y "/" sueltos): error y se sigue
    [^]             { error(yytext(), "Caracter no reconocido"); }
}

<COMENTARIO> {
    "*/"            { yybegin(YYINITIAL); }
    [^]             { }
}

<YYINITIAL> <<EOF>>  { return token(TipoToken.EOF, ""); }

<COMENTARIO> <<EOF>> { errores.add(new ErrorLexico("/*", "Comentario de bloque sin cerrar",
                                                   filaComentario, columnaComentario));
                       yybegin(YYINITIAL);
                       return token(TipoToken.EOF, ""); }
