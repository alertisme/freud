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

package org.freud.core.parser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.StringWriter;

public final class JdomTreeAdaptor extends CommonTreeAdaptor {

    private static final TokenType NULL_TOKEN_TYPE = new NullTokenType();

    public static final String ID_ATTR = "id";
    public static final String LINE_NUMBER_ATTR = "lineNumber";
    public static final String COL_NUMBER_ATTR = "colNumber";
    private Element rootElement;
    private TokenType[] tokenTypes;

    public JdomTreeAdaptor(final String rootElementName, final TokenType[] tokenTypes) {
        this.rootElement = new Element(rootElementName);
        this.tokenTypes = tokenTypes;

    }

    public Document getDocument() {
        return new Document(rootElement);
    }

    public Object create(Token payload) {
        return new JdomTree(payload);
    }

    public static String documentToString(final Document document) {
        XMLOutputter outputter = new XMLOutputter();
        StringWriter writer = new StringWriter();
        try {
            outputter.output(document, writer);
            return writer.toString();
        }
        catch (IOException e) {
            return e.toString();
        }
    }

    public final class JdomTree extends CommonTree {
        private TokenType tokenType = NULL_TOKEN_TYPE;
        private Element element;

        private JdomTree(Token token) {
            super(token);
            if (token != null) {
                tokenType = tokenTypes[token.getType()];
                element = new Element(tokenType.getName());
                element.setText(token.getText());
                if (isPositionSet(token)) {
                    element.setAttribute(LINE_NUMBER_ATTR, String.valueOf(token.getLine()));
                    element.setAttribute(COL_NUMBER_ATTR, String.valueOf(token.getCharPositionInLine()));
                }
            }
            else {
                element = rootElement;
            }
        }

        @Override
        public Tree dupNode() {
            return new JdomTree(getToken());
        }

        @Override
        public void setParent(Tree parentTree) {
            JdomTree parent = (JdomTree) parentTree;
            super.setParent(parent);
            setParentElement(parent);
        }

        private void setParentElement(final JdomTree parent) {
            if (parent != null) {

                if (element.getParent() != null) {
                    element.getParent().removeContent(element);
                }
                parent.element.addContent(element);
                if (tokenType.isIdent()) {
                    if (parent.tokenType.isIdRequired() && parent.element.getAttribute(ID_ATTR) == null) {
                        parent.element.setAttribute(ID_ATTR, token.getText());
                    }
                    if (parent.tokenType.isUseIdLocation() && isPositionSet(token)) {
                        parent.element.setAttribute(LINE_NUMBER_ATTR, String.valueOf(token.getLine()));
                        parent.element.setAttribute(COL_NUMBER_ATTR, String.valueOf(token.getCharPositionInLine()));
                    }
                }
            }
        }

        public Element getElement() {
            return element;
        }
    }

    private static boolean isPositionSet(final Token token) {
        return token.getCharPositionInLine() >= 0;
    }

    private static final class NullTokenType implements TokenType {
        public boolean isIdRequired() {
            return false;
        }

        public boolean isUseIdLocation() {
            return false;
        }

        public String getName() {
            return null;
        }

        public boolean isIdent() {
            return false;
        }
    }
}
