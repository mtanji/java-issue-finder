package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
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

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new TopLevelExceptionVisitor(holder);
    }

    static class TopLevelExceptionVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.throws.toplevel.exception.problem.description");
        private final Set<String> exceptions =
                new HashSet<>(Arrays.asList(
                        "java.lang.Throwable",
                        "java.lang.Exception"
                ));
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