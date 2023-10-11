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

        source                                              | result
        """print 1+1;"""                                    | "2"
        """print "aa" + "a";"""                             | "aaa"
        """print 1==1;"""                                   | "true"
        """var a = 1; print a;"""                           | "1"
        """var a = 1; a = 2; print a;"""                    | "2"
        """var a = 1; {var a = a + 1; print a;} print a;""" | "21"
    }
}
