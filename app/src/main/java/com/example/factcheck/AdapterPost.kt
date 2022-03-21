package com.example.factcheck

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

class AdapterPost (
    private val context: Context,
    private val postArrayList: ArrayList<ModelPost>
        ): RecyclerView.Adapter<AdapterPost.HolderPost>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPost {
        val view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false)
        return HolderPost(view)
    }

    override fun onBindViewHolder(holder: HolderPost, position: Int) {
        //get data, set data//
        val model = postArrayList[position]

        //get data//
        val authorName = model.authorName
        val content = model.content
        val published = model.published
        val selfLink = model.selfLink
        val title = model.title
        val updated = model.updated
        val  url = model.url
        val id = model.id

        //convert html to simple context//

        val document = Jsoup.parse(content)
        try {
            //getting if image//
            val elements = document.select("img")
            val image = elements.attr("src")
            //setting of image//
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv)
        }
        catch (e: Exception){
            //exception while getting image//
            holder.imageIv.setImageResource(R.drawable.ic_image_black)
        }

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
        //setting of data//
        holder.titleTv.text = title
        holder.descriptionTv.text = document.text()
        holder.publishInfoTv.text = "By $authorName $formattedDated"

        holder.itemView.setOnClickListener {
            //page click, next page//
            val intent = Intent(context,PostDetailsActivity::class.java)
            intent.putExtra("postId",id)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }

    //viewHolder Class, for row_post.xml//
    inner class HolderPost(itemView: View): RecyclerView.ViewHolder(itemView){
        //ui views//
        var moreBtn: ImageButton = itemView.findViewById(R.id.moreBtn)
        var titleTv: TextView = itemView.findViewById(R.id.titleTv)
        var publishInfoTv: TextView = itemView.findViewById(R.id.publishInfoTv)
        var imageIv: ImageView = itemView.findViewById(R.id.imageIv)
        var descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)
    }



}