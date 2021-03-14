package services

import Bundle
import com.intellij.lang.javascript.service.*
import com.intellij.lang.javascript.service.protocol.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.EmptyConsumer
import java.util.concurrent.Future

class FormatImportsService(project: Project) : JSLanguageServiceBase(project) {

    companion object {
        val LOG = Logger.getInstance(FormatImportsService::class.java)
    }

    init {
        println(Bundle.message("projectService", project.name))
    }

    override fun needInitToolWindow() = false

    override fun createLanguageServiceQueue(): JSLanguageServiceQueue? {
        val protocol = object : JSLanguageServiceNodeStdProtocolBase(myProject, EmptyConsumer.getInstance<Any>()) {
            override fun dispose() {}
            override fun createState(): JSLanguageServiceInitialState {
                val state = JSLanguageServiceInitialState()
                state.pluginName = Bundle.message("pluginName")
                val file =
                    JSLanguageServiceUtil.getPluginDirectory(this.javaClass, "languageService/index.js")
                state.pluginPath = LocalFilePath.create(file.absolutePath)
                println("pluginName: ${state.pluginName}, pluginPath: ${state.pluginPath}")
                LOG.warn("pluginName: ${state.pluginName}, pluginPath: ${state.pluginPath}")
                return state
            }
        }
        return JSLanguageServiceQueueImpl(
            myProject, protocol, myProcessConnector, myDefaultReporter,
            JSLanguageServiceDefaultCacheData()
        )
    }

    class Request(val source: String, val filePath: String) : JSLanguageServiceCommandObject() {
        override fun getCommand() = "formatSourceFromFile"
    }

    fun formatSourceFromFile(source: String, filePath: String): Future<JSLanguageServiceAnswer>? {
        val process = process
        if (process == null || !process.isValid)
            return null
        val request = Request(source, filePath)
        println("request: filePath = ${request.filePath}, source = ${request.source}")
        LOG.warn("request: filePath = ${request.filePath}, source = ${request.source}")
        return process.execute(request) { _, response ->
            // TODO: Remove debug code
            if (true) {
                println("response: $response")
                LOG.warn("response: $response")
                val e = response.element
                val error = e["error"].asString
                println("error: $error")
                LOG.warn("error: $error")
                val result = response.element["result"].asString
                if (result != null) {
                    println("result: $result")
                    LOG.warn("result: $result")
                }
            }
            response
        }
//        return sendCommand(request) { _, response ->
//            // TODO: Remove debug code
//            if (true) {
//                println("response: $response")
//                LOG.warn("response: $response")
//                val error = response.element["error"].asString
//                println("error: $error")
//                LOG.warn("error: $error")
//                if (error != null && error.isNotEmpty()) {
//                    // TODO: Error handling
//                }
//                val result = response.element["result"].asString
//                if (result != null) {
//                    println("result: $result")
//                    LOG.warn("result: $result")
//                }
//            }
//            response
//        }
    }
}
