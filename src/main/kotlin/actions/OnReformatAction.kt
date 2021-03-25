package actions

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor
import config.Config

class OnReformatAction : PostFormatProcessor {
    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement {
        return source
    }

    override fun processText(psiFile: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (!isApplicable(psiFile)) return rangeToReformat
        val project = psiFile.project
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return rangeToReformat
        val result = ActionCommon.format(document, project, false) ?: return rangeToReformat
        val oldLength = document.textLength
        document.setText(result)
        rangeToReformat.grown(result.length - oldLength)
        return rangeToReformat
    }

    companion object {
        fun isApplicable(psiFile: PsiFile): Boolean {
            val project = psiFile.project
            if (!Config.instance(project).state.formatOnReformat) return false
            return ActionCommon.isSelectedSupported(psiFile)
        }
    }
}
