package actions

import com.intellij.lang.ImportOptimizer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import config.Config

/**
 * Unused.
 */
class OnOptimizeImportsAction : ImportOptimizer {

    companion object {
        private val LOG = Logger.getInstance(this::class.java)

        fun isApplicable(psiFile: PsiFile): Boolean {
            val project = psiFile.project
            if (!Config.instance(project).state.formatOnOptimizeImports) return false
            return ActionCommon.isSelectedSupported(psiFile)
        }
    }

    override fun supports(psiFile: PsiFile): Boolean {
        LOG.info("psiFile: $psiFile")
        return isApplicable(psiFile)
    }

    override fun processFile(psiFile: PsiFile) = Runnable {
        LOG.info("psiFile: $psiFile")
        val project = psiFile.project
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
        if (document != null) {
            val result = ActionCommon.format(document, project, false)
            if (result != null) document.setText(result)
        }
    }
}
