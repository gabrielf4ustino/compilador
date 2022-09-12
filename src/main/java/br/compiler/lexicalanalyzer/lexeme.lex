package br.compiler.lexicalanalyzer;

import br.compiler.language.LanguageToken;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

%%

%{

public Set<String> keyWords = Stream.of("integer","boolean","true","false","read","write","return","goto","if").collect(Collectors.toSet());

public Set<String> identifiers = new HashSet<String>();

private LanguageToken createToken(String name, String value, Integer line, Integer column) {
    return new LanguageToken(name, value, line + 1, column + 1);
}

private void setIdentifiers(String identifier){
    identifiers.add(identifier);
}

%}

%public
%class LexicalAnalyzer
%type LanguageToken
%unicode
%char
%line
%column
%caseless

SPACE = [\r| |\t]
NEWLINE = [\n]
ID = [_|a-z|A-Z][a-z|A-Z|0-9|_]*
ARITHMETICOPERATORS = ["+"|"-"|"/"|"*"]
INTEGER = 0|[1-9][0-9]*
PARENTHESES = ["("|")"]
CURLYBRACES = ["{"|"}"]
BRACKET = ["["|"]"]
OR = ["|"]
QUOTATIONMARKS = ["\""]
COMMA = [","]

%%

{PARENTHESES}                 { return createToken("Parênteses", yytext(), yyline, yycolumn); }
{CURLYBRACES}                 { return createToken("Chaves", yytext(), yyline, yycolumn); }
{QUOTATIONMARKS}              { return createToken("Aspas", yytext(), yyline, yycolumn); }
{COMMA}                       { return createToken("Vígula", yytext(), yyline, yycolumn); }
{BRACKET}                     { return createToken("Colchetes", yytext(), yyline, yycolumn); }
{OR}                          { return createToken("Alternativa", yytext(), yyline, yycolumn); }
{SPACE}                       { return createToken("Espaço em branco", " ", yyline, yycolumn); }
{NEWLINE}                     { return createToken("Nova linha", " ", yyline + 1, yycolumn); }
{ID}                          {
                                if(keyWords.contains(yytext().toLowerCase())){
                                    return createToken("Palavra reservada", yytext(), yyline, yycolumn);
                                } else if(!identifiers.contains(yytext().toLowerCase())){
                                      setIdentifiers(yytext().toLowerCase());
                                      return createToken("Identificador", yytext(), yyline, yycolumn);
                                }
                              }
{ARITHMETICOPERATORS}         { return createToken("Operador", yytext(), yyline, yycolumn); }
{INTEGER}                     { return createToken("Número Inteiro", yytext(), yyline, yycolumn); }

. { return createToken("Caractere inválido", yytext(), yyline, yycolumn); }