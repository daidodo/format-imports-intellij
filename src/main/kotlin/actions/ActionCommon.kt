package actions

import Bundle
import Config
import com.intellij.lang.javascript.service.JSLanguageServiceUtil
import com.intellij.lang.javascript.service.protocol.JSLanguageServiceAnswer
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import services.FormatImportsService

sealed class ActionCommon {

    companion object {
        private val LOG = Logger.getInstance(this::class.java)

        fun isSupported(document: Document, project: Project?): Boolean {
            if (project == null || !document.isWritable) return false
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return false
            val file = psiFile.virtualFile
            return Config.supports(file.path)
        }

        private data class PathAndSource(val filePath: String, val source: String)

        fun format(document: Document, project: Project?): String? {
            if (project == null) return null
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return null
            val service = ServiceManager.getService(project, FormatImportsService::class.java)
            val response = ProgressManager.getInstance().runProcessWithProgressSynchronously(
                ThrowableComputable<JSLanguageServiceAnswer?, RuntimeException> {
                    val (filePath, source) = ReadAction.compute<PathAndSource, RuntimeException> {
                        pathAndSource(project, psiFile)
                    }
                    if (!Config.supports(filePath)) null
                    else JSLanguageServiceUtil.awaitFuture(service.formatSourceFromFile(source, filePath))
                },
                Bundle.message("progressTitle"),
                true,
                project
            ) ?: return null
            LOG.info("response: $response")
            val extract = { key: String -> if (response.element.has(key)) response.element[key].asString else null }
            val error = extract("error")
            if (error != null && error.isNotEmpty()) {
                // TODO: Error handler
                return null
            }
            return extract("result")
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
