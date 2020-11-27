package com.pakthemekeyboard.easyurdukeyboard.pdfreader.loudreader

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.pakthemekeyboard.easyurdukeyboard.pdfreader.R
import kotlinx.android.synthetic.main.activity_pdf_reader.*
import kotlinx.android.synthetic.main.activity_pdf_reader.progressBar
import kotlinx.android.synthetic.main.activity_pdf_reader.recyclerviews
import kotlinx.android.synthetic.main.layout_index_header.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class PdfLoudReader : AppCompatActivity(),TextToSpeech.OnInitListener {
    var tts: TextToSpeech? = null
    var adapter:ReaderAdapter?=null
    var PlayPause_Flag=true
    private var list= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_reader)
        tts = TextToSpeech(applicationContext, this, "com.google.android.tts")
        hideExtraViews()
        CoroutineScope(Dispatchers.Default).launch {


            val pdftext= ConvertPdfToText(intent.getStringExtra("path"))
            val sentence=pdftext.split(".").toTypedArray()
            sentence.forEach {
                list.add(it)
            }
            runOnUiThread {

                setuprecyleview()
            }


        }
        play.setOnClickListener {
            PlayPause_Flag = if(PlayPause_Flag) {
                play.setImageResource(R.drawable.ic_pause)
                speakOut(list[adapter!!.playingposition])
                false
            } else {
                play.setImageResource(R.drawable.ic_play)
                tts!!.stop()
                true
            }

        }
        stop.setOnClickListener {
            tts!!.stop()
            PlayPause_Flag=true
            play.setImageResource(R.drawable.ic_play)
            adapter!!.playingposition=0;
            adapter?.notifyDataSetChanged()
        }
    }

    private fun setuprecyleview() {
        progressBar.visibility=View.GONE
       recyclerviews.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = ReaderAdapter(this, list,0)
        recyclerviews.adapter = adapter


    }

    private fun ConvertPdfToText(path: String?):String {
        try {
            var parsedText = ""
            val reader =
                PdfReader(path)


            val n: Int = reader.numberOfPages
            for (i in 0 until n) {
                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1)
                    .trim { it <= ' ' } + "\n" //Extracting the content from the different pages
            }

            reader.close()
            return parsedText
        } catch (e: Exception) {
            println(e)
            return "Error->"+e.message
        }

    }

    private fun hideExtraViews() {
        h_subscribe.visibility=View.GONE
        headerText.text = "PDF Reader"
        drawerIcon.setImageResource(R.drawable.ic_back)
        drawerIcon.setOnClickListener { finish() }
            indexRateus.visibility = View.GONE
            indexSearch.visibility = View.GONE
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val a: MutableSet<String> = HashSet()
                a.add("male")
                val v = Voice("en-in-x-cxx#male_3-local", Locale("en", "in"), 400, 200, true, a)
                tts!!.voice = v
                tts!!.setSpeechRate(1.0f)
                val result = tts!!.setVoice(v)
                tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onDone(utteranceId: String) {
                        resetrecycleview()
                    }

                    override fun onError(utteranceId: String) {

                    }

                    override fun onStart(utteranceId: String) {

                    }
                })
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Toast.makeText(applicationContext,"This Language is not supported",Toast.LENGTH_SHORT).show()

            }
        } else {
            Log.d("TTS1", "Initilization Failed!")
        }
    }

    private fun speakOut(sentence:String ) {
        if(sentence.isNotEmpty()) {
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "ID"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts!!.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, "ID")
            } else
                tts!!.speak(sentence, TextToSpeech.QUEUE_FLUSH, params)
        }

    }
    private fun resetrecycleview(){
       runOnUiThread {

           val playingposition = ++adapter!!.playingposition
           if(adapter!!.playingposition<list.size)
           {
           speakOut(list[playingposition])
           recyclerviews.scrollToPosition(playingposition)
           adapter?.notifyDataSetChanged()
           }
           else {
               play.setImageResource(R.drawable.ic_play)
               PlayPause_Flag=true
               adapter!!.playingposition=0;
               adapter?.notifyDataSetChanged()
           }
       }
    }

    override fun onPause() {
        tts!!.stop()
        super.onPause()

    }
}