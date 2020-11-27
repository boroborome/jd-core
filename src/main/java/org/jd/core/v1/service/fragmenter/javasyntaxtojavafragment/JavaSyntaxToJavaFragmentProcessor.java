/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.model.javafragment.ImportsFragment;
import org.jd.core.v1.model.javasyntax.CompilationUnit;
import org.jd.core.v1.model.message.DecompileContext;
import org.jd.core.v1.model.processor.Processor;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.visitor.CompilationUnitVisitor;
import org.jd.core.v1.service.fragmenter.javasyntaxtojavafragment.visitor.SearchImportsVisitor;

/**
 * Convert a Java syntax model to a list of fragments.<br><br>
 *
 * Input:  {@link org.jd.core.v1.model.javasyntax.CompilationUnit}<br>
 * Output: List<{@link org.jd.core.v1.model.fragment.Fragment}><br>
 */
public class JavaSyntaxToJavaFragmentProcessor implements Processor {

    public void process(DecompileContext decompileContext) throws Exception {
        Loader loader = decompileContext.getHeader("loader");
        String mainInternalTypeName = decompileContext.getHeader("mainInternalTypeName");
        int majorVersion = decompileContext.getHeader("majorVersion");
        CompilationUnit compilationUnit = decompileContext.getBody();

        SearchImportsVisitor importsVisitor = new SearchImportsVisitor(loader, mainInternalTypeName);
        importsVisitor.visit(compilationUnit);
        ImportsFragment importsFragment = importsVisitor.getImportsFragment();
        decompileContext.setHeader("maxLineNumber", importsVisitor.getMaxLineNumber());

        CompilationUnitVisitor visitor = new CompilationUnitVisitor(loader, mainInternalTypeName, majorVersion, importsFragment);
        visitor.visit(compilationUnit);
        decompileContext.setBody(visitor.getFragments());
    }
}
