package com.cc.loc.scanner

import com.cc.lox.scanner.Scanner
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors
/**
 * @author cc
 * @date 2023/10/8
 */
class ScannerTest extends Specification {

    @Unroll
    def "test token #source #result"() {
        given:
        def target = new Scanner(source)

        when:
        def res = target.scanTokens().stream().map { a -> a.getLexeme() }.collect(Collectors.toList())
        then:
        res == result

        where:
        source                  | result
        """var a = 1.11 """     | ["var", "a", "=", "1.11", ""]
        """var b = "1.11" """   | ["var", "b", "=", "\"1.11\"", ""]
        """fun foo(var a) {}""" | ["fun", "foo", "(", "var", "a", ")", "{", "}", ""]
    }
}
