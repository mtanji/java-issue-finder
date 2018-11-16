package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiUnaryExpression;
import com.intellij.psi.tree.IElementType;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MethodPrimitiveParameterReassignedInspectionTest {

    @Test
    public void givenAssignmentIsInvalidWhenVisitAssignmentExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        MethodPrimitiveParameterReassignedInspection.MethodPrimitiveParameterReassignedVisitor visitor = new MethodPrimitiveParameterReassignedInspection.MethodPrimitiveParameterReassignedVisitor(
                holder);
        PsiAssignmentExpression expression = mock(PsiAssignmentExpression.class);

        visitor.visitAssignmentExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenAssignmentIsInvalidWhenVisitUnaryExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        MethodPrimitiveParameterReassignedInspection.MethodPrimitiveParameterReassignedVisitor visitor = new MethodPrimitiveParameterReassignedInspection.MethodPrimitiveParameterReassignedVisitor(
                holder);
        PsiUnaryExpression expression = mock(PsiUnaryExpression.class);
        doReturn(mock(IElementType.class)).when(expression).getOperationTokenType();

        visitor.visitUnaryExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
