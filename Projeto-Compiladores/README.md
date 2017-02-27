This is a project developed as a practical examination of Compiler's subject at UFCG.

We are using the EBNF definition available in this [site](http://www.fit.vutbr.cz/study/courses/APR/public/ebnf.html)

To execute, run this in the command line:

java -jar libs/jflex-1.6.1.jar --noinputstreamctor -d ./src/compiler/generated/ ./spec/lexical.flex

java -jar libs/java-cup-11a.jar -compact_red -expect 10000 -package compiler.generated -destdir ./src/compiler/generated -parser Parser ./spec/parser.cup
