package com.example.azenkivo.Tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.azenkivo.databinding.FragmentTutorial1Binding

// Fragment 1 Tutorial: Sinyal SOS & Hotline Satu Sentuhan
class Tutorial1Fragment : Fragment() {

    private var _binding: FragmentTutorial1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorial1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
