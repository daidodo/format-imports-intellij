package services

import com.intellij.openapi.project.Project

class ProjectService(project: Project) {

    init {
        println(Bundle.message("projectService", project.name))
    }
}
