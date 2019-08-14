package com.softbankrobotics.dx.watsontranslator

import com.ibm.cloud.sdk.core.security.Authenticator
import com.ibm.cloud.sdk.core.security.AuthenticatorConfig
import com.ibm.cloud.sdk.core.security.AuthenticatorFactory
import com.ibm.cloud.sdk.core.service.security.IamOptions
import com.ibm.watson.language_translator.v3.LanguageTranslator
import com.ibm.watson.language_translator.v3.model.TranslateOptions
import com.ibm.watson.language_translator.v3.model.TranslationResult
import com.ibm.watson.language_translator.v3.util.Language
import org.junit.Test

class WatsonTest {

    @Test
    fun translateTest(){
        var iamOptions = IamOptions.Builder()
            .apiKey("43BGRiyP5F6CQjO0Qr1ydrRtU4dwM0bwpHWQLC5MBYy7")
            .build()
        var service = LanguageTranslator("2018-05-01",iamOptions)
        service.setEndPoint("https://gateway-lon.watsonplatform.net/language-translator/api")

        var translateOptions = TranslateOptions.Builder()
            .addText("sorry")
            .source(Language.ENGLISH)
            .target(Language.SPANISH)
            .build()
        var translationResult = service.translate(translateOptions).execute().getResult()
        println(translationResult.translations[0].translationOutput)

    }



}

