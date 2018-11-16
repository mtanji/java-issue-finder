package com.aurea.codeInspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiVariable;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HashtableInspectionTest {

    @Test
    public void givenHashtableInexistWhenVisitMethodThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        PsiMethod method = mock(PsiMethod.class);
        HashtableInspection.HashtableVisitor visitor = new HashtableInspection.HashtableVisitor(holder);

        visitor.visitMethod(method);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenHashtableInexistWhenVisitVariableThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        PsiVariable variable = mock(PsiVariable.class);
        HashtableInspection.HashtableVisitor visitor = new HashtableInspection.HashtableVisitor(holder);

        visitor.visitVariable(variable);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

    @Test
    public void givenHashtableInexistWhenVisitNewExpressionThenProblemsHolderIsNotCalled() {
        ProblemsHolder holder = mock(ProblemsHolder.class);
        PsiNewExpression newExpression = mock(PsiNewExpression.class);
        HashtableInspection.HashtableVisitor visitor = new HashtableInspection.HashtableVisitor(holder);

        visitor.visitNewExpression(newExpression);

        verify(holder, times(0)).registerProblem(any(PsiElement.class), anyString());
    }

}
//public class HashtableInspectionTest extends LightCodeInsightFixtureTestCase {
//    protected CodeInsightTestFixture myFixture;
//    // Specify path to your test data directory
//    // e.g.  final String dataPath = "c:\\users\\john.doe\\idea\\community\\samples\\ComparingReferences/testData";
////  final String dataPath = "c:\\users\\John.Doe\\idea\\community\\samples\\comparingReferences/testData";
////    final String dataPath = "C:\\bootcamp-w4\\intellij-sdk-docs\\code_samples\\comparing_references_inspection\\testData";
//    final String dataPath = "C:\\bootcamp-w4\\java-issue-finder\\src\\test\\testData";
//
////    public void test() throws Throwable {
////        doTest("before");
////    }
//
//    public void testHashtableInspection() throws Throwable {
//        myFixture.configureByFile("HashtableTest.java");
//        myFixture.enableInspections(HashtableInspection.class);
//        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
//        Assert.assertTrue(!highlightInfos.isEmpty());
//    }
//
//    public void testHashtableInspectionNothingIsFound() throws Throwable {
//        myFixture.configureByFile("Nothing.java");
//        myFixture.enableInspections(HashtableInspection.class);
//        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
//        Assert.assertTrue(!highlightInfos.isEmpty());
//    }
//
////    protected void doTest(String testName) throws Throwable {
////        myFixture.configureByFile(testName + ".java");
////        myFixture.enableInspections(HashtableInspection.class);
////        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
////        Assert.assertTrue(!highlightInfos.isEmpty());
////    }
//
//    public void setUp() throws Exception {
//        final IdeaTestFixtureFactory fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();
//        final TestFixtureBuilder<IdeaProjectTestFixture> testFixtureBuilder =
//                fixtureFactory.createFixtureBuilder(getName());
//        myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(testFixtureBuilder.getFixture());
//        myFixture.setTestDataPath(dataPath);
//        final JavaModuleFixtureBuilder builder = testFixtureBuilder.addModule(JavaModuleFixtureBuilder.class);
//        builder.addContentRoot(myFixture.getTempDirPath()).addSourceRoot("");
//        builder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
//        myFixture.setUp();
//    }
//
//    public void tearDown() throws Exception {
//        myFixture.tearDown();
//        myFixture = null;
//    }
//
//}
