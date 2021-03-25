package config

import Bundle
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import javax.swing.JLabel

class ConfigPanel(private val project: Project) :
    BoundSearchableConfigurable(Bundle.message("name"), "daidodo.format-imports-intellij.settings") {

    override fun createPanel(): DialogPanel {
        val config = Config.Instance(project)
        return panel {
            row {
                val autoFormatWhen = JLabel(Bundle.message("label.autoFormatWhen"))
                autoFormatWhen()
            }
            row("") {
                cell {
                    checkBox(Bundle.message("label.reformatCode"),
                        { config.state.formatOnReformat }, {
                            config.state.formatOnReformat = it
                        })
                    // https://centic9.github.io/IntelliJ-Action-IDs/
                    val shortcut =
                        ActionManager.getInstance().getKeyboardShortcut(IdeActions.ACTION_EDITOR_REFORMAT)
                    shortcut?.let { comment(KeymapUtil.getShortcutText(it)) }
                }
            }
            row("") {
                cell {
                    checkBox(Bundle.message("label.SaveFile"),
                        { config.state.formatOnSave }, {
                            config.state.formatOnSave = it
                        })
                    val shortcut = ActionManager.getInstance().getKeyboardShortcut("SaveAll")
                    shortcut?.let { comment(KeymapUtil.getShortcutText(it)) }
                }
            }
//            row("") {
//                cell {
//                    checkBox(Bundle.message("label.OptimizeImports"),
//                        { config.state.formatOnOptimizeImports }, {
//                            config.state.formatOnOptimizeImports = it
//                        })
//                    val shortcut = ActionManager.getInstance().getKeyboardShortcut("OptimizeImports")
//                    shortcut?.let { comment(KeymapUtil.getShortcutText(it)) }
//                }
//            }
        }
    }
}
