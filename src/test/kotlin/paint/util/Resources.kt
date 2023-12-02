package paint.util

import java.util.Base64

class Resources {
    companion object {
        private val classLoader = Thread.currentThread().contextClassLoader

        fun getResourceAsBase64Image(resource: String): String {
            val bytes = classLoader.getResourceAsStream(resource)?.readAllBytes()
            return Base64.getEncoder().encodeToString(bytes)
        }
    }
}
