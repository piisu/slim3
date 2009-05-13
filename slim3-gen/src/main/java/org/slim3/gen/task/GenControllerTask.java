/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.gen.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slim3.gen.Constants;
import org.slim3.gen.desc.ControllerDesc;
import org.slim3.gen.desc.ControllerDescFactory;
import org.slim3.gen.generator.ControllerGenerator;
import org.slim3.gen.generator.ControllerTestCaseGenerator;
import org.slim3.gen.generator.Generator;
import org.slim3.gen.printer.Printer;
import org.slim3.gen.util.CloseableUtil;
import org.slim3.gen.util.StringUtil;
import org.xml.sax.InputSource;

/**
 * Represents a task to generate a controller java file.
 * 
 * @author taedium
 * @since 3.0
 * 
 */
public class GenControllerTask extends AbstractTask {

    /** the source directory */
    protected File srcDir;

    /** the test source directory */
    protected File testDir;

    /**
     * Sets the srcDir.
     * 
     * @param srcDir
     *            the srcDir to set
     */
    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * Sets the testDir.
     * 
     * @param testDir
     *            the testDir to set
     */
    public void setTestDir(File testDir) {
        this.testDir = testDir;
    }

    public void doExecute() throws IOException, XPathExpressionException {
        if (srcDir == null) {
            throw new IllegalStateException("The srcDir parameter is null.");
        }
        if (testDir == null) {
            throw new IllegalStateException("The testDir parameter is null.");
        }
        if (warDir == null) {
            throw new IllegalStateException("The warDir parameter is null.");
        }
        if (controllerPath == null) {
            throw new IllegalStateException(
                    "The controllerPath parameter is null.");
        }
        String path = controllerPath.startsWith("/") ? controllerPath : "/"
                + controllerPath;
        String controllerPackageName = findControllerPackageName();
        ControllerDescFactory factory = createControllerDescFactory(controllerPackageName);
        ControllerDesc controllerDesc = factory.createControllerDesc(path);
        generateController(controllerDesc);
        generateControllerTestCase(controllerDesc);
    }

    /**
     * Creates a {@link ControllerDescFactory}.
     * 
     * @param controllerPackageName
     *            the base package name of controllers.
     * @return a factory
     */
    protected ControllerDescFactory createControllerDescFactory(
            String controllerPackageName) {
        return new ControllerDescFactory(controllerPackageName);
    }

    /**
     * Finds a base package name of controllers.
     * 
     * @return a base package name of controlle
     * @throws FileNotFoundException
     * @throws XPathExpressionException
     */
    protected String findControllerPackageName() throws FileNotFoundException,
            XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if (prefix == null)
                    throw new NullPointerException(
                            "The parameter prefix is null.");
                else if ("pre".equals(prefix))
                    return "http://appengine.google.com/ns/1.0";
                else if ("xml".equals(prefix))
                    return XMLConstants.XML_NS_URI;
                return XMLConstants.NULL_NS_URI;
            }

            public String getPrefix(String uri) {
                throw new UnsupportedOperationException("getPrefix");
            }

            public Iterator<?> getPrefixes(String uri) {
                throw new UnsupportedOperationException("getPrefixes");
            }
        });
        File file = new File(new File(warDir, "WEB-INF"), "appengine-web.xml");
        InputStream inputStream = new FileInputStream(file);
        try {
            String value = xpath
                    .evaluate(
                            "/pre:appengine-web-app/pre:system-properties/pre:property[@name='slim3.controllerPackage']/@value",
                            new InputSource(inputStream));
            if (StringUtil.isEmpty(value)) {
                throw new RuntimeException(
                        "The system-property 'slim3.controllerPackage' is not found in appengine-web.xml or the system-property value is empty.");
            }
            return value;
        } finally {
            CloseableUtil.close(inputStream);
        }
    }

    /**
     * Generates a controller.
     * 
     * @param controllerDesc
     *            the controller description
     * @throws IOException
     */
    protected void generateController(ControllerDesc controllerDesc)
            throws IOException {
        File packageDir = new File(srcDir, controllerDesc.getPackageName()
                .replace(".", File.separator));
        packageDir.mkdirs();
        File javaFile = new File(packageDir, controllerDesc.getSimpleName()
                .replace('.', '/')
                + ".java");
        String className = controllerDesc.getPackageName() + "."
                + controllerDesc.getSimpleName();
        Generator generator = careateControllerGenerator(controllerDesc);
        generate(generator, javaFile, className);
    }

    /**
     * Creates a {@link Generator}.
     * 
     * @param controllerDesc
     *            the controller description
     * @return a generator
     */
    protected Generator careateControllerGenerator(ControllerDesc controllerDesc) {
        return new ControllerGenerator(controllerDesc);
    }

    /**
     * Generates a controller test case.
     * 
     * @param controllerDesc
     *            the controller description
     * @throws IOException
     */
    protected void generateControllerTestCase(ControllerDesc controllerDesc)
            throws IOException {
        File packageDir = new File(testDir, controllerDesc.getPackageName()
                .replace(".", File.separator));
        packageDir.mkdirs();
        File javaFile = new File(packageDir, controllerDesc.getSimpleName()
                + Constants.TEST_SUFFIX + ".java");
        String className = controllerDesc.getPackageName() + "."
                + controllerDesc.getSimpleName() + Constants.TEST_SUFFIX;
        Generator generator = careateControllerTestCaseGenerator(controllerDesc);
        generate(generator, javaFile, className);
    }

    /**
     * Creates a {@link Generator}.
     * 
     * @param controllerDesc
     *            the controller description
     * @return a generator
     */
    protected Generator careateControllerTestCaseGenerator(
            ControllerDesc controllerDesc) {
        return new ControllerTestCaseGenerator(controllerDesc);
    }

    /**
     * Generates a file.
     * 
     * @param generator
     * @param file
     * @throws IOException
     */
    protected void generate(Generator generator, File file, String className)
            throws IOException {
        if (file.exists()) {
            log("Already exists. Skipped generation. (" + className
                    + ".java:0)");
            return;
        }
        Printer printer = null;
        try {
            printer = createPrinter(file);
            generator.generate(printer);
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
        log("Generated. (" + className + ".java:0)");
    }
}