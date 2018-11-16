package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiVariable;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RawTypesInspectionTest {

    @Test
    public void givenRawTypeNotIdentifiedWhenVisitVariableThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        RawTypesInspection.RawTypesVisitor visitor = new RawTypesInspection.RawTypesVisitor(holder);
        PsiVariable variable = mock(PsiVariable.class);

        visitor.visitVariable(variable);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
