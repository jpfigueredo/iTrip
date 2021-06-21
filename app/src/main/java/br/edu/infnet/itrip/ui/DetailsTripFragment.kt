package br.edu.infnet.itrip.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.edu.infnet.itrip.Model.Trip
import br.edu.infnet.itrip.R
import br.edu.infnet.itrip.Retrofit.Country
import br.edu.infnet.itrip.Retrofit.Endpoint
import br.edu.infnet.itrip.Retrofit.NetworkUtils
import br.edu.infnet.itrip.ViewModel.TripViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details_trip.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsTripFragment : Fragment() {

    private var firestoreDB: FirebaseFirestore? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var tripViewModel: TripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        auth = FirebaseAuth.getInstance()
        firestoreDB = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_details_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { act ->
            tripViewModel = ViewModelProviders.of(act)
                .get(TripViewModel::class.java)
        }

        fillFieldsData()
        setUpListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater!!.inflate(R.menu.delete_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                val trip = tripViewModel.tripVM.value
                deleteTripFirebase(trip)
                findNavController().navigate(R.id.action_detailsTripFragment_to_myTripsFragment, null)
            }
        }
        return false
    }

    private fun setUpListener() {
        btn_update_details.setOnClickListener {
            findNavController().navigate(R.id.action_detailsTripFragment_to_addTripFragment, null)
        }
    }

    private fun deleteTripFirebase(trip: Trip?) {
        trip?.id.let {
            if (it != null) {
                firestoreDB!!.collection("Users").document(auth.currentUser!!.uid)
                    .collection("Trips")
                    .document(it)
                    .delete()
                    .addOnCompleteListener {
                        tripViewModel.tripVM.value = null
                        Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun getData(country: String) {
        val retrofitClient =
            NetworkUtils.getRetrofitInstance("https://restcountries.eu/rest/v2/")

        val endpoint = retrofitClient.create(Endpoint::class.java)
        val callback = endpoint.getCountry(country)

        callback.enqueue(object : Callback<List<Country>> {
            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Toast.makeText(context!!, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                response.body()?.forEach {

                    tv_capital_details.text = it.capital
                    tv_continent_details.text = it.subregion
                    tv_currency_details.text = it.currencies[0].name
                    tv_language_details.text = it.languages[0].name
                    tv_population_details.text = adjustPopulation(it.population)
                    tv_area_details.text = adjustArea(it.area)
                    loadFlag(it.flag, img_flag)
                    pb_details.visibility = View.GONE

                }
            }
        })

    }

    private fun fillFieldsData() {
        tripViewModel.tripVM.observe(viewLifecycleOwner, Observer {
            val trip = tripViewModel.tripVM.value
            if (trip != null) {
                tv_country_details.text = trip.countryTrip
                tv_date_details.text = trip.dateTrip
                tv_description_details.text = trip.descriptionTrip
                rb_details.rating = trip.ratingTrip.toFloat()
                tv_rating_details.text = trip.ratingTrip

                val bytarray: ByteArray = Base64.decode(trip.photoTrip, Base64.DEFAULT)
                val bmimage = BitmapFactory.decodeByteArray(
                    bytarray, 0,
                    bytarray.size
                )

                img_photo_trip_details.setImageBitmap(bmimage)

                pb_details.visibility = View.VISIBLE
                getData(trip.countryTrip)

            }
        })
    }

    private fun loadFlag(url: String, photo: ImageView){
        val imageUrl =
            "https://training.infnet.edu.br/wp-content/uploads/sites/" +
                    "18/2019/01/infnet-training-x2.png"
        Picasso
            .get()
            .load(url)
            .error(android.R.drawable.ic_notification_overlay)
            .into(photo)
    }

    private fun adjustPopulation(population: String): String {
        val p = population.toDouble()
        val text = String.format("%.2f", (p / (1000 * 1000)))
        return "$text mi"
    }

    private fun adjustArea(area: String): String {
        val a = area.toDouble()
        val text = String.format("%.2f", (a / (1000 * 1000)))
        return "$text mi Km²"
    }


}
