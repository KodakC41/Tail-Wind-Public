package com.cbruinsm.tailwindtwo.History

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight.Flight
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.fixes
import com.cbruinsm.tailwindtwo.R
import com.cbruinsm.tailwindtwo.databinding.FragmentFlightHistoryBinding
import com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels.flightsDataBase
import com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels.trackViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class FlightHistory : DialogFragment() {
    private val Rv = RecyclerViewUpdate()
    private var _binding: FragmentFlightHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: flightsDataBase  by activityViewModels()
    private val viewModel2 : trackViewModel  by activityViewModels()
    private val newPoint : fixes = fixes("",0.0,0.0,"","")
    private lateinit var fusedLocationClient: FusedLocationProviderClient //For location.
    private var longitude = 0.0
    private var latitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
        _binding = FragmentFlightHistoryBinding.inflate(inflater, container, false)
        binding.apply {
            FriendsView.run {
                layoutManager = LinearLayoutManager(context)
                adapter = Rv
            }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.forRecyclerView.observe(viewLifecycleOwner) {
            Rv.updateItems(it)
        }
        """
            Modeled after Professor Roolfs Swipe to delete method
        """.trimIndent()
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val item = Rv.getFriendAtPosition(viewHolder.adapterPosition)
                    DELETIONWARNING(item)
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.FriendsView)



        val itemTouchHelperCallback2 =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                @SuppressLint("NotifyDataSetChanged")
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val item = Rv.getFriendAtPosition(viewHolder.adapterPosition)
                    Rv.notifyDataSetChanged()
                    binding.apply {
                        FriendsView.isVisible = false
                    }
                    INPUT(item)
                    viewModel2._route.value = item.name
                }
            }
        val itemTouchHelper2 = ItemTouchHelper(itemTouchHelperCallback2)
        itemTouchHelper2.attachToRecyclerView(binding.FriendsView)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FlightHistory().apply {
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun DELETIONWARNING(flights: Flight) {
        val msg = resources.getString(R.string.Please)
        val builder = AlertDialog.Builder(context,R.style.round)
        with(builder) {
            setTitle(R.string.Are_you_sure)
            setMessage(msg)
            setPositiveButton(R.string.Confirm) { _, _ ->
                viewModel.deleteFlight(flights)
            }
            setNegativeButton(R.string.No) { _, _ ->
                Rv.notifyDataSetChanged()
            }
            val winDow = builder.create()
            winDow.window?.setLayout(1000,600)
            winDow.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.point))
            winDow.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun INPUT(flights: Flight) {
        val input = EditText(context)
        val input2 = Spinner(context)
        val msg = resources.getString(R.string.Enter_Comment_below)
        input.width = 500
        val builder = AlertDialog.Builder(context,R.style.round)
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Pronouns,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                input2.adapter = adapter
            }
        }
        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        val LL = LinearLayout(context)
        LL.addView(input)
        LL.addView(input2)
        with(builder) {
            setTitle(R.string.Add_comment)
            setMessage(msg)
            setView(LL)
            input.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_TEXT)
            setPositiveButton(R.string.Add_Point) { _, _ ->
                newPoint.name = "Point of Interest"
                current()
                newPoint.longitude = longitude
                newPoint.latitude = latitude
                newPoint.type = input2.selectedItem.toString()
                newPoint.description = input.text.toString()
            flights.name.add(newPoint) //update after new trackpoint is added.
                viewModel.updateFlight(flights)
                binding.apply {
                    FriendsView.isVisible = true
                }
            }
            setNegativeButton(R.string.No) { _, _ ->
                Rv.notifyDataSetChanged()
                binding.apply {
                    FriendsView.isVisible = true
                }
            }
            val winDow = builder.create()
            winDow.window?.setLayout(1000,600)
            winDow.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.point))
            winDow.show()
        }
    }
    fun current() {
        if (ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener{ location : Location ->
                latitude = location.latitude
                longitude = location.longitude
            }
    }
}