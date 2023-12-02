@file:Suppress("unused")

package paint.util

import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

val robot = Robot()

fun robotSendKeys(text: String) {
    // It doesn't work with all characters (for example, '%' is not working)
    text.toCharArray().forEach {
        val keyCode = KeyEvent.getExtendedKeyCodeForChar(it.code)
        if (KeyEvent.VK_UNDEFINED == keyCode) throw RuntimeException("Key code not found for character '$it'")
        if (it.isUpperCase()) robot.keyPress(KeyEvent.VK_SHIFT)
        robot.keyPress(keyCode)
        robot.keyRelease(keyCode)
        if (it.isUpperCase()) robot.keyRelease(KeyEvent.VK_SHIFT)
    }
}

fun robotSendAltF4() {
    robotSendKeyShortcut(KeyEvent.VK_ALT, KeyEvent.VK_F4)
}

fun robotSendKeyShortcut(vararg keys: Int) {
    keys.forEach { robot.keyPress(it) }
    keys.reversed().forEach { robot.keyRelease(it) }
}

fun robotSendKeys(vararg keys: Int) {
    keys.forEach {
        robot.keyPress(it)
        robot.keyRelease(it)
    }
}

fun robotSendKeysUsingClipboard(text: String) {
    val stringSelection = StringSelection(text)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

    run breaking@{
        (0..50).forEach {
            try {
                clipboard.setContents(stringSelection, stringSelection)
                return@breaking
            } catch (e: IllegalStateException) {
                if (it == 50) throw e
                robot.delay(100)
            }
        }
    }


    robot.keyPress(KeyEvent.VK_CONTROL)
    robot.keyPress(KeyEvent.VK_V)
    robot.keyRelease(KeyEvent.VK_V)
    robot.keyRelease(KeyEvent.VK_CONTROL)
}

fun robotClickPoint(point: Point) {
    robot.mouseMove(point.getX(), point.getY())
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
}

fun <T : SelenideElement> T.robotClickWithOffset(
    fromPoint: FromPoint = FromPoint.CENTER, xOffset: Int = 0, yOffset: Int = 0
): T {
    val clickPoint = fromPoint.forElement(this).moveBy(xOffset, yOffset)
    robotClickPoint(clickPoint)
    return this
}

fun <T : SelenideElement> T.robotClickWithOffset(
    fromPoint: FromPoint = FromPoint.CENTER, offset: Dimension = Dimension(0, 0)
): T {
    return robotClickWithOffset(fromPoint, offset.getWidth(), offset.getHeight())
}

interface ForElement {
    fun forElement(element: SelenideElement): Point
}

enum class FromPoint : ForElement {
    CENTER {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(element.size.getWidth() / 2, element.size.getHeight() / 2)
        }
    },
    TOP_LEFT {
        override fun forElement(element: SelenideElement): Point {
            return element.location
        }
    },
    TOP_RIGHT {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(element.size.getWidth(), 0)
        }
    },
    BOTTOM_LEFT {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(0, element.size.getHeight())
        }
    },
    BOTTOM_RIGHT {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(element.size.getWidth(), element.size.getHeight())
        }
    },
    CENTER_LEFT {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(0, element.size.getHeight() / 2)
        }
    },
    CENTER_RIGHT {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(element.size.getWidth(), element.size.getHeight() / 2)
        }
    },
    CENTER_TOP {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(element.size.getWidth() / 2, 0)
        }
    },
    CENTER_BOTTOM {
        override fun forElement(element: SelenideElement): Point {
            return element.location.moveBy(element.size.getWidth() / 2, element.size.getHeight())
        }
    }
}
