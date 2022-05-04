package com.cbruinsm.tailwindtwo.menu

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cbruinsm.tailwindtwo.BuildConfig
import com.cbruinsm.tailwindtwo.R
import com.cbruinsm.tailwindtwo.databinding.FragmentInformationBinding
import java.lang.StringBuilder

/**
 * Information
 * Provides information about the application.
 */
class Information : DialogFragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        binding.apply {
            Version.text = StringBuilder(getString(R.string.V)+" "+BuildConfig.VERSION_NAME)
            dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}