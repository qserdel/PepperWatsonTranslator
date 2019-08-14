package com.softbankrobotics.dx.watsontranslator

import com.ibm.cloud.sdk.core.service.security.IamOptions
import com.ibm.watson.language_translator.v3.LanguageTranslator
import com.ibm.watson.language_translator.v3.model.TranslateOptions
import com.ibm.watson.language_translator.v3.util.Language

class TranslationService(inputLanguage:String,outputLanguage:String) {


    var inputLanguage = inputLanguage
    var outputLanguage = outputLanguage

    var iamOptions = IamOptions.Builder()
        .apiKey("43BGRiyP5F6CQjO0Qr1ydrRtU4dwM0bwpHWQLC5MBYy7")
        .build()
    var service = LanguageTranslator("2018-05-01",iamOptions)
    init {
        service.setEndPoint("https://gateway-lon.watsonplatform.net/language-translator/api")
    }


    fun translateText(inputText:String):String{

        var translateOptions = TranslateOptions.Builder()
            .addText(inputText)
            .source(inputLanguage)
            .target(outputLanguage)
            .build()
        var translationResult = service.translate(translateOptions).execute().getResult()
        return translationResult.translations[0].translationOutput
    }
}