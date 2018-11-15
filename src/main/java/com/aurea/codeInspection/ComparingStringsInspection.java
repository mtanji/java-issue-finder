package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.siyeh.ig.psiutils.ComparisonUtils;
import com.siyeh.ig.psiutils.ExpressionUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ComparingStringsInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new ComparingStringVisitor(holder);
    }

    class ComparingStringVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.comparing.strings.problem.description");

        private final ProblemsHolder holder;

        ComparingStringVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitBinaryExpression(@NotNull PsiBinaryExpression expression) {
            super.visitBinaryExpression(expression);
            if (!ComparisonUtils.isEqualityComparison(expression)) {
                return;
            }
            final PsiExpression lhs = expression.getLOperand();
            if (!ExpressionUtils.hasStringType(lhs)) {
                return;
            }
            final PsiExpression rhs = expression.getROperand();
            if (!ExpressionUtils.hasStringType(rhs)) {
                return;
            }
            holder.registerProblem(expression.getOperationSign(), DESCRIPTION_TEMPLATE);
        }
    }
}