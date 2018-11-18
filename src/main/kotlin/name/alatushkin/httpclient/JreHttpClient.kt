package name.alatushkin.httpclient

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*


class JreHttpClient(
    val connectionTimeout: Int = 3000,
    val readTimeout: Int = 5000
) : HttpClient {
    override suspend fun invoke(httpRequest: HttpRequest): Response {
        val url = URL(httpRequest.url)
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = httpRequest.method.name
        con.connectTimeout = connectionTimeout

        con.readTimeout = readTimeout
        con.instanceFollowRedirects = true

        try {

            httpRequest.header.forEach(con::addRequestProperty)

            addBodyToRequest(con, httpRequest)

            val status = con.responseCode

            con.inputStream.use { inputStream ->
                return Response(
                    status,
                    inputStream?.readBytes() ?: ByteArray(0)
                )
            }
        } finally {
            con.disconnect()
        }
    }

    private fun addBodyToRequest(con: HttpURLConnection, httpRequest: HttpRequest) {
        val body = httpRequest.body ?: return

        val byteArray = when (body) {
            is RequestBody.FormUrlEncoded -> {
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                ParameterStringBuilder.getParamsString(body.map).toByteArray()

            }
            is RequestBody.RawBody -> {
                con.addRequestProperty("Content-Type", "application/octet-stream")
                body.byteArray
            }
            is RequestBody.MultipartBody -> {
                val boundary = UUID.randomUUID()
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary)
                val bos = ByteArrayOutputStream(2048)
                val charset = Charset.forName("UTF-8")
                body.map.forEach { name, part ->

                    bos.write("--$boundary\n".toByteArray(charset))
                    when (part) {
                        is String -> StringPart(part)
                        is MultipartBodyPart -> part
                        else -> error("Unknown attachment type ${part.javaClass.simpleName}")
                    }.writeTo(name, bos)

                    bos.write("\n".toByteArray(charset))

                }
                bos.write("--$boundary\n\n".toByteArray(charset))

                bos.toByteArray()
            }
        }

        con.doOutput = true
        DataOutputStream(con.outputStream).use { out ->
            out.write(byteArray)
            out.flush()
        }


    }


}

object ParameterStringBuilder {
    @Throws(UnsupportedEncodingException::class)
    fun getParamsString(params: Map<String, Any?>): String {
        val result = StringBuilder()

        for ((key, value) in params) {
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            if (value != null) {
                result.append(URLEncoder.encode(value.toString(), "UTF-8"))
            }
            result.append("&")
        }

        val resultString = result.toString()
        return if (resultString.length > 0)
            resultString.substring(0, resultString.length - 1)
        else
            resultString
    }
}