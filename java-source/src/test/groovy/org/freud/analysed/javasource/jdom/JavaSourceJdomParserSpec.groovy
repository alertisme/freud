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



package org.freud.analysed.javasource.jdom

import spock.lang.Specification

import static org.freud.analysed.javasource.JavaSourceParsingFixture.exampleJavaSourceResourceParsedBy
import static org.freud.analysed.javasource.jdom.JavaSourceJdomParser.parseJavaSource

class JavaSourceJdomParserSpec extends Specification {


    def 'parses a file into a Java source'() {
    expect:
        exampleJavaSourceResourceParsedBy { URL url ->
            parseJavaSource(new InputStreamReader(url.openStream()), url.toExternalForm())
        }
    }
}
