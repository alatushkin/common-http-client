package name.alatushkin.httpclient


fun httpClient(
    connectionTimeout: Int = 3000,
    readTimeout: Int = 5000
): HttpClient = JreHttpClient(connectionTimeout, readTimeout)