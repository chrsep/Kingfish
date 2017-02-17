package com.directdev.portal.utils

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**-------------------------------------------------------------------------------------------------
 *
 * Retrofit can crash the application when it receives a response that contains no data other than the
 * one placed on the header.
 *
 * But sometimes we do need to call an API (such as for signing in) that returns no value and get some data
 * (Such as cookies) from its response header. This nullConverter will prevent retrofit from crashing
 * when we make such call.
 *
 *------------------------------------------------------------------------------------------------*/

class NullConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit) = Converter<ResponseBody, Any> {
        if (it.contentLength() != 0L)
            retrofit.nextResponseBodyConverter<Any>(this, type, annotations).convert(it)
        else null
    }
}
