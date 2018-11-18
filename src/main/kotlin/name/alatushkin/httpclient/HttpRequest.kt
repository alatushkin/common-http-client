package name.alatushkin.httpclient


enum class HttpMethod {
    GET, POST, PUT, PATCH, HEAD, DELETE;

    operator fun invoke(
        url: String,
        header: Map<String, String> = emptyMap(),
        body: RequestBody? = null,
        timeout: Long = 3000
    ) = HttpRequest(this, url, header, body, timeout)
}

data class HttpRequest(
    val method: HttpMethod,
    val url: String,
    val header: Map<String, String>,
    val body: RequestBody? = null,
    val timeout: Long = 3000
)
