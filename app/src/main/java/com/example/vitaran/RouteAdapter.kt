package com.example.vitaran

import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class RouteAdapter(private val routeDataList: List<RouteDataList>) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dd: TextView = itemView.findViewById(R.id.DD_No)
        val resv: TextView = itemView.findViewById(R.id.Resv_No)
        val vehicle: TextView = itemView.findViewById(R.id.Vehicle_No)
        val routeName: TextView = itemView.findViewById(R.id.Route_Name)
        val status: TextView = itemView.findViewById(R.id.Status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_report_item, parent, false)

        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val item = routeDataList[position]
        holder.dd.text = item.DD_Number
        holder.resv.text = item.Reservation_No
        holder.vehicle.text = item.Vehicle_Number
        holder.routeName.text = item.Route_Name
        holder.status.text = item.Status
    }

    override fun getItemCount(): Int = routeDataList.size

}