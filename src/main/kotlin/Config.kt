import java.io.File

class Config {

    companion object {
        private val SUPPORTED_EXTENSIONS = Bundle.message("supportedExtensions").split(",")

        fun supports(filePath: String): Boolean {
            return SUPPORTED_EXTENSIONS.contains(File(filePath).extension)
        }
    }


}
