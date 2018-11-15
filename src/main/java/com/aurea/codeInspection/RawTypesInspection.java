package com.aurea.codeInspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.TypeConversionUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RawTypesInspection extends AbstractBaseJavaLocalInspectionTool {

    @NonNls
    private static final String DESCRIPTION_TEMPLATE ="Change type of {0} to {1}";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new RawTypesVisitor(holder);
    }

    @Nullable
    private static PsiType getSuggestedType(@NotNull PsiVariable variable) {
        final PsiExpression initializer = variable.getInitializer();
        if (initializer == null) {
            return null;
        }
        final PsiType variableType = variable.getType();
        final PsiType initializerType = initializer.getType();
        if (!(variableType instanceof PsiClassType)) {
            return null;
        }
        final PsiClassType variableClassType = (PsiClassType) variableType;
        if (!variableClassType.isRaw()) {
            return null;
        }
        if (!(initializerType instanceof PsiClassType)) {
            return null;
        }
        final PsiClassType initializerClassType = (PsiClassType) initializerType;
        if (initializerClassType.isRaw()) {
            return null;
        }
        final PsiClassType.ClassResolveResult variableResolveResult = variableClassType.resolveGenerics();
        final PsiClassType.ClassResolveResult initializerResolveResult = initializerClassType.resolveGenerics();
        if (initializerResolveResult.getElement() == null) {
            return null;
        }
        PsiClass variableResolved = variableResolveResult.getElement();
        if (variableResolved == null) {
            return null;
        }
        PsiSubstitutor targetSubstitutor = TypeConversionUtil.getClassSubstitutor(variableResolved, initializerResolveResult.getElement(), initializerResolveResult.getSubstitutor());
        if (targetSubstitutor == null) {
            return null;
        }
        PsiType type = JavaPsiFacade.getElementFactory(variable.getProject()).createType(variableResolved, targetSubstitutor);
        if (variableType.equals(type)) {
            return null;
        }
        return type;
    }

    static class RawTypesVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;
        RawTypesVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitVariable(PsiVariable variable) {
            super.visitVariable(variable);
            final PsiTypeElement variableTypeElement = variable.getTypeElement();
            if (variableTypeElement != null) {
                final PsiType type = getSuggestedType(variable);
                if (type != null) {
                    holder.registerProblem(variableTypeElement, DESCRIPTION_TEMPLATE);
                }
            }
        }
    }
}
