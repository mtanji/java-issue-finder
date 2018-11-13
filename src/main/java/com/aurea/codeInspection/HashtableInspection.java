package com.aurea.codeInspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.SuppressIntentionAction;
import com.intellij.codeInspection.SuppressManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiPrefixExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.ig.psiutils.LibraryUtil;
import com.siyeh.ig.psiutils.TypeUtils;
import java.awt.FlowLayout;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HashtableInspection extends AbstractBaseJavaLocalInspectionTool {
// code from BaseJavaLocalInspectionTool
//    @NotNull
//    @Override
//    public SuppressIntentionAction[] getSuppressActions(PsiElement element) {
//        String shortName = getShortName();
//        HighlightDisplayKey key = HighlightDisplayKey.find(shortName);
//        if (key == null) {
//            throw new AssertionError("HighlightDisplayKey.find(" + shortName + ") is null. Inspection: " + getClass());
//        }
//        return SuppressManager.getInstance().createSuppressActions(key);
//    }

    private static final Logger LOG = Logger.getInstance("#com.intellij.codeInspection.ComparingReferencesInspection");

    private final LocalQuickFix myQuickFix = new MyQuickFix();

    @SuppressWarnings({"WeakerAccess"})
    @NonNls
    public String CHECKED_CLASSES = "java.lang.String;java.util.Date";
    @NonNls
    private static final String DESCRIPTION_TEMPLATE =
            InspectionsBundle.message("inspection.comparing.references.problem.descriptor");

    @NotNull
    public String getDisplayName() {

        return "'==' or '!=' instead of 'equals()'";
    }

    @NotNull
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getShortName() {
        return "ComparingReferences";
    }

    private boolean isCheckedType(PsiType type) {
        if (!(type instanceof PsiClassType)) return false;

        StringTokenizer tokenizer = new StringTokenizer(CHECKED_CLASSES, ";");
        while (tokenizer.hasMoreTokens()) {
            String className = tokenizer.nextToken();
            if (type.equalsToText(className)) return true;
        }

        return false;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {

        HashtableVisitor htv = new HashtableVisitor(holder);
        return htv;

//    JavaElementVisitor jev = new ComparingReferencesVisitor(holder);
//    return jev;

//    return new JavaElementVisitor() {
//
//      @Override
//      public void visitReferenceExpression(PsiReferenceExpression psiReferenceExpression) {
//      }
//
//
//      @Override
//      public void visitBinaryExpression(PsiBinaryExpression expression) {
//        super.visitBinaryExpression(expression);
//        IElementType opSign = expression.getOperationTokenType();
//        if (opSign == JavaTokenType.EQEQ || opSign == JavaTokenType.NE) {
//          PsiExpression lOperand = expression.getLOperand();
//          PsiExpression rOperand = expression.getROperand();
//          if (rOperand == null || isNullLiteral(lOperand) || isNullLiteral(rOperand)) return;
//
//          PsiType lType = lOperand.getType();
//          PsiType rType = rOperand.getType();
//
//          if (isCheckedType(lType) || isCheckedType(rType)) {
//            holder.registerProblem(expression,
//                                   DESCRIPTION_TEMPLATE, myQuickFix);
//          }
//        }
//      }
//    };
    }

    private static boolean isNullLiteral(PsiExpression expr) {
        return expr instanceof PsiLiteralExpression && "null".equals(expr.getText());
    }

    private static class MyQuickFix implements LocalQuickFix {
        @NotNull
        public String getName() {
            // The test (see the TestThisPlugin class) uses this string to identify the quick fix action.
            return InspectionsBundle.message("inspection.comparing.references.use.quickfix");
        }


        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                PsiBinaryExpression binaryExpression = (PsiBinaryExpression) descriptor.getPsiElement();
                IElementType opSign = binaryExpression.getOperationTokenType();
                PsiExpression lExpr = binaryExpression.getLOperand();
                PsiExpression rExpr = binaryExpression.getROperand();
                if (rExpr == null)
                    return;

                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiMethodCallExpression equalsCall =
                        (PsiMethodCallExpression) factory.createExpressionFromText("a.equals(b)", null);

                equalsCall.getMethodExpression().getQualifierExpression().replace(lExpr);
                equalsCall.getArgumentList().getExpressions()[0].replace(rExpr);

                PsiExpression result = (PsiExpression) binaryExpression.replace(equalsCall);

                if (opSign == JavaTokenType.NE) {
                    PsiPrefixExpression negation = (PsiPrefixExpression) factory.createExpressionFromText("!a", null);
                    negation.getOperand().replace(result);
                    result.replace(negation);
                }
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }
    }

    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JTextField checkedClasses = new JTextField(CHECKED_CLASSES);
        checkedClasses.getDocument().addDocumentListener(new DocumentAdapter() {
            public void textChanged(DocumentEvent event) {
                CHECKED_CLASSES = checkedClasses.getText();
            }
        });

        panel.add(checkedClasses);
        return panel;
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    //==============================================================
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
//      if (ignoreRequiredObsoleteCollectionTypes && checkReferences(variable)) {
//        return;
//      }
            holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE);
//      holder.registerProblem(expression, DESCRIPTION_TEMPLATE, myQuickFix);
//      registerError(typeElement);
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
//      if (ignoreRequiredObsoleteCollectionTypes && checkReferences(method)) {
//        return;
//      }
            holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE);
//      registerError(typeElement);
        }

        @Override
        public void visitNewExpression(
                @NotNull PsiNewExpression newExpression) {
            super.visitNewExpression(newExpression);
            final PsiType type = newExpression.getType();
            if (!isHashtableType(type)) {
                return;
            }
//      if (ignoreRequiredObsoleteCollectionTypes && isRequiredObsoleteCollectionElement(newExpression)) {
//        return;
//      }
            holder.registerProblem(newExpression, DESCRIPTION_TEMPLATE);
//      registerNewExpressionError(newExpression);
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
    //==============================================================
    class ComparingReferencesVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;
        ComparingReferencesVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitReferenceExpression(PsiReferenceExpression psiReferenceExpression) {
        }


        @Override
        public void visitBinaryExpression(PsiBinaryExpression expression) {
            super.visitBinaryExpression(expression);
            IElementType opSign = expression.getOperationTokenType();
            if (opSign == JavaTokenType.EQEQ || opSign == JavaTokenType.NE) {
                PsiExpression lOperand = expression.getLOperand();
                PsiExpression rOperand = expression.getROperand();
                if (rOperand == null || isNullLiteral(lOperand) || isNullLiteral(rOperand)) return;

                PsiType lType = lOperand.getType();
                PsiType rType = rOperand.getType();

                if (isCheckedType(lType) || isCheckedType(rType)) {
                    holder.registerProblem(expression,
                            DESCRIPTION_TEMPLATE, myQuickFix);
                }
            }
        }
    }
}
