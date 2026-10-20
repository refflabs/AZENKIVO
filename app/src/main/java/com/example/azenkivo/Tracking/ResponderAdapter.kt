package com.example.azenkivo.Tracking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.azenkivo.R
import com.example.azenkivo.databinding.ItemResponderBinding

// Custom Adapter: menghubungkan data ResponderModel dengan item_responder.xml
// (pola sama seperti MessageAdapter di Modul P7 langkah 4.6)
class ResponderAdapter(
    context: Context,
    private val responders: List<ResponderModel>
) : ArrayAdapter<ResponderModel>(context, 0, responders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemResponderBinding.inflate(LayoutInflater.from(context), parent, false)
        val view = binding.root
        val data = responders[position]

        // Muat foto petugas dari URL ke ImageView
        Glide.with(context)
            .load(data.fotoUrl)
            .placeholder(R.drawable.ic_azenkivo_logo) // tampil selama gambar dimuat / jika gagal
            .into(binding.avatarImg)

        binding.textNamaUnit.text = data.namaUnit
        binding.textInstansi.text = data.instansi
        binding.textStatus.text = data.status
        binding.textEta.text = data.eta
        binding.textJarak.text = data.jarak

        // Aksi klik dipasang di adapter, bukan di Fragment (Modul P7 langkah 4.8)
        view.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(data.namaUnit)
                .setMessage(
                    "Instansi: ${data.instansi}\n" +
                    "Status: ${data.status}\n" +
                    "Jarak: ${data.jarak}\n" +
                    "Perkiraan tiba: ${data.eta}"
                )
                .setPositiveButton("Hubungi ${data.nomor}") { dialog, _ ->
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${data.nomor}")
                    context.startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("Tutup") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        return view
    }
}
