package com.example.azenkivo.Tracking

// Wadah data satu unit responder (seperti MessageModel di Modul P7 langkah 4.2)
data class ResponderModel(
    val namaUnit: String,
    val instansi: String,
    val status: String,
    val jarak: String,
    val eta: String,
    val nomor: String,
    val fotoUrl: String
)
