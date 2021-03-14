package actions

import com.intellij.lang.javascript.linter.LinterSaveActionsManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class FormatOnSaveAction : LinterSaveActionsManager.LinterSaveAction() {

    override fun isEnabledForProject(project: @NotNull Project): Boolean {
        // TODO: Check config.
        return true
    }

    override fun processDocuments(project: @NotNull Project, documents: @NotNull Array<out @NotNull Document>) {
        for (document in documents) {
            if (ActionCommon.isSupported(document, project)) {
                val result = ActionCommon.format(document, project) ?: continue
                WriteCommandAction.runWriteCommandAction(project) { document.setText(result) }
            }
        }
    }
}
