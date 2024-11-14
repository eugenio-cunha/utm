package br.com.b256.lint

import br.com.b256.lint.designsystem.DesignSystemDetector
import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

class B256IssueRegistry : IssueRegistry() {

    override val issues = listOf(
        DesignSystemDetector.ISSUE,
        TestMethodNameDetector.FORMAT,
        TestMethodNameDetector.PREFIX,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "B256",
        feedbackUrl = "https://github.com/eugenio-cunha/b256/issues",
        contact = "https://github.com/eugenio-cunha/b256",
    )
}
