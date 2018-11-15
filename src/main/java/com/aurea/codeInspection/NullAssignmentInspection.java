package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInsight.NullableNotNullManager;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiConditionalExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiParenthesizedExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTypeCastExpression;
import com.intellij.psi.PsiVariable;
import com.siyeh.ig.psiutils.ParenthesesUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class NullAssignmentInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new NullAssignmentVisitor(holder);
    }

    private class NullAssignmentVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.null.assignment.problem.description");
        private final ProblemsHolder holder;

        NullAssignmentVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitLiteralExpression(
                @NotNull PsiLiteralExpression value) {
            super.visitLiteralExpression(value);
            final String text = value.getText();
            if (!PsiKeyword.NULL.equals(text)) {
                return;
            }
            PsiElement parent = value.getParent();
            while (parent instanceof PsiParenthesizedExpression ||
                    parent instanceof PsiConditionalExpression ||
                    parent instanceof PsiTypeCastExpression) {
                parent = parent.getParent();
            }
            if (!(parent instanceof PsiAssignmentExpression)) {
                return;
            }
            final PsiAssignmentExpression assignmentExpression =
                    (PsiAssignmentExpression) parent;
            final PsiExpression lhs = ParenthesesUtils.stripParentheses(
                    assignmentExpression.getLExpression());
            if (lhs == null || isReferenceToNullableVariable(lhs)) {
                return;
            }
            holder.registerProblem(lhs, DESCRIPTION_TEMPLATE);
        }

        private boolean isReferenceToNullableVariable(
                PsiExpression lhs) {
            if (!(lhs instanceof PsiReferenceExpression)) {
                return false;
            }
            final PsiReferenceExpression referenceExpression =
                    (PsiReferenceExpression) lhs;
            final PsiElement element = referenceExpression.resolve();
            if (!(element instanceof PsiVariable)) {
                return false;
            }
            final PsiVariable variable = (PsiVariable) element;
            return NullableNotNullManager.isNullable(variable);
        }
    }
}
