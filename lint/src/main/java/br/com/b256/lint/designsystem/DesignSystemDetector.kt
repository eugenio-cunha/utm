package br.com.b256.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * A detector that checks for incorrect usages of Compose Material APIs over equivalents in
 * the Now in Android design system module.
 */
class DesignSystemDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        UQualifiedReferenceExpression::class.java,
    )

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return
                val preferredName = METHOD_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name = node.receiver.asRenderString()
                val preferredName = RECEIVER_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }
        }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DesignSystem",
            briefDescription = "Design system",
            explanation = "This check highlights calls in code that use Compose Material " +
                    "composables instead of equivalents from the Now in Android design system " +
                    "module.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                DesignSystemDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )

        // Unfortunately :lint is a Java module and thus can't depend on the :core-designsystem
        // Android module, so we can't use composable function references (eg. ::Button.name)
        // instead of hardcoded names.
        val METHOD_NAMES = mapOf(
            "MaterialTheme" to "B256Theme",
            "Button" to "B256Button",
            "OutlinedButton" to "B256OutlinedButton",
            "TextButton" to "B256TextButton",
            "FilterChip" to "B256FilterChip",
            "ElevatedFilterChip" to "B256FilterChip",
            "NavigationBar" to "B256NavigationBar",
            "NavigationBarItem" to "B256NavigationBarItem",
            "NavigationRail" to "B256NavigationRail",
            "NavigationRailItem" to "B256NavigationRailItem",
            "TabRow" to "B256TabRow",
            "Tab" to "B256Tab",
            "IconToggleButton" to "B256IconToggleButton",
            "FilledIconToggleButton" to "B256IconToggleButton",
            "FilledTonalIconToggleButton" to "B256IconToggleButton",
            "OutlinedIconToggleButton" to "B256IconToggleButton",
            "CenterAlignedTopAppBar" to "B256TopAppBar",
            "SmallTopAppBar" to "B256TopAppBar",
            "MediumTopAppBar" to "B256TopAppBar",
            "LargeTopAppBar" to "B256TopAppBar",
        )
        val RECEIVER_NAMES = mapOf(
            "Icons" to "B256Icons",
        )

        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using $name instead of $preferredName",
            )
        }
    }
}
