package com.aurea.codeInspection;

import com.aurea.plugin.TrilogyBundle;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiEnumConstantInitializer;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypeParameter;
import com.siyeh.HardcodedMethodConstants;
import com.siyeh.ig.psiutils.SerializationUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MissingSerialVersionInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MissingSerialVersionVisitor(holder);
    }

    static class MissingSerialVersionVisitor extends JavaElementVisitor {

        @NonNls
        private final String DESCRIPTION_TEMPLATE = TrilogyBundle.message("inspection.trilogy.missing.serial.version.problem.description");
        private final ProblemsHolder holder;

        MissingSerialVersionVisitor(final ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitClass(@NotNull PsiClass aClass) {
            if (aClass.isInterface() || aClass.isAnnotationType() || aClass.isEnum()) {
                return;
            }
            if (aClass instanceof PsiTypeParameter || aClass instanceof PsiEnumConstantInitializer) {
                return;
            }
            final PsiField serialVersionUIDField = aClass.findFieldByName(HardcodedMethodConstants.SERIAL_VERSION_UID, false);
            if (serialVersionUIDField != null) {
                return;
            }
            if (!SerializationUtils.isSerializable(aClass)) {
                return;
            }
            if (SerializationUtils.hasWriteReplace(aClass)) {
                return;
            }
            holder.registerProblem(aClass.getNameIdentifier(), DESCRIPTION_TEMPLATE);
        }
    }
}
