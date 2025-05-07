package com.example.vitaran

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class RoutesAdapter(private val routesDataList: List<RoutesDataList>) : RecyclerView.Adapter<RoutesAdapter.RoutesViewHolder>() {

    inner class RoutesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dd: TextView = itemView.findViewById(R.id.DD)
        val resv: TextView = itemView.findViewById(R.id.Resv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutesAdapter.RoutesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_route_item, parent, false)
        return RoutesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutesAdapter.RoutesViewHolder, position: Int) {
        val item  = routesDataList[position]
        holder.dd.text = "DD No: ${item.DD_Number}"
        holder.resv.text = "Resv No: ${item.Reservation_No}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, VerificationActivity::class.java)

            intent.putExtra("ddNo", item.DD_Number)
            intent.putExtra("matDocNo", item.Mat_Doc_No)
            intent.putExtra("revNo", item.Reservation_No)
            intent.putExtra("routeNo", item.Route_No)

            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int = routesDataList.size
}