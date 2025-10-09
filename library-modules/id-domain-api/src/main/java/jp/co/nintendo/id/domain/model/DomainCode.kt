package jp.co.nintendo.id.domain.model

/**
 * A registry enum class for managing code of domain in app.
 *
 * We could create an identification code for a domain or subdomains that the domain is aggregating.
 */
enum class DomainCode(val code: String) {
    ChatMessage("MSG"),
    ChatChannel("CHATCH")
}
