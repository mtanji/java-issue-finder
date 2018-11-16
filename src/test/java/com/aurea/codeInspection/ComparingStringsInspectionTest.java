package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ComparingStringsInspectionTest {

    @Test
    public void givenIssueIsNotFoundWhenVisitBinaryExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        ComparingStringsInspection.ComparingStringVisitor visitor = new ComparingStringsInspection.ComparingStringVisitor(holder);
        PsiBinaryExpression expression = mock(PsiBinaryExpression.class);
        doReturn(mock(IElementType.class)).when(expression).getOperationTokenType();

        visitor.visitBinaryExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
