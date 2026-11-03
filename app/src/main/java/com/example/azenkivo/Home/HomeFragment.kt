package com.example.azenkivo.Home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import androidx.fragment.app.Fragment
import com.example.azenkivo.MainActivity
import com.example.azenkivo.DetailActivity
import com.example.azenkivo.R
import com.example.azenkivo.WebViewActivity
import com.example.azenkivo.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Status tombol SOS (tahan 2 detik)
    private var sosTerkirim = false
    private val selesaiSos = Runnable { selesaiTahanSos() }

    // Data hotline untuk dialog "Semua"
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
        // Judul bawaan disembunyikan, sapaan sudah ada di Toolbar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        // Sapaan dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.home_default_name)
        binding.tvGreeting.text = getString(R.string.home_greeting, username)
        // Avatar: huruf pertama nama pengguna
        binding.tvAvatar.text = username.first().uppercaseChar().toString()
        if (username == "Tamu") {
            binding.tvStatus.setText(R.string.home_status_guest)
        }

        // Baris hotline -> buka aplikasi telepon
        pasangHotline(binding.card112, "112", R.string.home_112_label)
        pasangHotline(binding.card119, "119", R.string.home_119_label)
        pasangHotline(binding.card113, "113", R.string.home_113_label)
        pasangHotline(binding.card115, "115", R.string.home_115_label)

        binding.tvSemuaHotline.setOnClickListener { tampilkanSemuaHotline() }

        pasangTombolSos()

        // Kartu status -> halaman Info BMKG
        binding.bannerGempa.setOnClickListener {
            startActivity(Intent(requireContext(), WebViewActivity::class.java))
        }

        // Banner SOP -> pindah ke tab Panduan
        binding.btnBukaSop.setOnClickListener {
            (requireActivity() as MainActivity).bukaTab(R.id.panduan)
        }
        binding.cardBannerSop.setOnClickListener {
            (requireActivity() as MainActivity).bukaTab(R.id.panduan)
        }

        // Syarat tugas: Intent dari halaman utama ke DetailActivity
        binding.btnToDetail.setOnClickListener {
            startActivity(Intent(requireContext(), DetailActivity::class.java))
        }

    }

    // Klik baris -> dialer
    private fun pasangHotline(baris: View, nomor: String, @StringRes labelRes: Int) {
        baris.contentDescription = getString(R.string.home_call_desc, nomor, getString(labelRes))
        baris.setOnClickListener { bukaDialer(nomor) }
    }

    // Intent ACTION_DIAL: membuka aplikasi telepon dengan nomor terisi
    private fun bukaDialer(nomor: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$nomor")
        startActivity(intent)
    }

    private fun tampilkanSemuaHotline() {
        // ArrayAdapter + simple_list_item_1 ditampilkan di dalam dialog
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            daftarHotline
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.home_dialog_hotline_title)
            .setAdapter(adapter) { dialog, position ->
                bukaDialer(daftarNomor[position])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.home_dialog_close) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun tampilkanDialogSos() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.home_dialog_sos_title)
            .setMessage(R.string.home_dialog_sos_message)
            .setIcon(R.drawable.ic_app_mark)
            .setPositiveButton(R.string.home_dialog_sos_send) { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), R.string.home_sos_sent, Toast.LENGTH_SHORT).show()
                (requireActivity() as MainActivity).bukaTab(R.id.tracking)
            }
            .setNegativeButton(R.string.home_dialog_sos_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Tombol SOS: tahan 2 detik untuk kirim, lepas sebelum penuh = batal
    @SuppressLint("ClickableViewAccessibility")
    private fun pasangTombolSos() {
        val tombol = binding.btnSos
        tombol.clipToOutline = true
        binding.sosFill.pivotX = 0f

        tombol.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // Jangan biarkan ScrollView mengambil alih saat tombol ditahan
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    mulaiTahanSos()
                }
                MotionEvent.ACTION_UP -> {
                    val terkirim = sosTerkirim
                    batalTahanSos()
                    if (!terkirim) v.performClick()
                }
                MotionEvent.ACTION_CANCEL -> batalTahanSos()
            }
            true
        }

        // Tap biasa: beri petunjuk
        tombol.setOnClickListener {
            Toast.makeText(requireContext(), R.string.home_sos_hint, Toast.LENGTH_SHORT).show()
        }

        // TalkBack (ketuk dua kali & tahan) langsung ke dialog SOS
        tombol.setOnLongClickListener {
            tampilkanDialogSos()
            true
        }
        ViewCompat.replaceAccessibilityAction(
            tombol,
            AccessibilityActionCompat.ACTION_LONG_CLICK,
            getString(R.string.home_sos_action),
            null
        )
    }

    private fun mulaiTahanSos() {
        sosTerkirim = false
        binding.btnSos.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        binding.sosFill.animate().cancel()
        binding.sosFill.animate()
            .scaleX(1f)
            .setDuration(DURASI_TAHAN_SOS)
            .setInterpolator(LinearInterpolator())
            .start()
        binding.btnSos.postDelayed(selesaiSos, DURASI_TAHAN_SOS)
    }

    private fun batalTahanSos() {
        val b = _binding ?: return
        b.btnSos.removeCallbacks(selesaiSos)
        b.sosFill.animate().cancel()
        b.sosFill.animate().scaleX(0f).setDuration(200).start()
    }

    private fun selesaiTahanSos() {
        val b = _binding ?: return
        sosTerkirim = true
        b.btnSos.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        b.sosFill.animate().cancel()
        b.sosFill.scaleX = 0f
        tampilkanDialogSos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnSos.removeCallbacks(selesaiSos)
        _binding = null
    }

    private companion object {
        const val DURASI_TAHAN_SOS = 2000L
    }
}
