package actions

import Bundle
import Config
import com.intellij.lang.javascript.service.JSLanguageServiceUtil
import com.intellij.lang.javascript.service.protocol.JSLanguageServiceAnswer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
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
        e.presentation.isEnabledAndVisible = visible(e)
    }

    data class EditorAndDocAndFile(val editor: Editor, val document: Document, val psiFile: PsiFile?)

    data class PathAndSource(val filePath: String, val source: String)

    private fun visible(e: AnActionEvent): Boolean {
        val project = e.project ?: return false
        val (_, document, psiFile) = editorAndDocAndFile(e) ?: return false
        if (!document.isWritable) return false
        val (filePath) = filePathAndSource(project, psiFile) ?: return false
        return Config.supports(filePath)
    }

    private fun editorAndDocAndFile(e: AnActionEvent): EditorAndDocAndFile? {
        val project = e.project ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
        val document = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
        return EditorAndDocAndFile(editor, document, psiFile)
    }

    private fun filePathAndSource(project: Project?, psiFile: PsiFile?): PathAndSource? {
        if (project == null || psiFile == null) return null
        val file = psiFile.virtualFile ?: return null
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return null
        val source =
            JSLanguageServiceUtil.convertLineSeparatorsToFileOriginal(project, document.charsSequence, file).toString()
        return PathAndSource(file.path, source)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val (editor, document, psiFile) = editorAndDocAndFile(e) ?: return
        val service = ServiceManager.getService(project, FormatImportsService::class.java)
        val response = ProgressManager.getInstance().runProcessWithProgressSynchronously(
            ThrowableComputable<JSLanguageServiceAnswer?, RuntimeException> {
                val (filePath, source) = ReadAction.compute<PathAndSource, RuntimeException> {
                    filePathAndSource(project, psiFile)
                }
                if (Config.supports(filePath))
                    JSLanguageServiceUtil.awaitFuture(service.formatSourceFromFile(source, filePath))
                else
                    null
            },
            Bundle.message("progressTitle"),
            true,
            project
        ) ?: return
        LOG.debug("response: $response")
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
