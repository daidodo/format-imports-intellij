package com.github.daidodo.formatimportsintellij.services

import com.github.daidodo.formatimportsintellij.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
