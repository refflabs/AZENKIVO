package com.example.azenkivo.Home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.azenkivo.BaseActivity
import com.example.azenkivo.R
import com.example.azenkivo.WebViewActivity
import com.example.azenkivo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Data hotline untuk dialog "Semua (4)"
    private val daftarNomor = listOf("112", "119", "113", "115")
    private val daftarHotline = listOf(
        "112 - Panggilan Darurat Umum",
        "119 - Ambulans / Medis",
        "113 - Pemadam Kebakaran",
        "115 - Basarnas (SAR)"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        // Judul bawaan disembunyikan karena Toolbar Home sudah berisi sapaan sendiri
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        // Sapaan dari SharedPreferences (Latihan Modul P7 5.1)
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Tim Siaga")
        binding.tvGreeting.text = "Halo, $username!"
        if (username == "Tamu") {
            binding.tvStatus.text = "Status: Mode Tamu (Akses Darurat)"
        }

        // Kartu hotline -> buka aplikasi telepon
        binding.card112.setOnClickListener { bukaDialer("112") }
        binding.card119.setOnClickListener { bukaDialer("119") }
        binding.card113.setOnClickListener { bukaDialer("113") }
        binding.card115.setOnClickListener { bukaDialer("115") }

        binding.tvSemuaHotline.setOnClickListener { tampilkanSemuaHotline() }

        // SOS: klik biasa hanya memberi petunjuk, tekan & tahan baru mengirim
        binding.btnSos.setOnClickListener {
            Toast.makeText(requireContext(), "Tekan dan tahan tombol SOS untuk mengirim sinyal darurat", Toast.LENGTH_SHORT).show()
        }
        binding.btnSos.setOnLongClickListener {
            tampilkanDialogSos()
            true // true: event long click sudah ditangani
        }

        // Banner gempa -> halaman Info BMKG
        binding.bannerGempa.setOnClickListener {
            startActivity(Intent(requireContext(), WebViewActivity::class.java))
        }

        // Banner SOP -> pindah ke tab Panduan
        binding.btnBukaSop.setOnClickListener {
            (requireActivity() as BaseActivity).bukaTab(R.id.panduan)
        }
    }

    // Intent ACTION_DIAL: membuka aplikasi telepon dengan nomor terisi (tidak butuh izin)
    private fun bukaDialer(nomor: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$nomor")
        startActivity(intent)
    }

    private fun tampilkanSemuaHotline() {
        // ArrayAdapter + simple_list_item_1 (Modul P7 Tahap 3) ditampilkan di dalam dialog
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            daftarHotline
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Semua Nomor Darurat")
            .setAdapter(adapter) { dialog, position ->
                bukaDialer(daftarNomor[position])
                dialog.dismiss()
            }
            .setNegativeButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun tampilkanDialogSos() {
        AlertDialog.Builder(requireContext())
            .setTitle("Kirim Sinyal SOS?")
            .setMessage("Lokasi Anda akan dibagikan ke unit responder terdekat.")
            .setIcon(R.drawable.ic_azenkivo_logo)
            .setPositiveButton("Kirim SOS") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Sinyal SOS terkirim! Mencari unit terdekat...", Toast.LENGTH_SHORT).show()
                (requireActivity() as BaseActivity).bukaTab(R.id.tracking)
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
