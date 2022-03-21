package com.example.factcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_page_details.*
import kotlinx.android.synthetic.main.activity_post_details.*
import kotlinx.android.synthetic.main.activity_post_details.publishInfoTv
import kotlinx.android.synthetic.main.activity_post_details.titleTv
import kotlinx.android.synthetic.main.activity_post_details.view.*
import kotlinx.android.synthetic.main.activity_post_details.webView
import org.json.JSONObject
import java.text.SimpleDateFormat
import javax.xml.transform.OutputKeys

class PostDetailsActivity : AppCompatActivity() {

    //this is the next activity when we click on each article / posts//

    private lateinit var postId : String
    private val TAG = "POST_DETAILS_TAG"

    //action Bar//


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        //get post id//
        postId = intent.getStringExtra("postId")!!
        Log.d(TAG, "onCreate: $postId")

        //setup Web View//
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        loadPostDetails()

    }

    private fun loadPostDetails() {

        //the link for the on click each artile / posts//
        val url = "https://www.googleapis.com/blogger/v3/blogs/4631547779344475690/posts/2978019261854910680?key=AIzaSyDQG17amMjRSxE2SaYGdEoJBbeDe1YwUeQ"
        Log.d(TAG, "loadPostDetails: URL: $url")
        //create API request//
        val stringRequest = StringRequest(Request.Method.GET, url, { response->
            //get api response//
            Log.d(TAG, "loadPostDetails: $response")
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
                Log.d(TAG, "loadPostDetails: ${e.message}")
            }

        })
        { error ->
            //failled//
            Log.d(TAG, "loadPostDeatils: ${error.message}")
            Toast.makeText(this,"${error.message}", Toast.LENGTH_SHORT).show()
        }
        //add volley request queue//
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }



}