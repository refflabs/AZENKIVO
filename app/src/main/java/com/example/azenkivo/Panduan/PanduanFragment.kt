package com.example.azenkivo.Panduan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.azenkivo.SopDetailActivity
import com.example.azenkivo.R
import com.example.azenkivo.TutorialActivity
import com.example.azenkivo.databinding.FragmentPanduanBinding
import com.google.android.material.chip.Chip

class PanduanFragment : Fragment() {

    private var _binding: FragmentPanduanBinding? = null
    private val binding get() = _binding!!

    // Data SOP: judul + deskripsi untuk SimpleAdapter (Modul P7 3.4)
    private val dataListWithDesc = listOf(
        mapOf(
            "title" to "Resusitasi Jantung Paru (CPR)",
            "desc" to "Penanganan henti napas & henti jantung",
            "kategori" to "Pertolongan Pertama (P3K)",
            "badge" to "GOLDEN RATIO 30:2",
            "nomor" to "119",
            "langkahJudul" to arrayOf(
                "Cek Respon & Kesadaran Korban",
                "Buka Jalan Napas & Periksa Napas",
                "Kompresi Dada 30 Kali Cepat & Kuat",
                "Beri 2 Kali Napas Buatan & Ulangi"
            ),
            "langkahIsi" to arrayOf(
                "Tepuk bahu perlahan dan panggil dengan suara lantang. Jika tidak merespon, segera panggil bantuan 119.",
                "Tengadahkan kepala, topang dagu (head-tilt chin-lift). Dekatkan pipi ke hidung untuk merasakan hembusan.",
                "Letakkan tumit tangan di tengah dada, kedalaman 5-6 cm. Kecepatan 100-120 kali kompresi per menit.",
                "Tutup hidung korban, tiupkan napas hingga dada mengembang. Lakukan hingga korban bernapas atau ambulans tiba."
            )
        ),
        mapOf(
            "title" to "Pendarahan Hebat",
            "desc" to "Hentikan darah dengan tekanan langsung",
            "kategori" to "Pertolongan Pertama (P3K)",
            "badge" to "TEKAN LANGSUNG",
            "nomor" to "119",
            "langkahJudul" to arrayOf(
                "Lindungi Diri",
                "Tekan Luka dengan Kuat",
                "Tambah Lapisan Bila Tembus",
                "Baringkan & Panggil 119"
            ),
            "langkahIsi" to arrayOf(
                "Gunakan sarung tangan atau plastik bersih sebelum menyentuh darah korban.",
                "Tekan langsung luka memakai kain bersih atau kassa steril tanpa dilepas-lepas.",
                "Jangan angkat kain pertama. Tambahkan kain baru di atasnya dan terus tekan.",
                "Baringkan korban, jaga tetap hangat, dan hubungi ambulans bila darah tidak berhenti."
            )
        ),
        mapOf(
            "title" to "Luka Bakar",
            "desc" to "Dinginkan luka dengan air mengalir",
            "kategori" to "Pertolongan Pertama (P3K)",
            "badge" to "AIR MENGALIR 20 MENIT",
            "nomor" to "119",
            "langkahJudul" to arrayOf(
                "Jauhkan dari Sumber Panas",
                "Dinginkan 20 Menit",
                "Lepas Perhiasan",
                "Tutup dengan Longgar"
            ),
            "langkahIsi" to arrayOf(
                "Pastikan api atau sumber panas sudah padam dan lokasi aman.",
                "Alirkan air bersih bersuhu biasa ke luka selama 20 menit. Jangan memakai es.",
                "Lepaskan cincin, jam, atau pakaian ketat di sekitar luka sebelum membengkak.",
                "Tutup dengan plastik wrap atau kain bersih. Jangan oles pasta gigi, mentega, atau kecap."
            )
        ),
        mapOf(
            "title" to "Tersedak",
            "desc" to "Tepukan punggung & hentakan perut",
            "kategori" to "Pertolongan Pertama (P3K)",
            "badge" to "5 TEPUKAN + 5 HENTAKAN",
            "nomor" to "119",
            "langkahJudul" to arrayOf(
                "Pastikan Korban Tersedak",
                "5 Tepukan Punggung",
                "5 Hentakan Perut",
                "Ulangi atau Mulai CPR"
            ),
            "langkahIsi" to arrayOf(
                "Tanya \"Apakah kamu tersedak?\". Jika korban masih bisa batuk, dorong untuk terus batuk.",
                "Condongkan badan korban ke depan, tepuk keras di antara tulang belikat sebanyak 5 kali.",
                "Berdiri di belakang korban, kepalkan tangan di atas pusar, hentakkan ke dalam dan ke atas.",
                "Ulangi sampai benda keluar. Jika korban tidak sadar, panggil 119 dan mulai CPR."
            )
        ),
        mapOf(
            "title" to "Patah Tulang",
            "desc" to "Imobilisasi tanpa meluruskan tulang",
            "kategori" to "Pertolongan Pertama (P3K)",
            "badge" to "JANGAN DIGERAKKAN",
            "nomor" to "119",
            "langkahJudul" to arrayOf(
                "Jangan Gerakkan Bagian Cedera",
                "Pasang Bidai",
                "Kompres Dingin",
                "Bawa ke Fasilitas Kesehatan"
            ),
            "langkahIsi" to arrayOf(
                "Biarkan posisi tulang seperti saat ditemukan, jangan mencoba meluruskannya.",
                "Ikat bidai (papan atau kardus) melewati sendi di atas dan di bawah bagian yang patah.",
                "Kompres dengan es yang dibungkus kain untuk mengurangi bengkak dan nyeri.",
                "Hubungi 119, terutama bila tulang menembus kulit atau cedera di leher dan punggung."
            )
        ),
        mapOf(
            "title" to "Gempa Bumi",
            "desc" to "Merunduk, berlindung, bertahan",
            "kategori" to "Mitigasi Bencana",
            "badge" to "DROP • COVER • HOLD",
            "nomor" to "112",
            "langkahJudul" to arrayOf(
                "Merunduk",
                "Berlindung",
                "Bertahan",
                "Evakuasi ke Titik Kumpul"
            ),
            "langkahIsi" to arrayOf(
                "Segera turunkan badan ke lantai agar tidak terjatuh saat guncangan.",
                "Masuk ke bawah meja yang kokoh dan lindungi kepala serta leher dengan tangan.",
                "Pegang kaki meja sampai guncangan berhenti. Jauhi kaca, lemari, dan jendela.",
                "Setelah guncangan berhenti, keluar lewat tangga menuju area terbuka. Waspadai gempa susulan."
            )
        ),
        mapOf(
            "title" to "Kebakaran Gedung",
            "desc" to "Evakuasi lewat tangga darurat",
            "kategori" to "Mitigasi Bencana",
            "badge" to "JANGAN PAKAI LIFT",
            "nomor" to "113",
            "langkahJudul" to arrayOf(
                "Beri Peringatan",
                "Gunakan Tangga Darurat",
                "Merangkak di Bawah Asap",
                "Kumpul & Hubungi 113"
            ),
            "langkahIsi" to arrayOf(
                "Teriakkan \"Kebakaran!\" dan bunyikan alarm agar orang lain segera keluar.",
                "Jangan memakai lift. Ikuti tanda jalur evakuasi menuju pintu keluar.",
                "Asap naik ke atas. Merangkak rendah dan tutup hidung dengan kain basah.",
                "Tuju titik kumpul, jangan kembali ke dalam gedung, lalu hubungi pemadam kebakaran."
            )
        ),
        mapOf(
            "title" to "Banjir",
            "desc" to "Amankan listrik dan pindah ke tempat tinggi",
            "kategori" to "Mitigasi Bencana",
            "badge" to "MATIKAN LISTRIK",
            "nomor" to "115",
            "langkahJudul" to arrayOf(
                "Matikan Listrik & Gas",
                "Amankan Dokumen Penting",
                "Pindah ke Tempat Tinggi",
                "Hubungi 115 Bila Terjebak"
            ),
            "langkahIsi" to arrayOf(
                "Turunkan MCB dan tutup gas sebelum air masuk ke dalam rumah.",
                "Simpan dokumen dan barang berharga di wadah kedap air di tempat yang tinggi.",
                "Jangan menyeberangi arus. Arus setinggi 15 cm sudah bisa menjatuhkan orang dewasa.",
                "Hubungi Basarnas dan beri tanda keberadaan Anda dengan kain terang atau senter."
            )
        )
    )

    // Daftar aktif sesuai filter ChipGroup
    private var daftarAktif: List<Map<String, Any>> = dataListWithDesc

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPanduanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.pand_title)
        (requireActivity() as AppCompatActivity).supportActionBar?.subtitle = getString(R.string.pand_subtitle)

        // Option menu di Toolbar: ikon Info membuka Tutorial (Modul P9 5.4)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.panduan_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_tutorial -> {
                        val intent = Intent(requireContext(), TutorialActivity::class.java)
                        intent.putExtra(TutorialActivity.EXTRA_DARI_MENU, true)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Tampilkan semua panduan saat pertama dibuka
        tampilkanDaftarSop(null)

        // Filter daftar SOP berdasarkan ChipGroup kategori (Modul P9 2.2)
        binding.chipGroupKategori.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChipId = checkedIds.firstOrNull() // ambil id chip yang dipilih
            if (selectedChipId != null) {
                val chip = group.findViewById<Chip>(selectedChipId)
                Toast.makeText(requireContext(), "Filter: ${chip.text}", Toast.LENGTH_SHORT).show()
            }
            val kategori = when (selectedChipId) {
                R.id.chipP3k -> "Pertolongan Pertama (P3K)"
                R.id.chipBencana -> "Mitigasi Bencana"
                else -> null
            }
            tampilkanDaftarSop(kategori)
        }

        // Klik item -> buka SopDetailActivity dengan putExtra
        binding.listViewSop.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = daftarAktif[position]

            val intent = Intent(requireContext(), SopDetailActivity::class.java)
            intent.putExtra("EXTRA_JUDUL", selectedItem["title"] as String)
            intent.putExtra("EXTRA_DESKRIPSI", selectedItem["desc"] as String)
            intent.putExtra("EXTRA_KATEGORI", selectedItem["kategori"] as String)
            intent.putExtra("EXTRA_BADGE", selectedItem["badge"] as String)
            intent.putExtra("EXTRA_NOMOR", selectedItem["nomor"] as String)
            intent.putExtra("EXTRA_LANGKAH_JUDUL", selectedItem["langkahJudul"] as Array<String>)
            intent.putExtra("EXTRA_LANGKAH_ISI", selectedItem["langkahIsi"] as Array<String>)
            startActivity(intent)
        }
    }

    // Perbarui SimpleAdapter & jumlah panduan sesuai kategori terpilih
    private fun tampilkanDaftarSop(kategoriFilter: String?) {
        daftarAktif = if (kategoriFilter == null) {
            dataListWithDesc
        } else {
            dataListWithDesc.filter { it["kategori"] == kategoriFilter }
        }
        binding.tvJumlahSop.text = getString(R.string.pand_count, daftarAktif.size)

        // SimpleAdapter: judul, deskripsi, kategori
        val adapter = SimpleAdapter(
            requireContext(),
            daftarAktif,
            R.layout.item_sop,
            arrayOf("title", "desc", "kategori"), // key yang diambil dari data
            intArrayOf(android.R.id.text1, android.R.id.text2, R.id.tvKategoriSop) // tempat tampilnya
        )
        binding.listViewSop.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
