package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NullAssignmentInspectionTest {

    @Test
    public void givenAssignmentIsInvalidWhenVisitLiteralExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        NullAssignmentInspection.NullAssignmentVisitor visitor = new NullAssignmentInspection.NullAssignmentVisitor(holder);
        PsiLiteralExpression expression = mock(PsiLiteralExpression.class);

        visitor.visitLiteralExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
