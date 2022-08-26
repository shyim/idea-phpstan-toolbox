package de.shyim.ideaphpstan.intention

import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import de.shyim.ideaphpstan.index.dict.PhpstanIgnores

class RemoveFileFromBaseLineIntention(val ignores: PhpstanIgnores) : LocalQuickFix {
    override fun getFamilyName() = "Remove file from baseline"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = LocalFileSystem.getInstance().findFileByPath(ignores.phpStanFile) ?: return

        val editor = FileEditorManager.getInstance(project).openTextEditor(OpenFileDescriptor(project, file), false) ?: return

        CommandProcessor.getInstance().executeCommand(project, {
            editor.document.replaceString(
                editor.document.getLineStartOffset(ignores.start),
                editor.document.getLineEndOffset(ignores.end),
                ""
            )
        }, "Remove file from baseline", null)
    }
}