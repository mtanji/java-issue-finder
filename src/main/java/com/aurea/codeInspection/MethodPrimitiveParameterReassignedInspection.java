package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiUnaryExpression;
import com.intellij.psi.tree.IElementType;
import com.siyeh.ig.psiutils.ParenthesesUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodPrimitiveParameterReassignedInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MethodPrimitiveParameterReassignedVisitor(holder);
    }

    class MethodPrimitiveParameterReassignedVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.method.primitive.parameter.reassigned.problem.description");
        private final ProblemsHolder holder;

        MethodPrimitiveParameterReassignedVisitor(final ProblemsHolder holder) {
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
            if (!(type instanceof PsiPrimitiveType)) {
                return;
            }
            holder.registerProblem(lhs, DESCRIPTION_TEMPLATE);
        }

        @Override
        public void visitUnaryExpression(@NotNull PsiUnaryExpression expression) {
            super.visitUnaryExpression(expression);
            final IElementType tokenType = expression.getOperationTokenType();
            if (!tokenType.equals(JavaTokenType.PLUSPLUS) && !tokenType.equals(JavaTokenType.MINUSMINUS)) {
                return;
            }
            final PsiExpression operand = expression.getOperand();
            if (operand == null) {
                return;
            }
            final PsiParameter parameter = getParameter(operand);
            if (parameter == null) {
                return;
            }
            PsiType type = parameter.getType();
            if (!(type instanceof PsiPrimitiveType)) {
                return;
            }
            holder.registerProblem(operand, DESCRIPTION_TEMPLATE);
        }

        @Nullable
        private PsiParameter getParameter(PsiExpression expression) {
            expression = ParenthesesUtils.stripParentheses(expression);
            if (!(expression instanceof PsiReferenceExpression)) {
                return null;
            }
            final PsiReferenceExpression referenceExpression = (PsiReferenceExpression)expression;
            final PsiElement variable = referenceExpression.resolve();
            if (!(variable instanceof PsiParameter)) {
                return null;
            }
            final PsiParameter parameter = (PsiParameter)variable;
            return !isApplicable(parameter) ? null : parameter;
        }
    }

    private boolean isApplicable(PsiParameter parameter) {
        return parameter.getDeclarationScope() instanceof PsiMethod;
    }
}
