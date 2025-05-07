package com.example.vitaran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class VerificationAdapter(private val verificationDataList: List<VerificationDataList>) : RecyclerView.Adapter<VerificationAdapter.VerificationViewHolder>() {

    inner class VerificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val qty: TextView = itemView.findViewById(R.id.qtyLabel)
        val uom: TextView = itemView.findViewById(R.id.uomLabel)
        val matCode: TextView = itemView.findViewById(R.id.matCode)
        val matDesp: TextView = itemView.findViewById(R.id.matDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerificationAdapter.VerificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_verification_item, parent, false)
        return VerificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerificationViewHolder, position: Int) {
        val item = verificationDataList[position]
        holder.qty.text = "QTY: ${item.Quantity}"
        holder.uom.text = "UoM: ${item.UoM}"
        holder.matCode.text = "MAR Code: ${item.Mat_Code}"
        holder.matDesp.text = "MAT Desp: ${item.Description}"
    }

    override fun getItemCount(): Int = verificationDataList.size
}