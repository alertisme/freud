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

package org.freud.analysed.javasource.jdom;


import org.freud.analysed.javasource.Annotation;
import org.freud.analysed.javasource.VarDeclaration;

import javax.xml.bind.Element;
import java.util.List;

final class FieldDeclarationJdom implements VarDeclaration {
    private final String type;
    private final boolean finalVariable;
    private final String name;

    public FieldDeclarationJdom(Element varDeclElement) {
        type = "";
        name = "";
        finalVariable = false;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isFinalVariable() {
        return finalVariable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Annotation> getDeclaredAnnotations() {

        // TODO
        return null;
    }
}
