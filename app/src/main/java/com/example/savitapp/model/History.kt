package com.example.savitapp.model

import com.google.gson.annotations.SerializedName

data class History(
    @SerializedName("history_id") val historyId: Int,
    @SerializedName("nominal") val nominal: Long,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("nama_barang") val namaBarang: String
)