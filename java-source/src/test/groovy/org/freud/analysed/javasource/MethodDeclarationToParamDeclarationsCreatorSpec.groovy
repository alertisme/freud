/*
 * Copyright 2013 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.freud.analysed.javasource

import spock.lang.Specification
import spock.lang.Subject

class MethodDeclarationToParamDeclarationsCreatorSpec extends Specification {

    @Subject
    MethodDeclarationToParamDeclarationsCreator creator = new MethodDeclarationToParamDeclarationsCreator()

    def 'returns import declarations for Java source'() {
    given:
        MethodDeclaration methodDeclaration = Mock()
        ParamDeclaration param1 = Mock()
        ParamDeclaration param2 = Mock()
        ParamDeclaration param3 = Mock()
        methodDeclaration.parametersDeclarations >> [param1, param2, param3]
    expect:
        creator.create(methodDeclaration) as List == [param1, param2, param3]
    }
}
