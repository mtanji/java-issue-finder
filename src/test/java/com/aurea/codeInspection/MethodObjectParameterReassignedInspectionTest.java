package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MethodObjectParameterReassignedInspectionTest {

    @Test
    public void givenAssignmentIsInvalidWhenVisitAssignmentExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        MethodObjectParameterReassignedInspection.MethodObjectParameterReassignedVisitor visitor = new MethodObjectParameterReassignedInspection.MethodObjectParameterReassignedVisitor(holder);
        PsiAssignmentExpression expression = mock(PsiAssignmentExpression.class);

        visitor.visitAssignmentExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
