package com.directdev.portal.model

import com.squareup.moshi.Json

class LoginRequestBody(val uid: String, val pass: String, @Json(name = "ctl00\$ContentPlaceHolder1\$SubmitButtonBM") val button: String = "Login")
