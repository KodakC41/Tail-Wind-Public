package com.cbruinsm.tailwindtwo.ui.main.plane

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils.substring
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.lifecycle.ViewModelProvider
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.FlightResponse
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.fixes as Q
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Data.flights as flights
import com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight.Flight as flights2
import com.cbruinsm.tailwindtwo.R
import com.cbruinsm.tailwindtwo.TailWindApp.Companion.YOUR_NAME
import com.cbruinsm.tailwindtwo.databinding.FragmentFlightSearchBinding
import com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels.*
import java.lang.StringBuilder
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

/**
 * This fragment is in charge of getting the flight data from the API it uses two viewmodels
 * It can take quite a bit of time to communicate with the API
 * The first query is done in the text field @param ident, the next is done using the recyclerView @param fa_flight_id.
 * The data then is persisted between this fragment and the maps fragment which will then plot the flight path.
 */
class FlightSearch : AppCompatDialogFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    var Flight = "FLIGHTSEARCH"
    private var _binding: FragmentFlightSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var FLIGHT : planesViewModel // Flight ViewModel
    private lateinit var ROUTE : trackViewModel // Flight Path ViewModel
    private lateinit var TRACKER : trackerViewModel //Flight Tracker ViewModel (the expensive one)
    private var newFlights = flights2()
    private var FR : FlightResponse = FlightResponse()
    private val viewModel: flightsDataBase by activityViewModels()
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFlightSearchBinding.inflate(inflater, container, false)
        binding.apply {
            //init
            FLIGHT = ViewModelProvider(requireActivity())[planesViewModel::class.java]
            ROUTE = ViewModelProvider(requireActivity())[trackViewModel::class.java]
            TRACKER = ViewModelProvider(requireActivity())[trackerViewModel::class.java]

            //observe
            FLIGHT.flightX.observe(viewLifecycleOwner) {
                Flights.adapter = FlightAdapter(it)
                val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ) = false

                        @SuppressLint("NotifyDataSetChanged")
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val flight = FlightAdapter(it).getFlightAtPosition(viewHolder.adapterPosition)
                            TRACKER.getPlanes(flight.fa_flight_id)
                            FlightAdapter(it).notifyDataSetChanged()
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (TRACKER.success) {
                                    Success()
                                    flightsFiller(flight,FR)
                                    viewModel.addFlight(newFlights)
                                } else {
                                    Failure()
                                }
                            },1000)
                        }
                    }
                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(Flights)

                val itemTouchHelperCallback2 = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ) = false

                        @SuppressLint("NotifyDataSetChanged")
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val flight = FlightAdapter(it).getFlightAtPosition(viewHolder.adapterPosition)
                            FlightAdapter(it).notifyDataSetChanged()
                            ROUTE.getPos(flight.fa_flight_id)
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (ROUTE.success) {
                                    Success()
                                    for(i in 0 until ROUTE.route.value?.size!!) {
                                        FR.add(ROUTE.route.value!![i])
                                    }
                                    flightsFiller(flight,FR)
                                    viewModel.addFlight(newFlights)

                                } else {
                                    Failure()
                                }
                            },1000)

                        }
                    }
                val itemTouchHelper2 = ItemTouchHelper(itemTouchHelperCallback2)
                itemTouchHelper2.attachToRecyclerView(Flights)
            }
            Flights.layoutManager = LinearLayoutManager(context)




            //query
            FlightFinder.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    p0.let {
                        FLIGHT.getPlanes(p0.toString())
                    }
                }
            })
        }
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    //if query fails
    fun Failure() {
        var msg = resources.getString(R.string.Unable)
        val name = prefs.getString(YOUR_NAME,"your_name")
        msg = StringBuilder("Sorry $name, $msg").toString()
        val builder = AlertDialog.Builder(context,R.style.round)
        with(builder) {
            setTitle(R.string.flight_Alert)
            setMessage(msg)

        }
        val winDow = builder.create()
        winDow.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.point))
        winDow.show()
    }

    //if query succeeds
    fun Success() {
        val msg = resources.getString(R.string.Able)
        val builder = AlertDialog.Builder(context,R.style.round)
        with(builder) {
            setTitle(R.string.flight_Alert2)
            setMessage(msg)
        }
        val winDow = builder.create()
        winDow.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.point))
        winDow.show()
    }

    //ViewModel Adapter
    fun flightsFiller(flights : flights, flightResponse : FlightResponse) {
        newFlights.cancelled = flights.cancelled
        newFlights.destination = flights.destination.code
        newFlights.fa_flight_id = flights.fa_flight_id
        newFlights.gate_destination = flights.gate_destination
        newFlights.gate_origin = flights.gate_origin
        newFlights.ident = flights.ident
        newFlights.operator = flights.operator
        newFlights.operator_iata = flights.operator_iata
        newFlights.operator = flights.operator
        newFlights.origin = flights.origin.code
        newFlights.scheduled_out = flights.scheduled_out
        newFlights.name = flightResponse
    }
    @SuppressLint("ClickableViewAccessibility")
    private inner class FlightsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var flights: flights
        private var Plane: TextView = itemView.findViewById(R.id.FlightNum)
        private val Plane2: TextView = itemView.findViewById(R.id.Depart)
        private val Plane3: TextView = itemView.findViewById(R.id.Arrive)
        private val Plane4: TextView = itemView.findViewById(R.id.Time)

        /**
         * TODO(ADD THE ABILITY TO CHOSE BETWEEN LEFT AND RIGHT SWIPES)
         */
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
        }

        fun bind(flights: flights) {
            this.flights = flights
            Flight = flights.ident
            Plane.text = flights.ident
            Plane2.text = flights.origin.code
            Plane3.text = flights.destination.code
            Plane4.text = timeAdjust(flights.scheduled_out)
        }
        private fun timeAdjust(Time : String)  : String {
            val DATE_FORMAT =
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val time =
                LocalDateTime.parse(substring(Time, 0, Time.length), DateTimeFormatter.ofPattern(DATE_FORMAT))
            val zone =
                substring(time.atZone(ZoneId.of(TimeZone.getDefault().id)).toString(),11,22)
            var TimeX =
                LocalTime.of(Integer.parseInt(substring(zone,0,2)),Integer.parseInt(substring(zone,3,5)))

            when(zone.get(5)) {

                '-' -> TimeX = TimeX.minusHours(
                    Integer.parseInt(
                        substring(
                            zone,
                            7,
                            8
                        )
                    ).toLong())

                '+' -> TimeX = TimeX.plusHours(
                    Integer.parseInt(
                        substring(
                            zone,
                            7,
                            8
                        )
                    ).toLong())
            }

            val out = StringBuilder("Departs $TimeX local time")
            return out.toString()
        }
    }
    private inner class FlightAdapter(private val list : ArrayList<flights>) : RecyclerView.Adapter<FlightsViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightsViewHolder {
            val view = layoutInflater.inflate(R.layout.fragment_flights_card,parent, false)
            return FlightsViewHolder(view)
        }

        override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount() = list.size

        fun getFlightAtPosition(position: Int) = list[position]
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }

}