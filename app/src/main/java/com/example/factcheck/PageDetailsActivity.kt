package com.example.factcheck

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_page_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import javax.xml.transform.OutputKeys

class PageDetailsActivity : AppCompatActivity() {

    private lateinit var pageId: String

    private  val TAG = "PAGE_DETAILS_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_details)

        pageId = intent.getStringExtra("pageId")!!
        Log.d(TAG, "onCreate: ID:$pageId")

        loadPageDetils()

    }

    private fun loadPageDetils() {
        val url = " https://www.googleapis.com/blogger/v3/blogs/4631547779344475690/pages/169380283555933696?key=AIzaSyDQG17amMjRSxE2SaYGdEoJBbeDe1YwUeQ"
        Log.d(TAG, "loadPageDetails: URL: $url")
        //create API request//
        val stringRequest = StringRequest(Request.Method.GET, url, {response->
            //get api response//
            Log.d(TAG, "loadPageDetails: $response")
            try {
                val jsonObject = JSONObject(response)
                val title = jsonObject.getString("title")
                val published = jsonObject.getString("published")
                val content = jsonObject.getString("content")
                val url = jsonObject.getString("url")
                val id = jsonObject.getString("id")
                val displayName = jsonObject.getJSONObject("author").getString("displayName")
                val image = jsonObject.getJSONObject("author").getJSONObject("image").getString("url")

                //format date//
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val dateFormat2 = SimpleDateFormat("dd/MM/yyyy K:mm'a'")
                var formattedDated = ""

                try {
                    val date = dateFormat.parse(published)
                    formattedDated = dateFormat2.format(date)

                }
                catch (e: java.lang.Exception){
                    formattedDated = published
                    e.printStackTrace()

                }

                //set data//
                titleTv.text = title
                publishInfoTv.text = "By $displayName $formattedDated"

                //load content which is in web/htm in webview//
                webView.loadDataWithBaseURL(null, content, "text/html", OutputKeys.ENCODING, null)





            }
            catch (e: Exception){
                Log.d(TAG, "loadPageDetails: ${e.message}")
            }

        })
        { error ->
            //failled//
            Log.d(TAG, "loadPageDeatils: ${error.message}")
            Toast.makeText(this,"${error.message}", Toast.LENGTH_SHORT).show()
        }
        //add volley request queue//
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)

    }
}