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

/* Integer literals */
DecimalLiteral = 0 | [1-9][0-9]*

%%

{DecimalLiteral}                { return symbol(sym.INTEGER_LITERAL, new Integer(yytext())); }

/* White spaces */
" "							    { /*just ignore it*/ }

/* Arithmetical operators*/
"+"								{ return symbol(sym.PLUS); }
"-"								{ return symbol(sym.MINUS); }
"*"								{ return symbol(sym.MULT); }
"/"								{ return symbol(sym.DIV); }

/* Parenthesis */
"("								{ return symbol(sym.LPAREN); }
")"								{ return symbol(sym.RPAREN); }