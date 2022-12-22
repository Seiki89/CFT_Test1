package com.example.testcft

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.testcft.room_db.Maindb
import kotlinx.coroutines.*


class HistoryAdapter(
    val numList: MutableList<String>,
    val nameList: MutableList<String?>,
    val db:Maindb,
    val activity: MainActivity
) : RecyclerView.Adapter<HistoryAdapter.MyHistoryViewHolder>() {


    class MyHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNum: TextView = itemView.findViewById(R.id.recycleNum)
        val txtName: TextView = itemView.findViewById(R.id.recycleName)
        val itemCard: CardView = itemView.findViewById(R.id.recycleCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_adapter, parent, false)
        return MyHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyHistoryViewHolder, position: Int) {
        holder.txtNum.text = numList[position]
        holder.txtName.text = nameList[position]

        holder.itemCard.setOnClickListener {
            val numCard = numList[position]
            CoroutineScope(Dispatchers.Default).launch {
                activity.loadCardInfo(db,numCard)
                delay(50)
                withContext(Dispatchers.Main){notifyDataSetChanged()}

            }
        }



    }

    override fun getItemCount() = nameList.size
}