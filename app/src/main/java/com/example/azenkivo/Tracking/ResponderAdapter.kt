package com.example.azenkivo.Tracking

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.azenkivo.R
import com.example.azenkivo.databinding.ItemResponderBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Custom Adapter: ResponderModel -> item_responder.xml
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
            .placeholder(R.drawable.ph_avatar) // tampil selama gambar dimuat
            .error(R.drawable.ph_avatar) // tampil jika gagal dimuat
            .into(binding.avatarImg)
        binding.avatarImg.contentDescription = context.getString(R.string.resp_photo_desc, data.namaUnit)

        binding.textNamaUnit.text = data.namaUnit
        binding.textInstansi.text = data.instansi
        binding.textStatus.text = data.status
        binding.textEta.text = data.eta
        binding.textJarak.text = data.jarak

        // Warna titik avatar & teks status mengikuti keadaan unit
        val warnaStatus = warnaUntukStatus(data.status)
        binding.dotStatus.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, warnaStatus))
        binding.textStatus.setTextColor(ContextCompat.getColor(context, warnaStatus))

        // TalkBack membaca satu kalimat utuh per kartu
        view.contentDescription = context.getString(
            R.string.resp_card_desc,
            data.namaUnit, data.instansi, data.status, data.jarak, data.eta
        )

        // Aksi cepat "Hubungi": langsung ke dialer tanpa membuka dialog
        binding.btnHubungi.contentDescription =
            context.getString(R.string.resp_call_desc, data.namaUnit, data.nomor)
        binding.btnHubungi.setOnClickListener { bukaDialer(data.nomor) }

        // Aksi klik dipasang di adapter, bukan di Fragment
        view.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(data.namaUnit)
                .setMessage(
                    context.getString(
                        R.string.resp_dialog_message,
                        data.instansi, data.status, data.jarak, data.eta
                    )
                )
                .setPositiveButton(context.getString(R.string.resp_dialog_call, data.nomor)) { dialog, _ ->
                    bukaDialer(data.nomor)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.resp_dialog_close) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        return view
    }

    private fun bukaDialer(nomor: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$nomor")
        context.startActivity(intent)
    }

    // Menuju lokasi / sirene = biru, patroli = amber
    @ColorRes
    private fun warnaUntukStatus(status: String): Int = when {
        status.contains("Menuju", ignoreCase = true) ||
            status.contains("Sirene", ignoreCase = true) -> R.color.accent_strong
        status.contains("Patroli", ignoreCase = true) -> R.color.status_patrol
        else -> R.color.status_online
    }
}
