package com.aurea.codeInspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiThrowStatement;
import com.intellij.psi.PsiType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TopLevelExceptionInspection extends AbstractBaseJavaLocalInspectionTool {
    @NonNls
    private static final String DESCRIPTION_TEMPLATE ="Prohibited exception thrown '#ref' #loc";

    final Set<String> exceptions =
        new HashSet<>(Arrays.asList(
                    "java.lang.Throwable",
                    "java.lang.Exception"
            ));

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new TopLevelExceptionVisitor(holder);
    }

    private class TopLevelExceptionVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;
        TopLevelExceptionVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitThrowStatement(PsiThrowStatement statement) {
            super.visitThrowStatement(statement);
            final PsiExpression exception = statement.getException();
            if (exception == null) {
                return;
            }
            final PsiType type = exception.getType();
            if (type == null) {
                return;
            }
            final String text = type.getCanonicalText();
            if (exceptions.contains(text)) {
                holder.registerProblem(statement.getException(), DESCRIPTION_TEMPLATE);
            }
        }
    }
}