package apptest.util

import com.codeborne.selenide.Condition
import com.codeborne.selenide.WebElementCondition
import io.appium.java_client.AppiumBy

@Suppress("unused")
fun expanded(expanded: Boolean = true): WebElementCondition {
    return Condition.match("expanded") { (it.findElements(AppiumBy.tagName("DataItem")).size > 1) == expanded }
}

@Suppress("unused")
fun onScreen(onScreen: Boolean = true): WebElementCondition {
    val expectedIsOffscreen = if (onScreen) "False" else "True"
    return Condition.attribute("IsOffscreen", expectedIsOffscreen)
}
