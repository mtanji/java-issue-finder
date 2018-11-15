package com.aurea.codeInspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.LambdaUtil;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClassType;
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
    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Null is used for ''Optional'' type in {0}";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new OptionalAssignedWithNullVisitor(holder);
    }

    static class OptionalAssignedWithNullVisitor extends JavaElementVisitor {
        private final ProblemsHolder holder;
        OptionalAssignedWithNullVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
            checkNulls(expression.getType(), expression.getRExpression(),
                    InspectionsBundle.message("inspection.null.value.for.optional.context.assignment"));
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression call) {
            PsiExpression[] args = call.getArgumentList().getExpressions();
            if (args.length == 0) return;
            PsiMethod method = call.resolveMethod();
            if (method == null) return;
            PsiParameter[] parameters = method.getParameterList().getParameters();
            if (parameters.length > args.length) return;
            boolean varArgCall = MethodCallUtils.isVarArgCall(call);
            if (!varArgCall && parameters.length < args.length) return;
            for (int i = 0; i < args.length; i++) {
                PsiParameter parameter = parameters[Math.min(parameters.length - 1, i)];
                PsiType type = parameter.getType();
                if (varArgCall && i >= parameters.length - 1 && type instanceof PsiEllipsisType) {
                    type = ((PsiEllipsisType)type).getComponentType();
                }
                checkNulls(type, args[i], InspectionsBundle.message("inspection.null.value.for.optional.context.parameter"));
            }
        }

        @Override
        public void visitLambdaExpression(PsiLambdaExpression lambda) {
            PsiElement body = lambda.getBody();
            if (body instanceof PsiExpression) {
                checkNulls(LambdaUtil.getFunctionalInterfaceReturnType(lambda), (PsiExpression)body,
                        InspectionsBundle.message("inspection.null.value.for.optional.context.lambda"));
            }
        }

        @Override
        public void visitReturnStatement(PsiReturnStatement statement) {
            checkNulls(PsiTypesUtil.getMethodReturnType(statement), statement.getReturnValue(),
                    InspectionsBundle.message("inspection.null.value.for.optional.context.return"));
        }

        @Override
        public void visitVariable(PsiVariable variable) {
            checkNulls(variable.getType(), variable.getInitializer(),
                    InspectionsBundle.message("inspection.null.value.for.optional.context.declaration"));
        }

        private void checkNulls(PsiType type, PsiExpression expression, String declaration) {
            if (expression != null && TypeUtils.isOptional(type)) {
                ExpressionUtils.nonStructuralChildren(expression).filter(ExpressionUtils::isNullLiteral)
                        .forEach(nullLiteral -> register(nullLiteral, (PsiClassType)type, declaration));
            }
        }

        private void register(PsiExpression expression, PsiClassType type, String contextName) {
            holder.registerProblem(expression, DESCRIPTION_TEMPLATE);
        }
    }
}
