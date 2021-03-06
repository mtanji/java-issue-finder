package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import com.siyeh.ig.psiutils.LibraryUtil;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VectorInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new VectorVisitor(holder);
    }

    static class VectorVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.replace.vector.problem.description");
        private final ProblemsHolder holder;

        VectorVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitVariable(@NotNull PsiVariable variable) {
            super.visitVariable(variable);
            final PsiType type = variable.getType();
            if (!isVectorType(type)) {
                return;
            }
            if (LibraryUtil.isOverrideOfLibraryMethodParameter(variable)) {
                return;
            }
            final PsiTypeElement typeElement = variable.getTypeElement();
            if (typeElement == null) {
                return;
            }
            holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE);
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiType returnType = method.getReturnType();
            if (!isVectorType(returnType)) {
                return;
            }
            if (LibraryUtil.isOverrideOfLibraryMethod(method)) {
                return;
            }
            final PsiTypeElement typeElement = method.getReturnTypeElement();
            if (typeElement == null) {
                return;
            }
            holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE);
        }

        @Override
        public void visitNewExpression(
                @NotNull PsiNewExpression newExpression) {
            super.visitNewExpression(newExpression);
            final PsiType type = newExpression.getType();
            if (!isVectorType(type)) {
                return;
            }
            holder.registerProblem(newExpression, DESCRIPTION_TEMPLATE);
        }

        private boolean isVectorType(@Nullable PsiType type) {
            if (type == null) {
                return false;
            }
            final PsiType deepComponentType = type.getDeepComponentType();
            final String className = TypeUtils.resolvedClassName(deepComponentType);
            return "java.util.Vector".equals(className);
        }
    }
}
