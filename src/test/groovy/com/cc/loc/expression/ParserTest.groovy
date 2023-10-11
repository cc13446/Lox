package com.cc.loc.expression

import com.cc.lox.parser.Parser
import com.cc.lox.parser.printer.ExpressionPrinter
import com.cc.lox.scanner.Scanner
import spock.lang.Specification


/**
 * @author cc
 * @date 2023/10/10
 */
class ParserTest extends Specification {

    def "test parser"() {
        given:
        Scanner scanner = new Scanner(source)

        when:
        Parser parser = new Parser(scanner.scanTokens())

        def res = new ExpressionPrinter().print(parser.parseExpression())
        then:
        res == result

        where:
        source     | result
        """1==1""" | """(== 1.0 1.0)"""
    }
}
