package br.edu.infnet.itrip.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.infnet.itrip.Model.Trip
import br.edu.infnet.itrip.R
import br.edu.infnet.itrip.ViewModel.TripViewModel
import br.edu.infnet.luis_barbosa_dr4_at.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_my_trips.*

class MyTripsFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var tripViewModel: TripViewModel
    private lateinit var mAdapter: TripAdapter
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private lateinit var auth: FirebaseAuth
    private val TAG = "MyTripsFragment"
    private lateinit var rv_trips: RecyclerView
    private lateinit var tv_empty: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firestoreDB = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_my_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { act ->
            userViewModel = ViewModelProviders.of(act)
            .get(UserViewModel::class.java)
            tripViewModel = ViewModelProviders.of(act)
            .get(TripViewModel::class.java)
        }
        tripViewModel.tripVM.value = null

        loadTripList()
        fillUserData()
        setupListeners()
        getTripsListener()
        setUpWidgets()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater!!.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_profile -> {
                findNavController().navigate(R.id.action_myTripsFragment_to_profileFragment, null)
            }
            R.id.item_signOut -> {
                auth.signOut()
                findNavController().navigate(R.id.action_myTripsFragment_to_signInFragment, null)
            }
        }
        return false
    }

    private fun setupRecyclerView() {
        rv_trips.layoutManager = LinearLayoutManager(activity)
        rv_trips.itemAnimator = DefaultItemAnimator()
        rv_trips.adapter = mAdapter
    }

    private fun setupListeners() {
        fab_add_trip.setOnClickListener {
            findNavController().navigate(R.id.action_myTripsFragment_to_addTripFragment, null)
        }
    }

    private fun fillUserData() {
        userViewModel.name.observe(viewLifecycleOwner, Observer {
            if(it != null){
                tv_nome_user.text = it.toString()
            }
        })
        userViewModel.email.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                tv_email_user.text = it.toString()
            }
        })
        userViewModel.currentDate.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                tv_data_user.text = it.toString()
            }
        })
        tripViewModel.tripVM.value = null
    }


    private fun partItemClicked(trip: Trip) {

        tripViewModel.tripVM.value = null
        tripViewModel.tripVM.value = trip
        findNavController().navigate(R.id.action_myTripsFragment_to_detailsTripFragment, null)

    }

    private fun updateTripFirestore(trip: Trip) {
        trip.id?.let {
            firestoreDB!!.collection("Users").document(auth.currentUser!!.uid)
                .collection("Trips")
                .document(it)
                .set(trip.toMap())
                .addOnSuccessListener {
                    Log.e(TAG, "Trip document update successful!")
                    Toast.makeText(context, "Travel has been updated!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding Trip document", e)
                    Toast.makeText(context, "Travel could not be updated!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadTripList() {
        pb_my_trips.visibility = View.VISIBLE
        firestoreDB!!.collection("Users").document(auth.currentUser!!.uid)
            .collection("Trips")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tripList = mutableListOf<Trip>()
                    for (doc in task.result!!) {
                        val result = doc.toObject(Trip::class.java)
                        val trip = Trip(
                            doc.id,
                            result.countryTrip,
                            result.dateTrip,
                            result.photoTrip,
                            result.descriptionTrip,
                            result.ratingTrip
                        )
                        updateTripFirestore(trip)
                        tripList.add(trip)
                    }

                    mAdapter = TripAdapter(tripList) {
                            partItem: Trip -> partItemClicked(partItem)
                    }

                    setupRecyclerView()
                    pb_my_trips.visibility = View.GONE

                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    private fun getTripsListener() {
        val tripsCollection = firestoreDB!!.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("Trips")

            firestoreListener = tripsCollection
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    return@EventListener
                }

                val tripList = mutableListOf<Trip>()
                for (doc in documentSnapshots!!) {
                    val result = doc.toObject(Trip::class.java)
                    val trip = Trip(
                        doc.id,
                        result.countryTrip,
                        result.dateTrip,
                        result.photoTrip,
                        result.descriptionTrip,
                        result.ratingTrip
                    )
                    updateTripFirestore(trip)
                    tripList.add(trip)
                }

                mAdapter = TripAdapter(tripList) {
                        partItem: Trip -> partItemClicked(partItem)
                }

                rv_trips.adapter = mAdapter

                if (tripList.isNullOrEmpty()) {
                    tv_empty.visibility = View.VISIBLE
                } else {
                    tv_empty.visibility = View.GONE
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener!!.remove()
    }

    private fun setUpWidgets() {
        rv_trips = requireView().findViewById(R.id.rv_list_trip)
        tv_empty = requireView().findViewById(R.id.tv_empty_list)
        activity?.toolbar_layout!!.visibility = View.VISIBLE
    }
}