package com.example.vitaran

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ReportAdapter(private val dataList: List<DataList>) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeTextView: TextView = itemView.findViewById(R.id.routeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_item, parent, false)

        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = dataList[position]
        holder.routeTextView.text = item.Route

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RouteActivity::class.java)

            intent.putExtra("routeId", item.Route_Id)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.size
}
