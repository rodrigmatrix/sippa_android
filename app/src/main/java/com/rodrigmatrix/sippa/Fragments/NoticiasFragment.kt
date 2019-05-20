package com.rodrigmatrix.sippa


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class NoticiasFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_noticias, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            NoticiasFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
