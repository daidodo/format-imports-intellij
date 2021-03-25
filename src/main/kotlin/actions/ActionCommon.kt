package actions

import Bundle
import Config
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.lang.javascript.service.JSLanguageServiceUtil
import com.intellij.lang.javascript.service.protocol.JSLanguageServiceAnswer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import services.JsService

sealed class ActionCommon {

    companion object {
        private val LOG = Logger.getInstance(this::class.java)

        fun isSupported(document: Document, project: Project?): Boolean {
            if (project == null || !document.isWritable) return false
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return false
            val file = psiFile.virtualFile
            return Config.supports(file.path)
        }

        fun isSelectedSupported(psiFile: PsiFile): Boolean {
            val project = psiFile.project
            val file = psiFile.virtualFile
            val editor = FileEditorManager.getInstance(project).getSelectedEditor(file)
            if (editor is TextEditor) {
                val template = TemplateManager.getInstance(project).getActiveTemplate(editor.editor)
                if (template != null) return false
            }
            val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return false
            return isSupported(document, project)
        }

        private data class PathAndSource(val filePath: String, val source: String)

        fun format(document: Document, project: Project?, withProgress: Boolean): String? {
            if (project == null) return null
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return null
            val service = ServiceManager.getService(project, JsService::class.java)
            val response = run(withProgress, project) {
                val edt = ApplicationManager.getApplication().isDispatchThread
                val (filePath, source) = ReadAction.compute<PathAndSource, RuntimeException> {
                    pathAndSource(project, psiFile)
                }
                if (!Config.supports(filePath)) null
                else JSLanguageServiceUtil.awaitFuture(
                    service.formatSourceFromFile(source, filePath),
                    2000,
                    JSLanguageServiceUtil.QUOTA_MILLS,
                    null,
                    true,
                    null,
                    edt
                )
            } ?: return null
            LOG.info("response: $response")
            val extract = { key: String -> if (response.element.has(key)) response.element[key].asString else null }
            val error = extract("error")
            if (error != null && error.isNotEmpty()) {
                // TODO: Error handler
                return null
            }
            return extract("result")
        }

        private fun run(
            withProgress: Boolean,
            project: Project,
            compute: () -> JSLanguageServiceAnswer?
        ): JSLanguageServiceAnswer? {
            if (!withProgress) return compute()
            return ProgressManager.getInstance().runProcessWithProgressSynchronously(
                ThrowableComputable<JSLanguageServiceAnswer?, RuntimeException> {
                    compute()
                },
                Bundle.message("progressTitle"),
                true,
                project
            )
        }

        private fun pathAndSource(project: Project?, psiFile: PsiFile?): PathAndSource? {
            if (project == null || psiFile == null) return null
            val file = psiFile.virtualFile ?: return null
            val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return null
            val source =
                JSLanguageServiceUtil.convertLineSeparatorsToFileOriginal(project, document.charsSequence, file)
                    .toString()
            return PathAndSource(file.path, source)
        }
    }
}
