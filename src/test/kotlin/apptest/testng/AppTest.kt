package apptest.testng

import apptest.pom.mainWindowPage
import apptest.util.AppiumProvider
import apptest.util.ReportHelper
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.WebDriverRunner
import com.codeborne.selenide.appium.SelenideAppium
import com.github.qky666.selenidepom.config.SPConfig
import com.github.qky666.selenidepom.data.TestData
import com.github.qky666.selenidepom.pom.shouldLoadRequired
import org.apache.logging.log4j.kotlin.Logging
import org.openqa.selenium.SessionNotCreatedException
import org.testng.ITestResult
import org.testng.annotations.*

class AppTest : Logging {

    // appiumService is failing when started. We need to start appium manually: 'appium server --use-plugins images'
//    private val appiumLogFile = Path.of("build", "logs", "appium.log").toFile()
//    private val appiumService: AppiumDriverLocalService = AppiumServiceBuilder()
//        .withIPAddress(System.getProperty("host", "127.0.0.1"))
//        .usingPort(Integer.parseInt(System.getProperty("port", "4723")))
//        .withLogFile(appiumLogFile)
//        .withArgument(GeneralServerFlag.USE_DRIVERS, "windows")
//        .withArgument(GeneralServerFlag.USE_PLUGINS, "images")
//        .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
//        .withArgument(GeneralServerFlag.LOCAL_TIMEZONE)
//        .build()

    @BeforeSuite(description = "Set en-US keyboard. Start appium server (not working)")
    fun beforeSuite() {
        Runtime.getRuntime()
            .exec(arrayOf("powershell.exe", "Set-WinUserLanguageList", "-LanguageList", "en-US,", "es-ES", "-force"))
            .waitFor()
        // appiumService.start()
    }

    @AfterSuite(description = "Set es-ES keyboard. Stop appium server (not working)")
    fun afterSuite() {
        // appiumService.stop()
        Runtime.getRuntime()
            .exec(arrayOf("powershell.exe", "Set-WinUserLanguageList", "-LanguageList", "es-ES,", "en-US", "-force"))
            .waitFor()
    }

    @BeforeTest(description = "Setup driver", alwaysRun = true)
    @Parameters("env", "server", "process", "aumid", "appWorkingDir", "appTopLevelWindow")
    fun beforeMethod(
        env: String, server: String, process: String, aumid: String, appWorkingDir: String, appTopLevelWindow: String
    ) {
        if (appTopLevelWindow.isEmpty()) {
            val command = arrayOf("taskkill", "/im", process, "/f")
            Runtime.getRuntime().exec(command).waitFor()
        }

        // Set env
        TestData.init(env)

        SPConfig.quitCurrentThreadDriver()
        SPConfig.selenideConfig.browserSize(null)
        if (appTopLevelWindow.isEmpty()) WebDriverRunner.setWebDriver(
            AppiumProvider.createYWinAppDriver(server, aumid, appWorkingDir)
        ) else WebDriverRunner.setWebDriver(AppiumProvider.createAppDriverWithWindow(server, appTopLevelWindow))
        logger.info { "Webdriver created" }
    }

    @AfterMethod(description = "Take screenshot if failed", alwaysRun = true)
    fun afterMethod(result: ITestResult) {
        // Attach screenshot and write ToscaReport
        if (!result.isSuccess) {
            ReportHelper.attachScreenshot("Test failed screenshot")
        }
    }

    @AfterTest(description = "Close driver", alwaysRun = true)
    @Parameters("process")
    fun afterMethod(process: String) {
        // TODO: Close app (if open)

        // Quit webdriver
        Selenide.closeWebDriver()
        logger.info { "Closed webdriver" }

        // Kill app (just in case)
        Runtime.getRuntime().exec(arrayOf("taskkill", "/im", process, "/f")).waitFor()
    }

    @Test
    fun setupApp() {
        // Start app
        try {
            SelenideAppium.launchApp()
        } catch (_: SessionNotCreatedException) {
        }

        logger.info { "App started" }
        mainWindowPage.shouldLoadRequired()
    }
}
