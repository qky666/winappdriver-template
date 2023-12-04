package apptest.util

import io.appium.java_client.windows.WindowsDriver
import io.appium.java_client.windows.options.WindowsOptions
import org.apache.logging.log4j.kotlin.Logging
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.MalformedURLException
import java.net.URI

@Suppress("unused", "UNUSED_PARAMETER")
class AppiumProvider : Logging {
    companion object : Logging {

        fun createAppDriver(appiumServer: String, aumidOrPath: String, appWorkingDir: String = ""): WindowsDriver {
            val windowsOptions = WindowsOptions().setApp(aumidOrPath)
            if (appWorkingDir.isNotEmpty()) {
                windowsOptions.setAppWorkingDir(appWorkingDir)
            }
            // Experimental is not working finding some elements
            // .setExperimentalWebDriver(true)
            windowsOptions.setCapability("appium:deviceName", "WindowsPC")
            windowsOptions.setCapability("appium:settings[imageMatchThreshold]", "0.8")

            return try {
                val url = URI(appiumServer).toURL()
                WindowsDriver(url, windowsOptions)
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }

        fun createYWinAppDriver(server: String, aumidOrPath: String, appWorkingDir: String): RemoteWebDriver {
            val capabilities = DesiredCapabilities(mapOf("app" to aumidOrPath, "deviceName" to "WindowsPC"))
            return try {
                val url = URI(server).toURL()
                RemoteWebDriver(url, capabilities)
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }

        /**
         * IMPORTANT: NOT WORKING
         *
         * It seems that "val driver = WindowsDriver(url, windowsOptions)" is taking forever to finish.
         *
         * @param appiumServer the appium server URL
         * @param window the window handler
         * @return the new created Windows driver
         */
        @Suppress("UNREACHABLE_CODE", "UNUSED_PARAMETER")
        fun createAppDriverWithWindow(appiumServer: String, window: String): WindowsDriver {
            // IMPORTANT: NOT WORKING
            logger.info { "Starting createAppDriverWithWindow. Window: $window" }
            throw RuntimeException("WindowsDriver(url, windowsOptions) is taking forever to finish")

            val windowsOptions = WindowsOptions().setAppTopLevelWindow(window)
            // Experimental is not working finding some elements
            // .setExperimentalWebDriver(true)
            windowsOptions.setCapability("appium:deviceName", "WindowsPC")
            windowsOptions.setCapability("appium:settings[imageMatchThreshold]", "0.8")

            return try {
                val url = URI(appiumServer).toURL()
                val driver = WindowsDriver(url, windowsOptions)
                logger.info { "New webdriver with window $window created" }
                driver
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }

        @Suppress("unused")
        fun createRootDriver(appiumServer: String): WindowsDriver {
            val windowsOptions = WindowsOptions().setApp("Root")
            windowsOptions.setCapability("appium:deviceName", "WindowsPC")
            windowsOptions.setCapability("appium:settings[imageMatchThreshold]", "0.8")

            return try {
                val url = URI(appiumServer).toURL()
                WindowsDriver(url, windowsOptions)
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }
    }
}
