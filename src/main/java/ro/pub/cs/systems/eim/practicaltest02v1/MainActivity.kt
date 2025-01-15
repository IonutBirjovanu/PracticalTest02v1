package ro.pub.cs.systems.eim.practicaltest02v1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var inputText: EditText
    private lateinit var resultText: TextView
    private lateinit var fetchButton: Button
    private lateinit var showMapButton: Button
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v1_main)

        inputText = findViewById(R.id.editTextQuery)
        resultText = findViewById(R.id.textViewResults)
        fetchButton = findViewById(R.id.buttonFetch)
        showMapButton = findViewById(R.id.buttonShowMap)

        fetchButton.setOnClickListener {
            val query = inputText.text.toString()
            if (query.length > 2) {
                fetchAutocompleteSuggestions(query)
            }
        }

        showMapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }


        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > 2) {
                    fetchAutocompleteSuggestions(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchAutocompleteSuggestions(query: String) {
        val url = "https://www.google.com/complete/search?client=chrome&q=$query"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    resultText.text = "Failed to fetch suggestions"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseString = responseBody.string()
                    Log.d("AutocompleteService", "Response: $responseString")

                    val json = JSONArray(responseString)
                    val suggestions = json.getJSONArray(1)
                    val result = StringBuilder()
                    for (i in 0 until suggestions.length()) {
                        val suggestion = suggestions.getString(i)
                        result.append(suggestion).append(", ")
                        Log.d("AutocompleteService", "Parsed suggestion: $suggestion")
                    }
                    runOnUiThread {
                        resultText.text = result.toString().trimEnd(',', ' ')
                    }
                }
            }
        })
    }
}