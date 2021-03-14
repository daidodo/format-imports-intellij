package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.ex.util.EditorScrollingPositionKeeper

class FormatOnCommandAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && ActionCommon.isSupported(editor.document, e.project)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val result = ActionCommon.format(document, project) ?: return
        EditorScrollingPositionKeeper.perform(editor, true) {
            WriteCommandAction.runWriteCommandAction(project) { document.setText(result) }
        }
    }
}
