package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTryStatement;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EmptyTryBlockInspectionTest {

    @Test
    public void givenTryIsNullWhenVisitTryStatementThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        EmptyTryBlockInspection.EmptyTryBlockVisitor visitor = new EmptyTryBlockInspection.EmptyTryBlockVisitor(holder);
        PsiTryStatement tryStatement = mock(PsiTryStatement.class);

        visitor.visitTryStatement(tryStatement);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
