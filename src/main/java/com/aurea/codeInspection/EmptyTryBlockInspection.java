package com.aurea.codeInspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiTryStatement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class EmptyTryBlockInspection extends AbstractBaseJavaLocalInspectionTool {

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Empty Try block: #ref";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new EmptyTryBlockVisitor(holder);
    }

    class EmptyTryBlockVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;
        EmptyTryBlockVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitTryStatement(@NotNull PsiTryStatement statement) {
            super.visitTryStatement(statement);
            final PsiCodeBlock finallyBlock = statement.getTryBlock();
            if (finallyBlock == null) {
                return;
            }
            if (!finallyBlock.isEmpty()) {
                return;
            }
            holder.registerProblem(statement, DESCRIPTION_TEMPLATE);
        }
    }
}
