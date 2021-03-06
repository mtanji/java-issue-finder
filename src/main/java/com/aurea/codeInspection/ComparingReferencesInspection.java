package com.aurea.codeInspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPrefixExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.IncorrectOperationException;
import java.awt.FlowLayout;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ComparingReferencesInspection extends BaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance("#com.intellij.codeInspection.ComparingReferencesInspection");
    @NonNls
    private static final String DESCRIPTION_TEMPLATE =
            InspectionsBundle.message("inspection.comparing.references.problem.descriptor");
    private final LocalQuickFix myQuickFix = new MyQuickFix();
    @SuppressWarnings({"WeakerAccess"})
    @NonNls
    public String CHECKED_CLASSES = "java.lang.String;java.util.Date";

    private static boolean isNullLiteral(PsiExpression expr) {
        return expr instanceof PsiLiteralExpression && "null".equals(expr.getText());
    }

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
        if (!(type instanceof PsiClassType)) {
            return false;
        }

        StringTokenizer tokenizer = new StringTokenizer(CHECKED_CLASSES, ";");
        while (tokenizer.hasMoreTokens()) {
            String className = tokenizer.nextToken();
            if (type.equalsToText(className)) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

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
                    if (rOperand == null || isNullLiteral(lOperand) || isNullLiteral(rOperand)) {
                        return;
                    }

                    PsiType lType = lOperand.getType();
                    PsiType rType = rOperand.getType();

                    if (isCheckedType(lType) || isCheckedType(rType)) {
                        holder.registerProblem(expression,
                                DESCRIPTION_TEMPLATE, myQuickFix);
                    }
                }
            }
        };
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
                if (rExpr == null) {
                    return;
                }

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
}
