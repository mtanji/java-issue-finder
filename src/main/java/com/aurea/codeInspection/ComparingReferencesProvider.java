package com.aurea.codeInspection;

import com.intellij.codeInspection.InspectionToolProvider;

public class ComparingReferencesProvider implements InspectionToolProvider {

    public Class[] getInspectionClasses() {
        return new Class[]{ComparingReferencesInspection.class};
    }
}
