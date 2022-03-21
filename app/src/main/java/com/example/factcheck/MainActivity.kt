package com.example.factcheck

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.factcheck.databinding.ActivityMainBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import android.app.ActionBar as ActionBar1

@Suppress("DEPRECATION")
class MainActivity: AppCompatActivity() {

    @Suppress("DEPRECATION")
    private val TAG = "PAGES_TAG"

    private lateinit var actionBar: ActionBar



    private lateinit var pageArrayList: ArrayList<ModelPage>
    private lateinit var adapterPage: AdapterPage

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // actionBar = supportActionBar!!
       // actionBar.title = "Fact _ Check"
       // actionBar.subtitle = "Pages"
       // actionBar.setDisplayHomeAsUpEnabled(true)
       // actionBar.setDisplayShowHomeEnabled(true)

        loadPages()



    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun loadPages() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading Pages....")
        progressDialog.show()

        //url of API//
        val url = " https://www.googleapis.com/blogger/v3/blogs/4631547779344475690/pages?key=AIzaSyDQG17amMjRSxE2SaYGdEoJBbeDe1YwUeQ"
        Log.d(TAG, "loadPages: URL: $url")

        //request api call//
        val stringRequest = StringRequest(Request.Method.GET, url,
             {response ->
                //response is recieved//
                Log.d(TAG, "loadPages: $response")

                //response is in JSON Object//
                val jsonObject = JSONObject(response)
                //get array of pages from object//
                val jsonArrayPages = jsonObject.getJSONArray("items")
                //init and clear list befor adding data//
                pageArrayList = ArrayList()
                pageArrayList.clear()

                //get all pages from jsonArrayPages//
                for (i in 0 until jsonArrayPages.length()){
                    try {
                        //each pagess//
                        val jsonObjectPage = jsonArrayPages.getJSONObject(i)

                        //get data from jsonObject//
                        val id = jsonObjectPage.getString("id")
                        val title = jsonObjectPage.getString("title")
                        val content = jsonObjectPage.getString("content")
                        val published = jsonObjectPage.getString("published")
                        val updated = jsonObjectPage.getString("updated")
                        val url = jsonObjectPage.getString("url")
                        val selfLink = jsonObjectPage.getString("selfLink")
                        //displayName is in jsonObjectPage//
                        val displayName = jsonObjectPage.getJSONObject("author").getString("displayName")
                        //profile image//
                        val image = jsonObjectPage.getJSONObject("author").getJSONObject("image").getString("url")

                        //set data//
                        val modelPage = ModelPage(
                            "$displayName",
                        "$content" ,
                            "$id",
                            "$published",
                            "$selfLink",
                            "$title",
                            "$updated",
                        "$url"
                        )

                        //add model to list//
                        pageArrayList.add(modelPage)
                    }
                    catch (e: Exception){
                        Log.d(TAG, "loadPages: ${e.message}")
                    }
                }

                //setup adapter//
                adapterPage = AdapterPage(this@MainActivity, pageArrayList)
                pagesRv.adapter = adapterPage


        },  { error ->
                //failed to receive//
                progressDialog.dismiss()
                Log.d(TAG, "loadPages: ${error.message}")
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

        } )

        progressDialog.dismiss()

        //add request to qeue//
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)


    }


}





