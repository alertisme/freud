package org.freud.core.iterator

import org.freud.core.Creator
import org.freud.core.SubTypesCreator
import spock.lang.Specification
import spock.lang.Subject

import static AnalysedObjectBreadcrumbs.BREADCRUMBS

class SubTypeAnalysedObjectIteratorSpec extends Specification {

    @Subject
    SubTypeAnalysedObjectIterator iterator
    SubTypesCreator creator

    def setup() {
        creator = Mock()
        iterator = new SubTypeAnalysedObjectIterator(creator, ['a', 'b', 'c', 'd'])
    }

    def 'element contains several sub types'() {
    given:
        creator.create('a', _) >> { _, list -> list.addAll(['a1', 'a2', 'a3']) }
        creator.create(!'a', _) >> { _, list -> list.add('x') }
    when:
        List results = []
        for (Object o : iterator) {
            results.add(o)
        }
    then:
        results == ['a1', 'a2', 'a3', 'x', 'x', 'x']
    }

    def 'element contains no sub types'() {
    given:
        creator.create('a', _)
        creator.create(!'a', _) >> { _, list -> list.add('x') }
    when:
        List results = []
        for (Object o : iterator) {
            results.add(o)
        }
    then:
        results == ['x', 'x', 'x']
    }

    def 'element contains one sub types'() {
    given:
        creator.create('a', _) >> { _, list -> list.add('a') }
        creator.create(!'a', _) >> { _, list -> list.add('x') }
    when:
        List results = []
        for (Object o : iterator) {
            results.add(o)
        }
    then:
        results == ['a', 'x', 'x', 'x']
    }

    def 'elements contains variations of sub types'() {
    given:
        creator.create('a', _)
        creator.create('b', _) >> { _, list -> list.addAll(['b1', 'b2', 'b3']) }
        creator.create('c', _) >> { _, list -> list.addAll(['c1', 'c2']) }
        creator.create('d', _) >> { _, list -> list.add('d') }
    when:
        List results = []
        for (Object o : iterator) {
            results.add(o)
        }
    then:
        results == ['b1', 'b2', 'b3', 'c1', 'c2', 'd']
    }

    def 'analysed object saved in breadcrumbs'() {
    given:
        creator.create('a', _)
        creator.create('b', _) >> { _, list -> list.addAll(['b1', 'b2']) }
        creator.create('c', _) >> { _, list -> list.addAll(['c1', 'c2']) }
        creator.create('d', _) >> { _, list -> list.add('d') }
    when:
        iterator.next()
    then:
        BREADCRUMBS.size() == 1
        BREADCRUMBS.get(0) == 'b'
    when:
        iterator.next()
    then:
        BREADCRUMBS.size() == 1
        BREADCRUMBS.get(0) == 'b'
    when:
        iterator.next()
    then:
        BREADCRUMBS.size() == 1
        BREADCRUMBS.get(0) == 'c'
    }

    def 'analysed object appended to breadcrumbs'() {
    given:
        iterator = new SubTypeAnalysedObjectIterator(creator,
                        new AnalysedObjectIterator({ "X$it" } as Creator, ['a', 'b', 'c', 'd']))
        creator.create('Xa', _)
        creator.create('Xb', _) >> { _, list -> list.addAll(['b1', 'b2']) }
        creator.create('Xc', _) >> { _, list -> list.addAll(['c1', 'c2']) }
        creator.create('Xd', _) >> { _, list -> list.add('d') }
    when:
        iterator.next()
    then:
        BREADCRUMBS.size() == 2
        BREADCRUMBS.get(0) == 'b'
        BREADCRUMBS.get(1) == 'Xb'
    when:
        iterator.next()
    then:
        BREADCRUMBS.size() == 2
        BREADCRUMBS.get(0) == 'b'
        BREADCRUMBS.get(1) == 'Xb'
        when:
        iterator.next()
    then:
        BREADCRUMBS.size() == 2
        BREADCRUMBS.get(0) == 'c'
        BREADCRUMBS.get(1) == 'Xc'
    }
}