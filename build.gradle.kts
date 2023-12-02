import ru.vyarus.gradle.plugin.python.task.PythonTask

group = "es.mtp.test"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

plugins {
    idea
    kotlin("jvm") version "1.9.21"
    id("com.github.ben-manes.versions") version "0.50.0"
    id("io.qameta.allure") version "2.11.2"
    id("org.gradle.test-retry") version "1.5.7"
    id("ru.vyarus.use-python") version "3.0.0"
}

dependencies {
    val log4jVersion = "2.22.0"

    testImplementation("com.github.qky666:selenide-pom:0.23.4")
    testImplementation("com.codeborne:selenide-appium:7.0.3")
    testImplementation("org.testng:testng:7.8.0")
    testImplementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
    testImplementation("org.apache.logging.log4j:log4j-api-kotlin:1.3.0")
    testImplementation(kotlin("test"))
//    testImplementation("org.aspectj:aspectjweaver:1.9.20.1")
}

allure {
    // https://mvnrepository.com/artifact/io.qameta.allure/allure-testng
    version.set("2.24.0")
}

tasks.test {
    useTestNG {
        suiteXmlFiles = listOf(File("src/test/resources/testng.xml"))
        useDefaultListeners = true
    }
    retry {
        retry {
            maxRetries.set(0)
            maxFailures.set(20)
            failOnPassedAfterRetry.set(false)
        }
    }
    System.getProperties().forEach { property, value ->
        val propString = property.toString()
        val valueString = value.toString()
        listOf("project.", "data.", "selenide.", "selenide-pom.", "allure.").forEach {
            if (propString.startsWith(it, true)) {
                systemProperty(propString, valueString)
            }
        }
        listOf("param.", "parameter.").forEach {
            if (propString.startsWith(it, true)) {
                val newPropString = propString.replaceFirst(it, "", true)
                systemProperty(newPropString, valueString)
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.compileTestKotlin {
    kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
}

tasks.compileTestJava {
    options.compilerArgs.addAll(listOf("-encoding", "UTF-8"))
}

python {
    minPythonVersion = "3.12.0"
    minPipVersion = "23.2.1"
    virtualenvVersion = "20.24.5"
    pip("allure-combine:1.0.11")
}

task<PythonTask>("allureCombine") {
    val buildDir = layout.buildDirectory.asFile.get()
    val reportDir = "$buildDir/reports/allure-report/allureReport"
    val combineDir = "$buildDir/reports/allure-combine"
    command = "-m allure_combine.combine $reportDir --dest $combineDir --auto-create-folders --remove-temp-files"
    dependsOn.add("allureReport")
}
