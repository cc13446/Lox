package com.cc.loc.token

import com.cc.lox.token.Scanner
import com.cc.lox.token.Token
import com.cc.lox.token.type.TokenType
import spock.lang.Specification

import java.util.stream.Collectors


/**
 * @author cc
 * @date 2023/10/8
 */
class ScannerTest extends Specification {

    def "test token" () {
        given:
        def source = "var a = 1.11"
        def target = new Scanner(source)

        when:
        target.scanTokens()
        def res = target.getResult().stream().map {a -> a.getType()}.collect(Collectors.toList())
        then:
        res == [TokenType.VAR, TokenType.IDENTIFIER, TokenType.EQUAL, TokenType.NUMBER, TokenType.EOF]
    }
}
