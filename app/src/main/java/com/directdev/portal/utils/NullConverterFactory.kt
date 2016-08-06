package com.directdev.portal.utils

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

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
