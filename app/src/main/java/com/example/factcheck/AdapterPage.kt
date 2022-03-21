package com.example.factcheck

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.lang.Exception
import java.text.SimpleDateFormat

class AdapterPage (
    var context: Context,
    var pageArrayList: ArrayList<ModelPage>
): RecyclerView.Adapter<AdapterPage.HolderPage>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPage {
        val view = LayoutInflater.from(context).inflate(R.layout.row_pages, parent, false)
        return HolderPage(view)
    }

    override fun onBindViewHolder(holder: HolderPage, position: Int) {
        //get of data//
        val model = pageArrayList[position]

        val authorName = model.authorName
        val content = model.content
        val id = model.id
        val published = model.published
        val selfLink = model.selfLink
        val title = model.title
        val updated = model.updated
        val url = model.url

        //descritpion is in html/web formate, need to formate//
        val document = Jsoup.parse(content)
        try {
            //get image from the page, if there are multiple//
            val elements = document.select("img")
            val image = elements[0].attr("src")
            //Picasso for setting the image//
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageIv)

        }
        catch (e: Exception){
            e.printStackTrace()
        }

        //format date//
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val dateFormat2 = SimpleDateFormat("dd/MM/yyyy K:mm'a'")
        var formattedDated = ""

        try {
            val date = dateFormat.parse(published)
            formattedDated = dateFormat2.format(date)

        }
        catch (e: Exception){
            formattedDated = published
            e.printStackTrace()

        }

        //setting of data//
        holder.titleTv.text = title
        holder.descriptionTv.text = document.text()
        holder.publishInfoTv.text = "By $authorName $formattedDated"

        holder.itemView.setOnClickListener {
            //handle page click. will do in next
            val intent = Intent(context, PageDetailsActivity::class.java)
            intent.putExtra("pageId", id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return pageArrayList.size //return number of records//
    }


    //viewHolder for row_page.xml//
    inner class HolderPage(itemView: View): RecyclerView.ViewHolder(itemView){

        //init ui views for row_pages.xml//
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val publishInfoTv: TextView = itemView.findViewById(R.id.publishInfoTv)
        val imageIv: ImageView = itemView.findViewById(R.id.imageIv)
        val descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)

    }

}