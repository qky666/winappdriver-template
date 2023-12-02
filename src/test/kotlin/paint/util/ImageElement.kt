package paint.util

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.WebDriverRunner
import com.github.qky666.selenidepom.config.SPConfig
import io.appium.java_client.AppiumBy
import org.apache.logging.log4j.kotlin.Logging
import org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.TimeoutException
import java.time.Duration

@Suppress("unused")
class ImageElement : Logging {

    companion object {
        fun findElementByImageFile(
            image: String,
            index: Int = 0,
            timeout: Duration = SPConfig.timeout()
        ): SelenideElement? {
            val driver = WebDriverRunner.getWebDriver()
            val by = AppiumBy.image(Resources.getResourceAsBase64Image(image))
            val wait = WebDriverWait(driver, timeout)
            try {
                wait.until(numberOfElementsToBeMoreThan(by, index))
            } catch (_: TimeoutException) {
            }
            val elements = driver.findElements(by)
            return if (elements.size > index) Selenide.element(elements[index]) else null
        }
    }
}
