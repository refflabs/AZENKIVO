package com.example.azenkivo.Tracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.azenkivo.MainActivity
import com.example.azenkivo.R
import com.example.azenkivo.databinding.FragmentTrackingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    // Data dummy unit responder
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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.track_title)
        (requireActivity() as AppCompatActivity).supportActionBar?.subtitle =
            getString(R.string.track_subtitle, responderList.size)

        // Ringkasan ETA & jarak dari unit pertama
        val terdekat = responderList[0]
        binding.tvEta.text = terdekat.eta
        binding.tvJarak.text = getString(R.string.track_distance, terdekat.jarak)
        binding.tvJumlahUnit.text = getString(R.string.track_list_count, responderList.size)

        // Pasang Custom Adapter ke ListView
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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.track_dialog_cancel_title)
                .setMessage(R.string.track_dialog_cancel_message)
                .setPositiveButton(R.string.track_dialog_cancel_yes) { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(requireContext(), R.string.track_cancelled, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainActivity).bukaTab(R.id.beranda)
                }
                .setNegativeButton(R.string.track_dialog_cancel_no) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
