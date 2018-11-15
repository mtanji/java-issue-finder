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

public class VectorInspection extends AbstractBaseJavaLocalInspectionTool {

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Replace <code>#ref</code> #loc";
//    private static final int MAX_OCCURRENCES = 20;
//
//    @SuppressWarnings({"PublicField"})
//    public boolean ignoreRequiredObsoleteCollectionTypes = true;
//
//    @Override
//    @NotNull
//    public String getID() {
//        return "UseOfObsoleteCollectionType";
//    }
//
//    @Override
//    @NotNull
//    public String getDisplayName() {
//        return InspectionGadgetsBundle.message(
//                "use.obsolete.collection.type.display.name");
//    }
//
//    @Override
//    @NotNull
//    public String buildErrorString(Object... infos) {
//        return InspectionGadgetsBundle.message(
//                "use.obsolete.collection.type.problem.descriptor");
//    }
//
//    @Override
//    @Nullable
//    public JComponent createOptionsPanel() {
//        return new SingleCheckboxOptionsPanel(InspectionGadgetsBundle.message(
//                "use.obsolete.collection.type.ignore.library.arguments.option"
//        ), this, "ignoreRequiredObsoleteCollectionTypes");
//    }
//
//    @Override
//    public BaseInspectionVisitor buildVisitor() {
//        return new ObsoleteCollectionInspection.ObsoleteCollectionVisitor();
//    }


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new VectorVisitor(holder);
    }

    private class VectorVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;
        VectorVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitVariable(@NotNull PsiVariable variable) {
            super.visitVariable(variable);
            final PsiType type = variable.getType();
            if (!isObsoleteCollectionType(type)) {
                return;
            }
            if (LibraryUtil.isOverrideOfLibraryMethodParameter(variable)) {
                return;
            }
            final PsiTypeElement typeElement = variable.getTypeElement();
            if (typeElement == null) {
                return;
            }
//            if (ignoreRequiredObsoleteCollectionTypes && checkReferences(variable)) {
//                return;
//            }
            holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE);
//            registerError(typeElement);
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            final PsiType returnType = method.getReturnType();
            if (!isObsoleteCollectionType(returnType)) {
                return;
            }
            if (LibraryUtil.isOverrideOfLibraryMethod(method)) {
                return;
            }
            final PsiTypeElement typeElement = method.getReturnTypeElement();
            if (typeElement == null) {
                return;
            }
//            if (ignoreRequiredObsoleteCollectionTypes && checkReferences(method)) {
//                return;
//            }
            holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE);
//            registerError(typeElement);
        }

        @Override
        public void visitNewExpression(
                @NotNull PsiNewExpression newExpression) {
            super.visitNewExpression(newExpression);
            final PsiType type = newExpression.getType();
            if (!isObsoleteCollectionType(type)) {
                return;
            }
//            if (ignoreRequiredObsoleteCollectionTypes &&
//                    isRequiredObsoleteCollectionElement(newExpression)) {
//                return;
//            }
            holder.registerProblem(newExpression, DESCRIPTION_TEMPLATE);
//            registerNewExpressionError(newExpression);
        }

        private boolean isObsoleteCollectionType(@Nullable PsiType type) {
            if (type == null) {
                return false;
            }
            final PsiType deepComponentType = type.getDeepComponentType();
            final String className = TypeUtils.resolvedClassName(deepComponentType);
            return "java.util.Vector".equals(className);
        }

//        private boolean checkReferences(PsiNamedElement namedElement) {
//            final PsiFile containingFile = namedElement.getContainingFile();
////            if (!isOnTheFly() || isCheapToSearchInFile(namedElement)) {
////                return ReferencesSearch.search(namedElement, GlobalSearchScope.fileScope(containingFile))
////                        .anyMatch(ref -> isRequiredObsoleteCollectionElement(ref.getElement()));
////            }
//            return true;
//        }
//
//        private boolean isRequiredObsoleteCollectionElement(PsiElement element) {
//            final PsiElement parent = element.getParent();
//            if (parent instanceof PsiVariable) {
//                final PsiVariable variable = (PsiVariable)parent;
//                final PsiType variableType = variable.getType();
//                if (isObsoleteCollectionType(variableType)) {
//                    return true;
//                }
//            }
//            else if (parent instanceof PsiReturnStatement) {
//                final PsiType returnType = PsiTypesUtil.getMethodReturnType(parent);
//                if (isObsoleteCollectionType(returnType)) {
//                    return true;
//                }
//            }
//            else if (parent instanceof PsiAssignmentExpression) {
//                final PsiAssignmentExpression assignmentExpression =
//                        (PsiAssignmentExpression)parent;
//                final PsiExpression lhs = assignmentExpression.getLExpression();
//                final PsiType lhsType = lhs.getType();
//                if (isObsoleteCollectionType(lhsType)) {
//                    return true;
//                }
//            }
//            else if (parent instanceof PsiMethodCallExpression) {
//                return isRequiredObsoleteCollectionElement(parent);
//            }
//            if (!(parent instanceof PsiExpressionList)) {
//                return false;
//            }
//            final PsiExpressionList argumentList = (PsiExpressionList)parent;
//            final PsiElement grandParent = parent.getParent();
//            if (!(grandParent instanceof PsiCallExpression)) {
//                return false;
//            }
//            final PsiCallExpression callExpression =
//                    (PsiCallExpression)grandParent;
//            final int index = getIndexOfArgument(argumentList, element);
//            final PsiMethod method = callExpression.resolveMethod();
//            if (method == null) {
//                return false;
//            }
//            final PsiParameterList parameterList =
//                    method.getParameterList();
//            final PsiParameter[] parameters = parameterList.getParameters();
//            if (index >= parameters.length) {
//                final PsiParameter lastParameter =
//                        parameters[parameters.length - 1];
//                if (!lastParameter.isVarArgs()) {
//                    return false;
//                }
//                final PsiType type = lastParameter.getType();
//                if (!(type instanceof PsiEllipsisType)) {
//                    return false;
//                }
//                final PsiEllipsisType ellipsisType = (PsiEllipsisType)type;
//                final PsiType componentType = ellipsisType.getComponentType();
//                return isObsoleteCollectionType(componentType);
//            }
//            final PsiParameter parameter = parameters[index];
//            final PsiType type = parameter.getType();
//            return isObsoleteCollectionType(type);
//        }
//
//        private int getIndexOfArgument(PsiExpressionList argumentList,
//                PsiElement argument) {
//            final PsiExpression[] expressions =
//                    argumentList.getExpressions();
//            int index = -1;
//            for (PsiExpression expression : expressions) {
//                index++;
//                if (expression.equals(argument)) {
//                    break;
//                }
//            }
//            return index;
//        }
//    }
//
//    private static boolean isCheapToSearchInFile(@NotNull PsiNamedElement element) {
//        String name = element.getName();
//        if (name == null) return false;
//        return CachedValuesManager.getCachedValue(element, () -> {
//            PsiFile file = element.getContainingFile();
//            StringSearcher searcher = new StringSearcher(name, true, true);
//            CharSequence contents = file.getViewProvider().getContents();
//            int[] count = new int[1];
//            boolean cheapEnough = searcher.processOccurrences(contents, __->++count[0] <= MAX_OCCURRENCES);
//            return CachedValueProvider.Result.create(cheapEnough, file);
//        });
//    }
    }
}