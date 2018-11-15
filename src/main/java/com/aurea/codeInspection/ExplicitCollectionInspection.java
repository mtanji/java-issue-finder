package com.aurea.codeInspection;

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

    @NonNls
    private static final String DESCRIPTION_TEMPLATE ="Declaration of <code>#ref</code> should probably be weakened to ''{0}'' #loc";
//    /**
//     * @noinspection PublicField
//     */
//    public boolean ignoreLocalVariables = false;
//    /**
//     * @noinspection PublicField
//     */
//    public boolean ignorePrivateMethodsAndFields = false;
//
//    @Override
//    @NotNull
//    public String getID() {
//        return "CollectionDeclaredAsConcreteClass";
//    }
//
//    @Override
//    @NotNull
//    public String getDisplayName() {
//        return InspectionGadgetsBundle.message(
//                "collection.declared.by.class.display.name");
//    }
//
//    @Override
//    @NotNull
//    public String buildErrorString(Object... infos) {
//        final String type = (String)infos[0];
//        return InspectionGadgetsBundle.message(
//                "collection.declared.by.class.problem.descriptor",
//                type);
//    }
//
//    @Override
//    @Nullable
//    public JComponent createOptionsPanel() {
//        final MultipleCheckboxOptionsPanel optionsPanel =
//                new MultipleCheckboxOptionsPanel(this);
//        optionsPanel.addCheckbox(InspectionGadgetsBundle.message(
//                "collection.declared.by.class.ignore.locals.option"),
//                "ignoreLocalVariables");
//        optionsPanel.addCheckbox(InspectionGadgetsBundle.message(
//                "collection.declared.by.class.ignore.private.members.option"),
//                "ignorePrivateMethodsAndFields");
//        return optionsPanel;
//    }
//
//    @Override
//    protected InspectionGadgetsFix buildFix(Object... infos) {
//        return new DeclareCollectionAsInterfaceInspection.DeclareCollectionAsInterfaceFix((String)infos[0]);
//    }
//
//    private static class DeclareCollectionAsInterfaceFix extends InspectionGadgetsFix {
//
//        private final String typeString;
//
//        DeclareCollectionAsInterfaceFix(@NotNull String typeString) {
//            this.typeString = typeString;
//        }
//
//        @Override
//        @NotNull
//        public String getName() {
//            return InspectionGadgetsBundle.message(
//                    "declare.collection.as.interface.quickfix", typeString);
//        }
//
//        @NotNull
//        @Override
//        public String getFamilyName() {
//            return "Weaken type";
//        }
//
//        @Override
//        protected void doFix(Project project, ProblemDescriptor descriptor) {
//            final PsiElement element = descriptor.getPsiElement();
//            final PsiElement parent = element.getParent();
//            if (!(parent instanceof PsiJavaCodeReferenceElement)) {
//                return;
//            }
//            final StringBuilder newElementText = new StringBuilder(typeString);
//            final PsiJavaCodeReferenceElement referenceElement = (PsiJavaCodeReferenceElement)parent;
//            final PsiReferenceParameterList parameterList = referenceElement.getParameterList();
//            if (parameterList != null) {
//                newElementText.append(parameterList.getText());
//            }
//            final PsiElement grandParent = parent.getParent();
//            if (!(grandParent instanceof PsiTypeElement)) {
//                return;
//            }
//            final PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
//            final PsiTypeElement newTypeElement = factory.createTypeElementFromText(newElementText.toString(), element);
//            JavaCodeStyleManager.getInstance(project).shortenClassReferences(grandParent.replace(newTypeElement));
//        }
//    }
//
//    @Override
//    public BaseInspectionVisitor buildVisitor() {
//        return new DeclareCollectionAsInterfaceInspection.DeclareCollectionAsInterfaceVisitor();
//    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new ExplicitCollectionVisitor(holder);
    }

    private class ExplicitCollectionVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;
        ExplicitCollectionVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitVariable(@NotNull PsiVariable variable) {
//            if (isOnTheFly() && DeclarationSearchUtils.isTooExpensiveToSearch(variable, false)) {
//                return;
//            }
//            if (ignoreLocalVariables && variable instanceof PsiLocalVariable) {
//                return;
//            }
//            if (ignorePrivateMethodsAndFields) {
//                if (variable instanceof PsiField) {
//                    if (variable.hasModifierProperty(PsiModifier.PRIVATE)) {
//                        return;
//                    }
//                }
//            }
//            if (variable instanceof PsiParameter) {
//                final PsiParameter parameter = (PsiParameter)variable;
//                final PsiElement scope = parameter.getDeclarationScope();
//                if (scope instanceof PsiMethod) {
//                    if (ignorePrivateMethodsAndFields) {
//                        final PsiMethod method = (PsiMethod)scope;
//                        if (method.hasModifierProperty(PsiModifier.PRIVATE)) {
//                            return;
//                        }
//                    }
//                }
//                else if (ignoreLocalVariables) {
//                    return;
//                }
//            }
            final PsiType type = variable.getType();
            if (!CollectionUtils.isConcreteCollectionClass(type) || LibraryUtil.isOverrideOfLibraryMethodParameter(variable)) {
                return;
            }

            checkToWeaken(type, variable.getTypeElement(), variable);
        }

        @Override
        public void visitMethod(@NotNull PsiMethod method) {
            super.visitMethod(method);
//            if (ignorePrivateMethodsAndFields &&
//                    method.hasModifierProperty(PsiModifier.PRIVATE)) {
//                return;
//            }
//            if (isOnTheFly() && DeclarationSearchUtils.isTooExpensiveToSearch(method, false)) {
//                return;
//            }
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
//                registerError(nameElement, qualifiedName);
            }
        }
    }
}