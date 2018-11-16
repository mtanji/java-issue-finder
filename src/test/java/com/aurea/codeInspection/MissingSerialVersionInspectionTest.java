package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.siyeh.ig.psiutils.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@PrepareForTest(SerializationUtils.class)
@RunWith(PowerMockRunner.class)
public class MissingSerialVersionInspectionTest {

    @Test
    public void givenClassIsNotSerializableWhenVisitClassThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        PsiClass clazz = mock(PsiClass.class);
        MissingSerialVersionInspection.MissingSerialVersionVisitor visitor = new MissingSerialVersionInspection.MissingSerialVersionVisitor(holder);
        PowerMockito.mockStatic(SerializationUtils.class);
        PowerMockito.when(SerializationUtils.isSerializable(any(PsiClass.class))).thenReturn(false);

        visitor.visitClass(clazz);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }
}
