package com.softbankrobotics.dx.watsontranslator

import android.os.Bundle
import android.util.Log
import android.view.View
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.button_page.*

const val TAG = "MainWatsonTranslate"

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {


    var currentChatbot: QiChatbot? = null
    var currentChatFuture: Future<Void>? = null
    lateinit var qiContext: QiContext
    var chats: HashMap<String,Chat> = HashMap()
    var chatbots: HashMap<String,QiChatbot> = HashMap()
    lateinit var executors: Map<String, QiChatExecutor>
    private var language1 = "not selected yet"
    private var language2 = "not selected yet"
    private var currentLanguage = ""
    private var translation = ""
    private var baseText = ""

    private val languages = hashMapOf(
        "english" to "en",
        "french" to "fr",
        "spanish" to "es",
        "german" to "de"
    )


    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "onRobotFocusGained called")
        this.qiContext = qiContext

        executors = hashMapOf(
            "switchLanguage" to SwitchLanguageExecutor(qiContext),
            "sayTranslation" to SayTranslationExecutor(qiContext)
        )

        buildChat("english")
        buildChat("french")
        buildChat("spanish")
        //buildChat("german")
        showButtonPage()
    }


    override fun onRobotFocusRefused(reason: String?) {
    }

    override fun onRobotFocusLost() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        QiSDK.register(this, this)
        Log.i(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
    }


    override fun onDestroy() {
        QiSDK.unregister(this, this)
        Log.i(TAG, "onDestroy called")
        super.onDestroy()
    }

    private fun getLocale(language: String) : Locale {
        return when (language) {
            "english" -> Locale(Language.ENGLISH, Region.UNITED_KINGDOM)
            "french" -> Locale(Language.FRENCH, Region.FRANCE)
            "spanish" -> Locale(Language.SPANISH, Region.SPAIN)
            else -> Locale(Language.GERMAN, Region.AUSTRIA)
        }
    }

    private fun buildChat(language: String) {

        val locale = getLocale(language)

        val topic = TopicBuilder.
            with(qiContext)
            .withResource(R.raw.translation_chat)
            .build()
        val qiChatbot = QiChatbotBuilder
            .with(qiContext)
            .withTopic(topic)
            .withLocale(locale)
            .build()

        chatbots[language] = qiChatbot

        qiChatbot.executors = executors


        chats[language] = ChatBuilder
            .with(qiContext)
            .withLocale(locale)
            .withChatbot(qiChatbot)
            .build()
    }

    private fun showButtonPage() {
        runOnUiThread {
            setContentView(R.layout.button_page)
            languageHeader.text = "translate from ..."
            englishButton.setOnClickListener {
                if (language1 == "not selected yet" || language1 == "english") {
                    language1 = "english"
                    languageHeader.text = "translate from english to ..."
                } else {
                    language2 = "english"
                    currentLanguage=language1
                    setContentView(R.layout.activity_main)
                    languageView.text = "$language1 -> $language2"
                    listenInLanguage(currentLanguage)
                }
            }
            frenchButton.setOnClickListener {
                if (language1 == "not selected yet" || language1 == "french") {
                    language1 = "french"
                    languageHeader.text = "traduire de français à ..."
                } else {
                    language2 = "french"
                    currentLanguage=language1
                    setContentView(R.layout.activity_main)
                    languageView.text = "$language1 -> $language2"
                    listenInLanguage(currentLanguage)
                }
            }
            spanishButton.setOnClickListener {
                if (language1 == "not selected yet" || language1 == "spanish") {
                    language1 = "spanish"
                    languageHeader.text = "traducir del español al ..."
                } else {
                    language2 = "spanish"
                    currentLanguage=language1
                    setContentView(R.layout.activity_main)
                    languageView.text = "$language1 -> $language2"
                    listenInLanguage(currentLanguage)
                }
            }
            germanButton.setOnClickListener {
                if (language1 == "not selected yet" || language1 == "german") {
                    language1 = "german"
                    languageHeader.text = "übersetzen aus Deutsch in ..."
                } else {
                    language2 = "german"
                    currentLanguage=language1
                    setContentView(R.layout.activity_main)
                    languageView.text = "$language1 -> $language2"
                    listenInLanguage(currentLanguage)
                }
            }
        }
    }

    private fun listenInLanguage(language : String) {
        currentChatbot = chatbots[language]
        Log.i(TAG, "I am listening in $language")

        // Start the dialogue
        currentChatFuture = chats[language]?.async()?.run()
    }

    private fun translate(inputLanguage:String,outputLanguage:String,text:String): String {
            val translationService = TranslationService(inputLanguage, outputLanguage)
            return translationService.translateText(text)
    }


    private inner class SwitchLanguageExecutor(qiContext: QiContext) : BaseQiChatExecutor(qiContext) {
        override fun runWith(params: MutableList<String>?) {
            baseText = params!!.get(0)
            if(currentLanguage==language1){
                translation = translate(language1,language2,baseText)
                runOnUiThread{
                    input.text = baseText
                    languageView.text = "$language1 -> $language2"
                    output.text = translation
                }
                currentLanguage=language2
            } else {
                translation = translate(language2,language1,baseText)
                runOnUiThread{
                    input.text = baseText
                    languageView.text = "$language2 -> $language1"
                    output.text = translation
                }
                currentLanguage=language1
            }
            currentChatFuture?.cancel(true)
            SayBuilder.with(qiContext).withLocale(getLocale(currentLanguage)).withText(translation).buildAsync()
                .andThenCompose {say ->
                    say.async().run()
                }
                .thenConsume {
                    listenInLanguage(currentLanguage)
                }
        }
        override fun stop() {}
    }

    private inner class SayTranslationExecutor(qiContext: QiContext): BaseQiChatExecutor(qiContext){
        override fun runWith(params: MutableList<String>?) {
            currentChatbot?.variable("translation")?.value = translation
        }
        override fun stop() {}
    }

}