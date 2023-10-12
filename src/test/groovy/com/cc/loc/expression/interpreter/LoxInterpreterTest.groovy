package com.cc.loc.expression.interpreter

import com.cc.lox.interpreter.LoxInterpreter
import com.cc.lox.parser.Parser
import com.cc.lox.scanner.Scanner
import spock.lang.Specification


/**
 * @author cc
 * @date 2023/10/10
 */
class LoxInterpreterTest extends Specification {
    def "test interpreter"() {
        given:
        Scanner scanner = new Scanner(source)
        Parser parser = new Parser(scanner.scanTokens())

        when:
        LoxInterpreter interpreter = new LoxInterpreter()
        interpreter.interpret(parser.parse())
        def res = interpreter.getPrint()

        then:
        res == result

        where:

        source | result
        """
            print 1 + 1;
        """
               | "2"
        """
            print "aa" + "a";
        """
               | "aaa"
        """
            print 1 == 1;
        """
               | "true"
        """
            var a = 1; 
            print a;
        """
               | "1"
        """
            var a = 1; 
            a = 2; 
            print a;
        """
               | "2"
        """
            var a = 1; 
            {
                var a = a + 1; 
                print a;
            } 
            print a;
        """
               | "21"
        """
            var a = 1; 
            if (a == 1) {
                print a;
            } 
            print 2;
        """
               | "12"
        """
            var a = 1; 
            if (a == 2 or a == 3) {
                print a;
            } else {
                print a + 1;
            }
            print 2;
        """
               | "22"
        """
            var a = 1;
            while (a != 10) {
                a = a + 1;
            }
            print a;
        """
                | "10"
        """
            var a = 0;
            var temp;

            for (var b = 1; a < 10000; b = temp + b) {
                print a;
                print " ";
                temp = a;
                a = b;
            }
        """
                | "0 1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987 1597 2584 4181 6765 "
    }
}
