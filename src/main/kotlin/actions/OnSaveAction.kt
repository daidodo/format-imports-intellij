package actions

import com.intellij.lang.javascript.linter.LinterSaveActionsManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import config.Config
import org.jetbrains.annotations.NotNull

class OnSaveAction : LinterSaveActionsManager.LinterSaveAction() {

    override fun isEnabledForProject(project: @NotNull Project): Boolean {
        return Config.instance(project).state.formatOnSave
    }

    override fun processDocuments(project: @NotNull Project, documents: @NotNull Array<out @NotNull Document>) {
        for (document in documents) {
            if (ActionCommon.isSupported(document, project)) {
                val result = ActionCommon.format(document, project, true) ?: continue
                WriteCommandAction.runWriteCommandAction(project) { document.setText(result) }
            }
        }
    }
}
