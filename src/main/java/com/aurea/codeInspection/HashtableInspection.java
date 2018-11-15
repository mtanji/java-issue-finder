package com.aurea.codeInspection;

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

public class HashtableInspection extends AbstractBaseJavaLocalInspectionTool {

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Using #ref #loc";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new HashtableVisitor(holder);
    }

    class HashtableVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;

        HashtableVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitVariable(@NotNull PsiVariable variable) {
            super.visitVariable(variable);
            final PsiType type = variable.getType();
            if (!isHashtableType(type)) {
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
            if (!isHashtableType(returnType)) {
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
            if (!isHashtableType(type)) {
                return;
            }
            holder.registerProblem(newExpression, DESCRIPTION_TEMPLATE);
        }

        private boolean isHashtableType(@Nullable PsiType type) {
            if (type == null) {
                return false;
            }
            final PsiType deepComponentType = type.getDeepComponentType();
            final String className = TypeUtils.resolvedClassName(deepComponentType);
            return "java.util.Hashtable".equals(className);
        }
    }
}
