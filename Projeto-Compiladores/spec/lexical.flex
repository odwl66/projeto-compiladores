package compiler.generated;
import java_cup.runtime.*;
import compiler.core.Token;

%%

%public
%class Scanner
%cup

%{
   StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
	return new Token(type);
  }

  private Symbol symbol(int type, Object value) {
	return new Token(type, value);
  }
%}

/* Identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* White spaces*/
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

/* Integer literals */
Sign = "+" | "-"
DecimalLiteral 	= 0 | [1-9][0-9]*
DigitSequence 	= {Sign}?{DecimalLiteral}
IntegerNumber 	= {DigitSequence}

/* Float literals */

/* String and Character literals */

/* Comments */

%%

<YYINITIAL> {

    /* Keywords */
    "program"                      { return symbol(sym.PROGRAM); }
    "label"                      { return symbol(sym.LABEL); }
    
    /* Boolean literals*/

    /* Identifier*/
	{Identifier} 					{ return symbol(sym.IDENTIFIER,yytext());}
	
	{IntegerNumber}                { return symbol(sym.INTEGER_NUMBER, new Integer(yytext())); }
	
    /* Comments*/

    /* Separators */
    "("                             { return symbol(sym.LPAREN); }
    ")"                             { return symbol(sym.RPAREN); }
    "."   		  				    { return symbol(sym.DOT); }
    ","                             { return symbol(sym.COMMA); }
    ";"                             { return symbol(sym.SEMICOLON); }

    /* String literal */

    /* Character literal */

    /* White spaces */
    {WhiteSpace}				    { /*just ignore it*/ }

    /* Arithmetical operators*/
    
    /* Operators */
     
    /* Logical Operators*/

    /* Assignment */
}