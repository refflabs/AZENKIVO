package com.example.azenkivo.Profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.azenkivo.MainActivity
import com.example.azenkivo.R
import com.example.azenkivo.databinding.DialogInputBinding
import com.example.azenkivo.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Data profil disimpan di "user_pref"
    private val pref: SharedPreferences
        get() = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    private val pilihanGoldar = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Tidak tahu")

    // Permintaan izin lokasi
    private val mintaIzinLokasi = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { hasil ->
        val diizinkan = hasil.values.any { it }
        if (!diizinkan && _binding != null) {
            Toast.makeText(requireContext(), R.string.prof_gps_denied, Toast.LENGTH_LONG).show()
        }
        perbaruiStatusGps()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.prof_title)
        (requireActivity() as AppCompatActivity).supportActionBar?.subtitle = null

        // Header: nama dari SharedPreferences
        val username = pref.getString("username", null)
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.home_default_name)
        binding.tvNama.text = username
        binding.tvAvatar.text = username.first().uppercaseChar().toString()
        binding.tvPeran.setText(
            if (username == "Tamu") R.string.prof_role_guest else R.string.prof_role_volunteer
        )

        // Identitas medis & darurat
        tampilkanNilai(binding.tvGoldar, binding.rowGoldar, KEY_GOLDAR, R.string.prof_blood_label)
        tampilkanNilai(binding.tvKontak, binding.rowKontak, KEY_KONTAK, R.string.prof_contact_label)
        tampilkanNilai(binding.tvCatatan, binding.rowCatatan, KEY_CATATAN, R.string.prof_notes_label)

        binding.rowGoldar.setOnClickListener { pilihGolonganDarah() }
        binding.rowKontak.setOnClickListener {
            editTeks(
                KEY_KONTAK, R.string.prof_contact_label, R.string.prof_contact_hint,
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                binding.tvKontak, binding.rowKontak
            )
        }
        binding.rowCatatan.setOnClickListener {
            editTeks(
                KEY_CATATAN, R.string.prof_notes_label, R.string.prof_notes_hint,
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
                binding.tvCatatan, binding.rowCatatan
            )
        }

        // Pengaturan
        binding.switchNotif.isChecked = pref.getBoolean(KEY_NOTIF, true)
        binding.switchNotif.setOnCheckedChangeListener { _, aktif ->
            pref.edit { putBoolean(KEY_NOTIF, aktif) }
        }

        // Switch GPS mencerminkan izin sistem yang sebenarnya
        binding.switchGps.setOnClickListener {
            val sudahDiizinkan = punyaIzinLokasi()
            perbaruiStatusGps()
            if (sudahDiizinkan) {
                tawarkanBukaPengaturan()
            } else {
                mintaIzinLokasi.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        // Logout: memakai AlertDialog milik MainActivity
        binding.btnLogout.setOnClickListener {
            (requireActivity() as MainActivity).showLogoutDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Status izin bisa berubah setelah kembali dari Pengaturan sistem
        perbaruiStatusGps()
    }

    // Identitas medis

    private fun tampilkanNilai(tv: TextView, baris: View, key: String, @StringRes labelRes: Int) {
        val nilai = pref.getString(key, null)?.takeIf { it.isNotBlank() }
        val teks = nilai ?: getString(R.string.prof_not_set)
        tv.text = teks
        tv.setTextColor(
            ContextCompat.getColor(requireContext(), if (nilai == null) R.color.ink_secondary else R.color.ink)
        )
        baris.contentDescription = getString(R.string.prof_edit_desc, getString(labelRes), teks)
    }

    private fun pilihGolonganDarah() {
        val sekarang = pilihanGoldar.indexOf(pref.getString(KEY_GOLDAR, null))
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.prof_blood_label)
            .setSingleChoiceItems(pilihanGoldar, sekarang) { dialog, posisi ->
                pref.edit { putString(KEY_GOLDAR, pilihanGoldar[posisi]) }
                tampilkanNilai(binding.tvGoldar, binding.rowGoldar, KEY_GOLDAR, R.string.prof_blood_label)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.prof_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun editTeks(
        key: String,
        @StringRes judulRes: Int,
        @StringRes hintRes: Int,
        tipeInput: Int,
        tv: TextView,
        baris: View
    ) {
        val dialogBinding = DialogInputBinding.inflate(layoutInflater)
        dialogBinding.tilInput.hint = getString(hintRes)
        dialogBinding.etInput.inputType = tipeInput
        dialogBinding.etInput.setText(pref.getString(key, ""))
        dialogBinding.etInput.setSelection(dialogBinding.etInput.text?.length ?: 0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(judulRes)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.prof_save) { dialog, _ ->
                val isi = dialogBinding.etInput.text?.toString()?.trim().orEmpty()
                pref.edit { putString(key, isi) }
                tampilkanNilai(tv, baris, key, judulRes)
                Toast.makeText(requireContext(), R.string.prof_saved, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNeutralButton(R.string.prof_clear) { dialog, _ ->
                pref.edit { remove(key) }
                tampilkanNilai(tv, baris, key, judulRes)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.prof_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
        dialogBinding.etInput.requestFocus()
    }

    // Izin lokasi

    private fun punyaIzinLokasi(): Boolean {
        val ctx = requireContext()
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun perbaruiStatusGps() {
        val b = _binding ?: return
        val aktif = punyaIzinLokasi()
        b.switchGps.isChecked = aktif
        b.tvGpsStatus.setText(if (aktif) R.string.prof_gps_on else R.string.prof_gps_off)
    }

    // Izin tidak bisa dicabut lewat kode
    private fun tawarkanBukaPengaturan() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.prof_gps_settings_title)
            .setMessage(R.string.prof_gps_settings_message)
            .setPositiveButton(R.string.prof_gps_open_settings) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                startActivity(intent)
            }
            .setNegativeButton(R.string.prof_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val KEY_GOLDAR = "goldar"
        const val KEY_KONTAK = "kontak_darurat"
        const val KEY_CATATAN = "catatan_medis"
        const val KEY_NOTIF = "notif_siaga"
    }
}
