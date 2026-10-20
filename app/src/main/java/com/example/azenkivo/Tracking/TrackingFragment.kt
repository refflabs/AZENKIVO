package com.example.azenkivo.Tracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.azenkivo.BaseActivity
import com.example.azenkivo.R
import com.example.azenkivo.databinding.FragmentTrackingBinding

class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    // Data dummy unit responder (dispatch disimulasikan, belum ada backend).
    // Urutan dari yang paling cepat tiba (ETA terkecil).
    private val responderList = listOf(
        ResponderModel("Unit Medis Reaksi Cepat 02", "RSUD Siaga • dr. Hendra", "Sirene Aktif • Prioritas Utama", "1.4 km", "4 menit", "119", "https://avatar.iran.liara.run/public/1"),
        ResponderModel("Relawan Siaga RT 04", "Relawan AZENKIVO • Fajar", "Menuju Lokasi", "0.6 km", "5 menit", "112", "https://avatar.iran.liara.run/public/2"),
        ResponderModel("Ambulans PMI 07", "PMI Kota • Rina S.", "Siaga di Pos", "2.3 km", "7 menit", "119", "https://avatar.iran.liara.run/public/3"),
        ResponderModel("Damkar Pos Sektor 3", "Dinas Damkar • Regu B", "Siaga di Pos", "2.1 km", "8 menit", "113", "https://avatar.iran.liara.run/public/4"),
        ResponderModel("Patroli Polisi 05", "Polsek • Bripka Dimas", "Patroli Wilayah", "2.6 km", "9 menit", "110", "https://avatar.iran.liara.run/public/5"),
        ResponderModel("Tim SAR Basarnas 01", "Basarnas • Andi P.", "Siaga di Pos", "3.8 km", "12 menit", "115", "https://avatar.iran.liara.run/public/6")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Live Tracking"
        (requireActivity() as AppCompatActivity).supportActionBar?.subtitle = "Unit responder menuju lokasi"

        // Ringkasan ETA & jarak dari unit pertama (paling cepat tiba)
        val terdekat = responderList[0]
        binding.tvEta.text = "ETA: ${terdekat.eta.uppercase()}"
        binding.tvJarak.text = "Jarak: ${terdekat.jarak} ke lokasi"

        // Pasang Custom Adapter ke ListView (Modul P7 langkah 4.7)
        val adapter = ResponderAdapter(requireContext(), responderList)
        binding.listResponder.adapter = adapter

        // Hubungi Ambulans -> buka aplikasi telepon nomor 119
        binding.btnHubungiAmbulans.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:119")
            startActivity(intent)
        }

        // Batal SOS -> konfirmasi, lalu kembali ke Beranda
        binding.btnBatalSos.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Batalkan SOS?")
                .setMessage("Unit responder akan diberi tahu bahwa keadaan sudah aman.")
                .setPositiveButton("Ya, Batalkan") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "SOS dibatalkan", Toast.LENGTH_SHORT).show()
                    (requireActivity() as BaseActivity).bukaTab(R.id.beranda)
                }
                .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
