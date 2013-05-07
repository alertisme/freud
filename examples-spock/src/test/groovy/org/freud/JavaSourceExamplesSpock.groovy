package org.freud

import org.spockframework.runtime.ConditionNotSatisfiedError
import spock.lang.FailsWith
import spock.lang.Specification

import static org.freud.analysed.javasource.JavaSourceDsl.classDeclarationsWithin
import static org.freud.analysed.javasource.JavaSourceDsl.codeBlocksWithin
import static org.freud.analysed.javasource.JavaSourceDsl.javaSourceOf
import static org.freud.analysed.javasource.JavaSourceDsl.methodCallsWithin
import static org.freud.analysed.javasource.JavaSourceDsl.methodDeclarationsWithin
import static org.freud.analysed.javasource.JavaSourceDsl.packageDeclarationsWithin
import static org.freud.groovy.Freud.analyse
import static org.freud.groovy.Freud.resourcesOf

class JavaSourceExamplesSpock extends Specification {

    def 'package depth must be between 3 and 7'() {
    expect:
        analyse(analysed) { it.packagePath.length >= 3 && it.packagePath.length <= 7 }
    where:
        analysed << packageDeclarationsWithin(javaSourceOf(resourcesOf(['javasource/second/third/ExampleClass.javasrc'])))
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'package depth must be between 3 and 7 - failing test'() {
    expect:
        analyse(analysed) { it.packagePath.length >= 3 && it.packagePath.length <= 7 }
    where:
        analysed << packageDeclarationsWithin(javaSourceOf(resourcesOf([
                'javasource/second/third/fourth/fifth/sixth/seventh/eighth/ExampleClassWithPackageDepthOfEight.javasrc'])))
    }

    def 'no System.out.print(ln) in code'() {
    expect:
        analyse(analysed) { !(it.instanceReferences == ['System', 'out'] && it.methodName.startsWith('print')) }
    where:
        analysed << methodCallsWithin(codeBlocksWithin(methodDeclarationsWithin(classDeclarationsWithin(
                javaSourceOf(resourcesOf(['javasource/second/third/ExampleClass.javasrc']))))))
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'no System.out.print(ln) in code - failing test'() {
    expect:
        analyse(analysed) { !(it.instanceReferences == ['System', 'out'] && it.methodName.startsWith('print')) }
    where:
        analysed << methodCallsWithin(codeBlocksWithin(methodDeclarationsWithin(classDeclarationsWithin(
                javaSourceOf(resourcesOf(['javasource/ClassWithSystemOutPrint.javasrc']))))))
    }

    def 'code block limited to max 30 lines'() {
    expect:
        analyse(analysed) { it.numberOfLines <= 30 }
    where:
        analysed << codeBlocksWithin(methodDeclarationsWithin(classDeclarationsWithin(
                javaSourceOf(resourcesOf(['javasource/second/third/ExampleClass.javasrc',
                                          'javasource/ClassWith30LineMethod.javasrc'])))))
    }

    @FailsWith(ConditionNotSatisfiedError)
    def 'code block limited to max 30 lines - failing test'() {
    expect:
        analyse(analysed) { println it; it.numberOfLines; it.numberOfLines <= 30 }
    where:
        analysed << codeBlocksWithin(methodDeclarationsWithin(classDeclarationsWithin(
                javaSourceOf(resourcesOf(['javasource/ClassWithLongMethod.javasrc'])))))
    }

/*

  public static FreudAnalyser codeBlockSizeIsLimitedTo30Lines(final AnalysedObjectIterator<JavaSource> iterator)
  {
      return Freud.iterateOver(CodeBlock.class).
              assertThat(codeBlockLines().lessThanOrEqualTo(30)).within(iterator);
  }

  public static FreudAnalyser codeBlockSizeIsLimitedToOneLineIfFreudNotSuppressed(final AnalysedObjectIterator<JavaSource> iterator)
  {
      return Freud.iterateOver(CodeBlock.class).
              forEach(no(method(hasDeclaredAnnotation("SuppressWarnings", Matchers.containsString("\"freud:"))))).
              assertThat(codeBlockLines().lessThanOrEqualTo(1)).within(iterator);
  }
*/
}
