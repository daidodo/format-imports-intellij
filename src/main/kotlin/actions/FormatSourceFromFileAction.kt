package actions

import Bundle
import com.intellij.lang.javascript.service.JSLanguageServiceUtil
import com.intellij.lang.javascript.service.protocol.JSLanguageServiceAnswer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.ex.util.EditorScrollingPositionKeeper
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import services.FormatImportsService

class FormatSourceFromFileAction : AnAction() {

    companion object {
        val LOG = Logger.getInstance(FormatSourceFromFileAction::class.java)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    data class FilePathAndSource(val filePath: String, val source: String)

    private fun prepare(project: Project?, psiFile: PsiFile?): FilePathAndSource? {
        if (project == null || psiFile == null) return null
        val file = psiFile.virtualFile ?: return null
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return null
        val source =
            JSLanguageServiceUtil.convertLineSeparatorsToFileOriginal(project, document.charsSequence, file).toString()
        return FilePathAndSource(file.path, source)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val service = ServiceManager.getService(project, FormatImportsService::class.java)
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return
        val response = ProgressManager.getInstance().runProcessWithProgressSynchronously(
            ThrowableComputable<JSLanguageServiceAnswer?, RuntimeException> {
                val (filePath, source) = ReadAction.compute<FilePathAndSource, RuntimeException> {
                    prepare(project, psiFile)
                }
                JSLanguageServiceUtil.awaitFuture(service.formatSourceFromFile(source, filePath))
            },
            Bundle.message("progressTitle"),
            true,
            project
        ) ?: return
        LOG.warn("response: $response")
        val error = response.element["error"].asString
        if (error != null && error.isNotEmpty()) {
            // TODO: Error handler
            return
        }
        val result = response.element["result"].asString
        EditorScrollingPositionKeeper.perform(editor, true) {
            WriteCommandAction.runWriteCommandAction(project) { document.setText(result) }
        }
    }
}
