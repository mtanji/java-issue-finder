package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiThrowStatement;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TopLevelExceptionInspectionTest {

    @Test
    public void givenTopLevelExceptionNotIdentifiedWhenVisitStatementThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        TopLevelExceptionInspection.TopLevelExceptionVisitor visitor = new TopLevelExceptionInspection.TopLevelExceptionVisitor(holder);
        PsiThrowStatement statement = mock(PsiThrowStatement.class);

        visitor.visitThrowStatement(statement);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
