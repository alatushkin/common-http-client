package name.alatushkin.httpclient

import java.io.OutputStream
import java.nio.charset.Charset

sealed class RequestBody {
    data class FormUrlEncoded(val map: Map<String, Any>) : RequestBody()
    data class MultipartBody(val map: Map<String, Any>) : RequestBody()

    data class RawBody(val byteArray: ByteArray) : RequestBody()
}

interface MultipartBodyPart {
    fun writeTo(name: String, os: OutputStream)
}


data class StringPart(val value: String) : MultipartBodyPart {
    override fun writeTo(name: String, os: OutputStream) {
        val charset = Charset.forName("UTF-8")
        os.write("Content-Disposition: form-data; name=\"$name\"\n\n".toByteArray(charset))
        os.write(value.toByteArray(charset))
        os.write("\n".toByteArray(charset))
    }
}

data class FilePart(val fileName: String, val contentType: String, val byteArray: ByteArray) : MultipartBodyPart {
    override fun writeTo(name: String, os: OutputStream) {
        val charset = Charset.forName("UTF-8")
        os.write(
            "Content-Disposition: form-data; name=\"$name\"; fileName=\"$fileName\"\nContent-Type: $contentType\n\n".toByteArray(
                charset
            )
        )
        os.write(byteArray)
        os.write("\n".toByteArray(charset))
    }
}

data class BinaryPart(val byteArray: ByteArray) : MultipartBodyPart {
    override fun writeTo(name: String, os: OutputStream) {
        val charset = Charset.forName("UTF-8")
        os.write("Content-Disposition: form-data; name=\"$name\"\n\n".toByteArray(charset))
        os.write(byteArray)
        os.write("\n".toByteArray(charset))
    }
}

