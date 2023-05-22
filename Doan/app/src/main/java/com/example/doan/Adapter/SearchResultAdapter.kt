import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doan.Data.PlayListData
import com.example.doan.Data.SearchResultData
import com.example.doan.R
import com.squareup.picasso.Picasso

class SearchResultAdapter(var ds:List<SearchResultData>) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBaiHat = itemView.findViewById<TextView>(R.id.txtTenBaiHat)
        val txtCaSi = itemView.findViewById<TextView>(R.id.txtCaSi)
        val imageView= itemView.findViewById<ImageView>(R.id.imgView)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_song, parent, false)
        return SearchResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.itemView.apply {
            holder.txtBaiHat.text = ds[position].songName
            holder.txtCaSi.text = ds[position].singerName
            Picasso.get().load(ds[position].img).into(holder.imageView)

        }
    }

    override fun getItemCount(): Int {
        return ds.size
    }
}
