package org.freud

import org.freud.analysed.css.rule.CssRule
import org.freud.analysed.css.rule.selector.CssSelector
import org.spockframework.runtime.ConditionNotSatisfiedError
import spock.lang.FailsWith
import spock.lang.Specification

import static org.freud.analysed.css.CssDsl.cssDeclarationsWithin
import static org.freud.analysed.css.CssDsl.cssRulesOf
import static org.freud.analysed.css.CssDsl.cssSelectorsWithin
import static org.freud.analysed.css.rule.selector.CssSelector.Combinator.DESCENDANT
import static org.freud.analysed.css.rule.selector.CssSelector.Type.CLASS
import static org.freud.analysed.css.rule.selector.CssSelector.Type.ID
import static org.freud.analysed.css.rule.selector.CssSelector.Type.TAG
import static org.freud.groovy.Freud.analyse
import static org.freud.groovy.Freud.forEach

class CssExamplesSpock extends Specification {

    static URL root = ClassLoader.getSystemResource('CssExamples/')

    def 'classOrIdCssSelectorsNameMustNotContainUpperCaseCharacters'() {
    expect:
        analyse(analysed) { !it.selectorString.matches(".*[A-Z].*") }
    where:
        analysed << forEach(cssSelectorsWithin(cssRulesOf([new URL(root, 'file.css').text])), {it.type == CLASS || it.type == ID})
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'classOrIdCssSelectorsNameMustNotContainUpperCaseCharacters - failing test'() {
    expect:
        analyse(analysed) { !it.selectorString.matches(".*[A-Z].*") }
    where:
        analysed << forEach(cssSelectorsWithin(cssRulesOf([new URL(root, 'classWithUpperCase.css').text])), {it.type == CLASS || it.type == ID})
    }


    def 'cssDisplayDeclarationIsAlwaysNone'() {
    expect:
        analyse(analysed) { it.value == 'none' }
    where:
        analysed << forEach(cssDeclarationsWithin(cssRulesOf([new URL(root, 'displayNone.css').text])), { it.key == 'display'})
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'cssDisplayDeclarationIsAlwaysNone - failing test'() {
    expect:
        analyse(analysed) { it.value == 'none' }
    where:
        analysed << forEach(cssDeclarationsWithin(cssRulesOf([new URL(root, 'displayBlock.css').text])), { it.key == 'display'})
    }

    /**
     * see https://developer.mozilla.org/en/Writing_Efficient_CSS
     */
    def 'doNotQualifyIdRuleWithTagOrClassName'() {
    expect:
        analyse(analysed) { CssSelector selector -> selector.combinator != DESCENDANT }
    where:
        analysed << forEach(cssSelectorsWithin(cssRulesOf([new URL(root, 'file.css').text])), { it.type == ID })
    }

    @FailsWith(ConditionNotSatisfiedError) // slightly different iteration because test requires to fail on EVERY analysed object
    def 'doNotQualifyIdRuleWithTagOrClassName - failing test'() {
    expect:
        analyse(analysed) { CssRule rule ->
            rule.cssSelectors.every { CssSelector selector ->
                selector.type != ID || selector.combinator != DESCENDANT
            }
        }
    where:
        analysed << forEach(cssRulesOf([new URL(root, 'doNotQualifyIdRuleWithTagOrClassName.css').text]))
    }

    /**
     * see https://developer.mozilla.org/en/Writing_Efficient_CSS
     */
    def 'doNotQualifyClassRuleWithTagName'() {
    expect:
        analyse(analysed) { CssRule rule ->
            int firstClassIndex = rule.cssSelectors.findIndexOf { it.type == CLASS }

            int firstTagIndex = rule.cssSelectors.findIndexOf { it.type == TAG }

            return firstTagIndex == -1 || firstClassIndex == -1 || firstClassIndex < firstTagIndex
        }
    where:
        analysed << cssRulesOf([new URL(root, 'file.css').text])
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'doNotQualifyClassRuleWithTagName - failing test'() {
    expect:
        analyse(analysed) { CssRule rule ->

            int firstClassIndex = rule.cssSelectors.findIndexOf { it.type == CLASS }

            int firstTagIndex = rule.cssSelectors.findIndexOf { it.type == TAG }

            return firstTagIndex == -1 || firstClassIndex == -1 || firstClassIndex < firstTagIndex
        }
    where:
        analysed << cssRulesOf([new URL(root, 'doNotQualifyClassRuleWithTagName.css').text])
    }

    /**
     * see http://css-tricks.com/efficiently-rendering-css/
     */
    def 'descendantSelectorsAreTheWorst'() {
    expect:
        analyse(analysed) { CssRule rule ->
            rule.cssSelectors.size() <= 1
        }
    where:
        analysed << cssRulesOf([new URL(root, 'descendantSelectorsAreTheWorst.css').text])
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'descendantSelectorsAreTheWorst failing test'() {
    expect:
        analyse(analysed) { CssRule rule ->
            rule.cssSelectors.size() <= 1
        }
    where:
        analysed << cssRulesOf([new URL(root, 'descendantSelectorsAreTheWorst_failing.css').text])
    }
}
