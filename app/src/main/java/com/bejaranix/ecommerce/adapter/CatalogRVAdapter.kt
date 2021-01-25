package com.bejaranix.ecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bejaranix.ecommerce.databinding.CatalogItemBinding
import com.bejaranix.ecommerce.model.CatalogItem
import com.bejaranix.ecommerce.utils.getProgressDrawable
import com.bejaranix.ecommerce.utils.loadImage

class CatalogRVAdapter(
    private val mInflater: LayoutInflater,
    private var mItems: ArrayList<CatalogItem> = arrayListOf())
    : RecyclerView.Adapter<CatalogRVAdapter.CatalogViewHolder>() {


    fun updateValues(mItems: ArrayList<CatalogItem>){
        this.mItems = mItems
        notifyDataSetChanged()
    }

    class CatalogViewHolder(val viewHolder: CatalogItemBinding):RecyclerView.ViewHolder(viewHolder.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder =
        CatalogViewHolder(CatalogItemBinding.inflate(mInflater, parent, false))

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val catalogItem = mItems[position]
        holder.viewHolder.titleItem.text = catalogItem.title
        holder.viewHolder.priceItem.text = String.format("%.2f",catalogItem.price.toFloat())
        holder.viewHolder.imageItem.loadImage(catalogItem.image,getProgressDrawable(holder.viewHolder.root.context))
    }

    override fun getItemCount(): Int  = mItems.size

}