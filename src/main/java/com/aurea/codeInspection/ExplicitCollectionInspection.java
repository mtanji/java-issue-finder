package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import com.siyeh.ig.psiutils.CollectionUtils;
import com.siyeh.ig.psiutils.LibraryUtil;
import com.siyeh.ig.psiutils.WeakestTypeFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ExplicitCollectionInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new ExplicitCollectionVisitor(holder);
    }

    private class ExplicitCollectionVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.explicit.collection.types.problem.description");
        private final ProblemsHolder holder;

        ExplicitCollectionVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitVariable(@NotNull PsiVariable variable) {
            super.visitVariable(variable);
            final PsiType type = variable.getType();
            if (!CollectionUtils.isConcreteCollectionClass(type) || LibraryUtil.isOverrideOfLibraryMethodParameter(variable)) {
                return;
            }

            checkToWeaken(type, variable.getTypeElement(), variable);
        }

        @Override
        public void visitMethod(@NotNull PsiMethod method) {
            super.visitMethod(method);
            final PsiType type = method.getReturnType();
            if (!CollectionUtils.isConcreteCollectionClass(type) || LibraryUtil.isOverrideOfLibraryMethod(method)) {
                return;
            }

            checkToWeaken(type, method.getReturnTypeElement(), method);
        }

        private void checkToWeaken(PsiType type, PsiTypeElement typeElement, PsiElement variable) {
            if (typeElement == null) {
                return;
            }
            final PsiJavaCodeReferenceElement reference = typeElement.getInnermostComponentReferenceElement();
            if (reference == null) {
                return;
            }
            final PsiElement nameElement = reference.getReferenceNameElement();
            if (nameElement == null) {
                return;
            }
            final Collection<PsiClass> weaklings = WeakestTypeFinder.calculateWeakestClassesNecessary(variable, false, true);
            if (weaklings.isEmpty()) {
                return;
            }
            final PsiClassType javaLangObject = PsiType.getJavaLangObject(nameElement.getManager(), nameElement.getResolveScope());
            final List<PsiClass> weaklingList = new ArrayList<>(weaklings);
            final PsiClass objectClass = javaLangObject.resolve();
            weaklingList.remove(objectClass);
            String qualifiedName = weaklingList.isEmpty() ? CollectionUtils.getInterfaceForClass(type.getCanonicalText())
                    : weaklingList.get(0).getQualifiedName();
            if (qualifiedName != null) {
                holder.registerProblem(nameElement, DESCRIPTION_TEMPLATE);
            }
        }
    }
}