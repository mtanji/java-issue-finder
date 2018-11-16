package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.siyeh.ig.psiutils.ParenthesesUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodObjectParameterReassignedInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MethodObjectParameterReassignedVisitor(holder);
    }

    static class MethodObjectParameterReassignedVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.method.object.parameter.reassigned.problem.description");
        private final ProblemsHolder holder;

        MethodObjectParameterReassignedVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitAssignmentExpression(@NotNull PsiAssignmentExpression expression) {
            super.visitAssignmentExpression(expression);
            final PsiExpression lhs = expression.getLExpression();
            final PsiParameter parameter = getParameter(lhs);

            if (parameter == null) {
                return;
            }
            PsiType type = parameter.getType();
            if (type instanceof PsiPrimitiveType) {
                return;
            }
            holder.registerProblem(lhs, DESCRIPTION_TEMPLATE);
        }

        @Nullable
        private PsiParameter getParameter(PsiExpression expression) {
            expression = ParenthesesUtils.stripParentheses(expression);
            if (!(expression instanceof PsiReferenceExpression)) {
                return null;
            }
            final PsiReferenceExpression referenceExpression = (PsiReferenceExpression) expression;
            final PsiElement variable = referenceExpression.resolve();
            if (!(variable instanceof PsiParameter)) {
                return null;
            }
            final PsiParameter parameter = (PsiParameter) variable;
            return !isApplicable(parameter) ? null : parameter;
        }

        private boolean isApplicable(PsiParameter parameter) {
            return parameter.getDeclarationScope() instanceof PsiMethod;
        }
    }
}
