package config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(name = "FormatImportsConfiguration", storages = [Storage("format-imports-intellij.xml")])
class Config(private val project: Project) :
    PersistentStateComponent<Config.State> {

    companion object {
        fun instance(project: Project): Config {
            return ServiceManager.getService(project, Config::class.java)
        }
    }

    data class State(
        var formatOnReformat: Boolean = false,
        var formatOnSave: Boolean = false,
        var formatOnOptimizeImports: Boolean = false
    )

    private var state = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }
}
