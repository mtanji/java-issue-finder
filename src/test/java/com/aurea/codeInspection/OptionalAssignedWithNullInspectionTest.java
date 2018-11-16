package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiVariable;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OptionalAssignedWithNullInspectionTest {

    @Test
    public void givenAssignmentIsValidWhenVisitAssignmentExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor visitor = new OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor(holder);
        PsiAssignmentExpression expression = mock(PsiAssignmentExpression.class);

        visitor.visitAssignmentExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenLambdaIsValidWhenVisitLambdaExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor visitor = new OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor(holder);
        PsiLambdaExpression expression = mock(PsiLambdaExpression.class);

        visitor.visitLambdaExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenMethodCallIsValidWhenVisitMethodCallExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor visitor = new OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor(holder);
        PsiMethodCallExpression expression = mock(PsiMethodCallExpression.class);
        PsiExpressionList expressionList = mock(PsiExpressionList.class);
        doReturn(expressionList).when(expression).getArgumentList();
        doReturn(new PsiExpression[]{}).when(expressionList).getExpressions();

        visitor.visitMethodCallExpression(expression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenReturnIsValidWhenVisitReturnStatementThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor visitor = new OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor(holder);
        PsiReturnStatement statement = mock(PsiReturnStatement.class);

        visitor.visitReturnStatement(statement);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenVariableIsValidWhenVisitVariableThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor visitor = new OptionalAssignedWithNullInspection.OptionalAssignedWithNullVisitor(holder);
        PsiVariable variable = mock(PsiVariable.class);

        visitor.visitVariable(variable);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

}
