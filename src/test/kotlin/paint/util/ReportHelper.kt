package paint.util

import com.codeborne.selenide.Selenide
import io.qameta.allure.Allure.addAttachment
import org.apache.logging.log4j.kotlin.Logging
import org.openqa.selenium.OutputType

object ReportHelper : Logging {
    fun attachScreenshot(name: String = "Page screenshot"): ByteArray? {
        val screenshot = Selenide.screenshot(OutputType.BYTES)
        val stream = screenshot?.inputStream()
        if (stream != null) {
            logger.info { "Attaching screenshot: $name" }
            addAttachment(name, stream)
            stream.close()
        } else {
            logger.info { "No screenshot available: $name" }
            addAttachment(name, "No screenshot available")
        }
        return screenshot
    }
}
