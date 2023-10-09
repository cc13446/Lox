package com.cc.loc.expression

import com.cc.lox.scanner.Scanner
import com.cc.lox.parser.expression.impl.BinaryExpression
import com.cc.lox.parser.expression.impl.GroupingExpression
import com.cc.lox.parser.expression.impl.LiteralExpression
import com.cc.lox.parser.expression.impl.UnaryExpression
import com.cc.lox.parser.printer.ExpressionPrinter
import com.cc.lox.scanner.Token
import com.cc.lox.scanner.type.TokenType
import spock.lang.Specification


/**
 * @author cc
 * @date 2023/10/9
 */
class ExpressionPrinterTest extends Specification {

    def "printer test"() {
        given:
        def expression = new BinaryExpression(
                new UnaryExpression(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new LiteralExpression(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new GroupingExpression(
                        new LiteralExpression(45.67)))

        when:
        def res = new ExpressionPrinter().print(expression)

        then:
        res == "(* (- 123) (group 45.67))"
    }
}
