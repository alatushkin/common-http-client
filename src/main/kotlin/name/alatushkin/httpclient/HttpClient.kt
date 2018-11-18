package name.alatushkin.httpclient

interface HttpClient {
    suspend operator fun invoke(httpRequest: HttpRequest): Response
}