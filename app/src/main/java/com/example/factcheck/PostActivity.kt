package com.example.factcheck

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.close
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.common.io.Closeables.close
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_post.*
import org.json.JSONObject
import android.app.ActionBar as ActionBar1

class PostActivity : AppCompatActivity() {

    //after login, the next screen will be the post activity//

    private var url = ""
    private val TAG = "MAIN_TAG"

    private lateinit var postArrayList: ArrayList<ModelPost>
    private lateinit var adapterPost: AdapterPost

    //Navigation View//
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout
    lateinit var actionBarDrawerLayout: ActionBarDrawerToggle

   // private lateinit var actionBar: ActionBar

    private lateinit var progressDialog: ProgressDialog

    private var isSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        drawerLayout = findViewById(R.id.drawerLayout)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)


        val actionBarDrawerLayout = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerLayout)
        actionBarDrawerLayout.syncState()

        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId){
                R.id.nav_profile -> replaceFragment(ProfileFragment(), it.title.toString())
                R.id.nav_aboutapp -> Toast.makeText(applicationContext, "Clicked AboutApp", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> Toast.makeText(applicationContext, "Clicked Logout",Toast.LENGTH_SHORT).show()



            }
            true
        }


        //Navigation View init//


        //setup dialog//
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait....")

        //clear tge list before adding//
        postArrayList = ArrayList()
        postArrayList.clear()

        loadPosts()

        btn_Search.setOnClickListener {
            url = ""
            postArrayList = ArrayList()
            postArrayList.clear()
            val query = et_search.text.toString().trim()
            if (TextUtils.isEmpty(query)){
                loadPosts()
            } else{
                searchPosts(query)
            }
        }

    }

    private fun replaceFragment(fragment: Fragment, title : String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (actionBarDrawerLayout.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    private fun loadPosts() {

        isSearch = false

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.show()

        //the url link that will list all the articles / posts//
            val url = "https://www.googleapis.com/blogger/v3/blogs/4631547779344475690/posts?maxResults=7&key=AIzaSyDQG17amMjRSxE2SaYGdEoJBbeDe1YwUeQ"
        Log.d(TAG, "loadPost: URL: $url")


        //request api call//
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {response ->
                //response is recieved//
                Log.d(TAG, "loadPages: $response")

                //response is in JSON Object//
                val jsonObject = JSONObject(response)
                //get array of pages from object//
                val jsonArrayPages = jsonObject.getJSONArray("items")
                //init and clear list befor adding data//
                postArrayList = ArrayList()
                postArrayList.clear()

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
                        val modelPost = ModelPost(
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
                        postArrayList.add(modelPost)
                    }
                    catch (e: Exception){
                        Log.d(TAG, "loadPages: ${e.message}")
                    }
                }

                //setup adapter//
                adapterPost = AdapterPost(this@PostActivity, postArrayList)
                postRv.adapter = adapterPost


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

    //for search view//

    private fun searchPosts (query: String ){

        isSearch = true

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.show()

        //it will search the key words from the article / posts//
        val url = "https://www.googleapis.com/blogger/v3/blogs/4631547779344475690/posts/search?q=$query&key=AIzaSyDQG17amMjRSxE2SaYGdEoJBbeDe1YwUeQ"
        Log.d(TAG, "searchPosts: URL: $url")


        //request api call//
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {response ->
                //response is recieved//
                Log.d(TAG, "searchPosts: $response")

                //response is in JSON Object//
                val jsonObject = JSONObject(response)
                //get array of pages from object//
                val jsonArrayPages = jsonObject.getJSONArray("items")
                //init and clear list befor adding data//
                postArrayList = ArrayList()
                postArrayList.clear()

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
                        val modelPost = ModelPost(
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
                        postArrayList.add(modelPost)
                    }
                    catch (e: Exception){
                        Log.d(TAG, "searchPosts: ${e.message}")
                    }
                }

                //setup adapter//
                adapterPost = AdapterPost(this@PostActivity, postArrayList)
                postRv.adapter = adapterPost


            },  { error ->
                //failed to receive//
                progressDialog.dismiss()
                Log.d(TAG, "searchPosts: ${error.message}")
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

            } )

        progressDialog.dismiss()

        //add request to qeue//
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)

    }
}