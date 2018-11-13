package com.aurea.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.lang.FileASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.uast.java.JavaUClass;

public class SearchAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        FileASTNode astNode = file.getNode();

//astNode.findChildByType(Javatypes

//        PsiClass psic = new JavaUClass();

        if(true) {
            System.out.println("is true");
        } else {
            System.out.println("is false");
        }
//TODO asdf
        Language lang = e.getData(CommonDataKeys.PSI_FILE).getLanguage();
        String languageTag = "+[" + lang.getDisplayName().toLowerCase() + "]";

        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();

        String query = selectedText.replace(' ', '+') + languageTag;
        BrowserUtil.browse("https://stackoverflow.com/search?q=" + query);
    }

    private void help() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) {
                for (int k = 0; k < 1000; k++) {
                    for (int l = 0; l < 100; l++) {
                        for (int m = 0; m < 10; m++) {
                            System.out.println(
                                    "x"
                            );
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(new StringBuilder().append("a").append(i).toString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(new StringBuilder().append("a").append(i).toString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(new StringBuilder().append("a").append(i).toString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(new StringBuilder().append("a").append(i).toString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(new StringBuilder().append("a").append(i).toString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(new StringBuilder().append("a").append(i).toString());
        }
    }
// TODO dddd
    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
    }
}
