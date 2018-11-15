package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.LambdaUtil;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiEllipsisType;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiTypesUtil;
import com.siyeh.ig.psiutils.ExpressionUtils;
import com.siyeh.ig.psiutils.MethodCallUtils;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class OptionalAssignedWithNullInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new OptionalAssignedWithNullVisitor(holder);
    }

    static class OptionalAssignedWithNullVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.optional.assigned.with.null.problem.description");
        private final ProblemsHolder holder;

        OptionalAssignedWithNullVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
            checkNulls(expression.getType(), expression.getRExpression());
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression call) {
            PsiExpression[] args = call.getArgumentList().getExpressions();
            if (args.length == 0) {
                return;
            }
            PsiMethod method = call.resolveMethod();
            if (method == null) {
                return;
            }
            PsiParameter[] parameters = method.getParameterList().getParameters();
            if (parameters.length > args.length) {
                return;
            }
            boolean varArgCall = MethodCallUtils.isVarArgCall(call);
            if (!varArgCall && parameters.length < args.length) {
                return;
            }
            for (int i = 0; i < args.length; i++) {
                PsiParameter parameter = parameters[Math.min(parameters.length - 1, i)];
                PsiType type = parameter.getType();
                if (varArgCall && i >= parameters.length - 1 && type instanceof PsiEllipsisType) {
                    type = ((PsiEllipsisType) type).getComponentType();
                }
                checkNulls(type, args[i]);
            }
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression lambda) {
            PsiElement body = lambda.getBody();
            if (body instanceof PsiExpression) {
                checkNulls(LambdaUtil.getFunctionalInterfaceReturnType(lambda), (PsiExpression) body);
            }
        }

        @Override
        public void visitReturnStatement(PsiReturnStatement statement) {
            checkNulls(PsiTypesUtil.getMethodReturnType(statement), statement.getReturnValue());
        }

        @Override
        public void visitVariable(PsiVariable variable) {
            checkNulls(variable.getType(), variable.getInitializer());
        }

        private void checkNulls(PsiType type, PsiExpression expression) {
            if (expression != null && TypeUtils.isOptional(type)) {
                ExpressionUtils.nonStructuralChildren(expression).filter(ExpressionUtils::isNullLiteral)
                        .forEach(nullLiteral -> register(nullLiteral));
            }
        }

        private void register(PsiExpression expression) {
            holder.registerProblem(expression, DESCRIPTION_TEMPLATE);
        }
    }
}
