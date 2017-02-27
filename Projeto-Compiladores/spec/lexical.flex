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

/* White spaces*/
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

/* Integer literals */

/* Float literals */

/* String and Character literals */

/* Comments */

%%

<YYINITIAL> {

    /* Keywords */
    "program"                      { return symbol(sym.PROGRAM); }
    
    /* Boolean literals*/

    /* Identifier*/

    /* Comments*/

    /* Separators */
    "("                             { return symbol(sym.LPAREN); }
    ")"                             { return symbol(sym.RPAREN); }
    "."   		  				    { return symbol(sym.DOT); }
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