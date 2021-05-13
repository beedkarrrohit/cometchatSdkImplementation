package com.example.cometchatprotask.cometchatactivities.bottomSheetFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AttachBottomSheet : BottomSheetDialogFragment(),View.OnClickListener {
    lateinit var bottomSheetListener : BottomSheetListener
    lateinit var binding : LayoutBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.layout_bottom_sheet,container,false)
        binding = LayoutBottomSheetBinding.bind(view)
        binding.apply {
            sendImage.setOnClickListener(this@AttachBottomSheet)
        }
        return view
    }

    interface BottomSheetListener{
        fun onButtonClicked(id : Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            bottomSheetListener = context as BottomSheetListener
        }catch (e: Exception){
            throw ClassCastException(context.toString()+"must Implement BottomsheetListener")
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_image-> {
                bottomSheetListener.onButtonClicked(p0.id)
                dialog?.cancel()
            }
        }
    }
}