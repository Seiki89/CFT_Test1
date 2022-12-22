package com.example.testcft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testcft.databinding.ActivityMainBinding
import com.example.testcft.room_db.Item
import com.example.testcft.room_db.Maindb
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity() : AppCompatActivity() {
    lateinit var bind: ActivityMainBinding
    private val numList = mutableListOf<String>()
    private val nameList = mutableListOf<String?>()
    private val scopeDef = CoroutineScope(Dispatchers.Default)
    private val scopeMain = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val db = Maindb.historydb(this)


        //организация списка истории запросов
        val recyclerView: RecyclerView = bind.recycleHistory
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = HistoryAdapter(numList, nameList, db, this)
        recycleLoad(db, recyclerView)




        bind.btnOk.setOnClickListener {
            //действия по нажатию на кнопку ОК
            val numCard = bind.editTextNumCard.text.toString()
            scopeDef.launch {
                loadCardInfo(db, numCard)
                delay(50)
                saveDB(db)
                delay(50)
                withContext(Dispatchers.Main) {
                    recycleLoad(db, recyclerView)
                    HistoryAdapter(numList, nameList, db, this@MainActivity).notifyDataSetChanged()
                }
            }
        }

        bind.exitButton.setOnClickListener { finish() }


    }

    private fun recycleLoad(db: Maindb, recyclerView: RecyclerView) {
        //загрузка рейцайкла
        numList.clear()
        nameList.clear()
        scopeDef.launch {
            db.getDao().getAllItems().forEach {
                numList.add(it.num)
                nameList.add(it.bank)
            }
            withContext(Dispatchers.Main) {
                delay(50)
                recyclerView.adapter = HistoryAdapter(numList, nameList, db, this@MainActivity)
            }
        }
    }

    suspend fun loadCardInfo(db: Maindb, numCard: String) {
        //получить данные в отдельный поток
        if (numCard.isBlank()) {
            withContext(Dispatchers.Main) { bind.editTextNumCard.hint = "You didn't enter the number" }
        } else {
            val url = URL("https://lookup.binlist.net/$numCard")
            val connection = withContext(Dispatchers.IO){url.openConnection()} as HttpsURLConnection
            //проверка соединения, формирование вывода
            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader =
                    withContext(Dispatchers.IO){InputStreamReader(inputSystem, "UTF-8")}
                val result = Gson().fromJson(inputStreamReader, LoadData::class.java)
                toUI(result, db)
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Input or connection error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveDB(db: Maindb) {
        //сохранение в бд через проверку
        if (bind.editTextNumCard.text.isNullOrBlank()) {
        } else {
            val numToDB = bind.editTextNumCard.text.toString()
            val bankToDB = bind.txtNameBank.text.toString()
            scopeDef.launch {
                db.getDao().inserItem(Item(null, numToDB, bankToDB))
            }
        }
    }

    private fun viewMap(Latitude:String,Longitude:String) {
        //собрать ссылку на карту
        val gmmIntentUri = Uri.parse("google.streetview:cbll=$Latitude,$Longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent)
    }

    private fun geoOnUI(){
        //проверка наличия геопозиции
        if (bind.txtLatitude.text.toString().isBlank() && bind.txtLongitude.text.toString().isBlank()) {
            bind.imgGeo.visibility = View.GONE
        }else{val lat = bind.txtLatitude.text.toString()
            val lon = bind.txtLongitude.text.toString()
            bind.imgGeo.visibility = View.VISIBLE
            bind.cardCountry.setOnClickListener { viewMap(lat,lon) }}
    }

    private fun toUI(loadData: LoadData, db: Maindb) {
        //вывод данных в UI из json
        scopeMain.launch {
            bind.apply {
                txtSheme.text = loadData.scheme
                txtType.text = loadData.type
                txtBrend.text = loadData.brand
                when (loadData.prepaid) {
                    true -> txtRepaid.text = "YES"
                    false -> txtRepaid.text = "NO"
                }
                txtCardLenght.text = loadData.number.length.toString()
                when (loadData.number.luhn) {
                    true -> txtCardLun.text = "YES"
                    false -> txtCardLun.text = "NO"
                }
                txtCountry.text = loadData.country?.name ?: ""
                txtCountryEmoj.text = loadData.country?.emoji ?: ""
                txtLatitude.text = loadData.country?.latitude.toString()
                txtLongitude.text = loadData.country?.longitude.toString()
                txtNameBank.text = loadData.bank?.name ?: ""
                txtBankCity.text = loadData.bank?.city ?: ""
                txtPhoneBank.text = loadData.bank?.phone ?: ""
                txtUrlBank.text = loadData.bank?.url ?: ""

                geoOnUI()
            }
        }
    }

}