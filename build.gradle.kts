plugins {
    id("build-standard-jetbrains-plugin-build")
}

dependencies {
    // TODO(ChrisCarini) - Remove when IJPL-157292 is resolved
    //  Resources:
    //      - https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-faq.html#junit5-test-framework-refers-to-junit4
    //      - https://youtrack.jetbrains.com/issue/IJPL-157292/lib-testFramework.jar-is-missing-library-opentest4j
    testImplementation("org.opentest4j:opentest4j:1.3.0")
}