package br.edu.infnet.itrip.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import br.edu.infnet.itrip.AsyncTask.DecodeImageAsyncTsk
import br.edu.infnet.itrip.ENCODE_IMAGE
import br.edu.infnet.itrip.Model.Trip
import br.edu.infnet.itrip.R
import kotlinx.android.synthetic.main.fragment_add_trip.*

class TripAdapter (
    private var tripsList: MutableList<Trip>,
    val onClickListener: (Trip) -> Unit
)
    : RecyclerView.Adapter<TripAdapter.TripsViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_card, parent, false)

        return TripsViewholder(view)
    }

    class TripsViewholder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        var dateTrip: TextView = itemView.findViewById(R.id.tv_date_card)
        var countryTrip: TextView = itemView.findViewById(R.id.tv_country_card)
        var descriptionTrip: TextView = itemView.findViewById(R.id.tv_description_card)
        var ratingTrip: TextView = itemView.findViewById(R.id.tv_rating_card)
        var ratingBar: RatingBar = itemView.findViewById(R.id.rb_card)
        var photoTrip: ImageView = itemView.findViewById(R.id.img_country_card)
    }

    override fun onBindViewHolder(holder: TripsViewholder, position: Int) {
        val trip = tripsList[position]

        holder.dateTrip.text = trip.dateTrip
        holder.countryTrip.text = trip.countryTrip
        holder.descriptionTrip.text = trip.descriptionTrip

        DecodeImageAsyncTsk(holder.photoTrip).execute(trip.photoTrip)

        holder.ratingBar.rating = trip.ratingTrip.toFloat()
        holder.ratingTrip.text = trip.ratingTrip

        holder.itemView.setOnClickListener{
            onClickListener(trip)
        }
    }

    override fun getItemCount(): Int {
        return tripsList.size
    }

}
