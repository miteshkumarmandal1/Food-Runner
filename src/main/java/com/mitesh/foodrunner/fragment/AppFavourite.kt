package com.mitesh.foodrunner.fragment

import android.content.Context
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mitesh.foodrunner.FoodDetail
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.adapter.HomeRecyclerAdapter
import com.mitesh.foodrunner.database.RestaurantDatabase
import com.mitesh.foodrunner.database.RestaurantEntity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppFavourite.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppFavourite : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var favResList: List<RestaurantEntity>

    lateinit var recyclerView_fav: RecyclerView
    lateinit var layoutManager_fav: RecyclerView.LayoutManager
    lateinit var recyclerAdapter_fav: HomeRecyclerAdapter
    lateinit var progresslayout_fav: RelativeLayout
    lateinit var progressbar_fav: ProgressBar
    lateinit var no_fav_layout:RelativeLayout

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
        val view= inflater.inflate(R.layout.fragment_app_favourite, container, false)
        recyclerView_fav=view.findViewById(R.id.recylerView_fav)
        progressbar_fav=view.findViewById(R.id.progress_bar_fav)
        progresslayout_fav=view.findViewById(R.id.progress_layout_fav)
        no_fav_layout=view.findViewById(R.id.no_fav_layout)
        layoutManager_fav=LinearLayoutManager(activity)

        no_fav_layout.visibility=View.GONE
        progresslayout_fav.visibility=View.GONE

       favResList=DbFavAccess(activity as Context).execute().get()
        if(!favResList.isEmpty()){
            val foodDetail= arrayListOf<FoodDetail>()
            for(i in favResList.indices){
                val foodEntity=FoodDetail(
                    favResList[i].id,
                    favResList[i].name,
                    favResList[i].rating,
                    favResList[i].cost_for_one,
                    favResList[i].image_url,
                    true

                )
                foodDetail.add(foodEntity)
            }

            recyclerAdapter_fav= HomeRecyclerAdapter(activity as Context,foodDetail)
            recyclerView_fav.adapter=recyclerAdapter_fav
            recyclerView_fav.layoutManager=layoutManager_fav

            recyclerView_fav.addItemDecoration(
                DividerItemDecoration(
                    recyclerView_fav.context,
                    (layoutManager_fav as LinearLayoutManager).orientation
                )
            )
        }else{
              no_fav_layout.visibility=View.VISIBLE
        }


        return  view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppFavourite.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppFavourite().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
  class DbFavAccess(val context: Context):AsyncTask<Void,Void,List<RestaurantEntity>>() {
      val myDb= Room.databaseBuilder(context,RestaurantDatabase::class.java,"Restaurant_Database").build()
      override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {

          val list= myDb.restaurantDao().getAllRes()
          myDb.close()
          return list
      }

  }
}