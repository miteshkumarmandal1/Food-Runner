package com.mitesh.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mitesh.foodrunner.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var profile_name: TextView
    lateinit var profile_phone: TextView
    lateinit var profile_email: TextView
    lateinit var profile_address: TextView
    lateinit var profile_id:TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_app_profile, container, false)
        profile_name = view.findViewById(R.id.profile_name)
        profile_phone = view.findViewById(R.id.profile_phone)
        profile_email = view.findViewById(R.id.profile_email)
        profile_address = view.findViewById(R.id.profile_address)
        profile_id=view.findViewById(R.id.profile_id)
        sharedPreferences =
            requireActivity().getSharedPreferences(
                getString(R.string.preference_file_name),
                Context.MODE_PRIVATE
            )
        profile_name.text=sharedPreferences.getString("Name","No Data")
        profile_phone.text=sharedPreferences.getString("Mobile","No Data")
        profile_email.text=sharedPreferences.getString("Email","No Data")
        profile_address.text=sharedPreferences.getString("Address","No Data")
        profile_id.text=sharedPreferences.getString("user_id","No Data")


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}