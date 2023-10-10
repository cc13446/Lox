package com.cc.loc.expression.interpreter

import com.cc.lox.interpreter.ExpressionInterpreter
import com.cc.lox.parser.Parser
import com.cc.lox.scanner.Scanner
import spock.lang.Specification


/**
 * @author cc
 * @date 2023/10/10
 */
class ExpressionInterpreterTest extends Specification {
    def "test interpreter"() {
        given:
        Scanner scanner = new Scanner(source)
        Parser parser = new Parser(scanner.scanTokens())

        when:
        ExpressionInterpreter interpreter = new ExpressionInterpreter()
        def res = interpreter.interpret(parser.parse())

        then:
        res == result

        where:

        source             | result
        "1+1"              | "2"
        """ "aa" + "a" """ | "aaa"
        "1==1"             | "true"

    }
}
