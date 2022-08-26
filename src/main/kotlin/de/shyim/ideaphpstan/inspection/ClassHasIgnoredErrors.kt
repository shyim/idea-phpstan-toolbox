package de.shyim.ideaphpstan.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpClass
import de.shyim.ideaphpstan.index.PhpstanIgnoreIndex
import de.shyim.ideaphpstan.intention.RemoveFileFromBaseLineIntention

class ClassHasIgnoredErrors: LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val file = holder.file

        if (file !is PhpFile) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        val fileName = holder.file.virtualFile.path.replace(holder.project.basePath!! + "/", "")

        // We don't care about vendor files
        if (fileName.startsWith("vendor/")) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        val errors = FileBasedIndex.getInstance().getValues(PhpstanIgnoreIndex.key, fileName, GlobalSearchScope.allScope(holder.project))

        if (errors.isEmpty()) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        val classPattern = PlatformPatterns
            .psiElement(PhpTokenTypes.IDENTIFIER)
            .withParent(
                PlatformPatterns.psiElement(PhpClass::class.java)
            )

        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (classPattern.accepts(element)) {
                    holder.registerProblem(
                        element,
                        "This file has ${errors.first().errors} ignored PhpStan errors in baseline.",
                        ProblemHighlightType.WARNING,
                        RemoveFileFromBaseLineIntention(errors.first())
                    )
                    return
                }

                super.visitElement(element)
            }
        }
    }
}