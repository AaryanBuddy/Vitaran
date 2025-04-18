package com.example.vitaran

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName





class ReportResponseData {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<DataList>? = null
}