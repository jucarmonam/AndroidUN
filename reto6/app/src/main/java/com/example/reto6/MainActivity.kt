package com.example.reto6

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.reto6.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private var obj: ListView? = null
    private var mydb: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner = binding.filterSpinnerClassification

        // Spinner click listener
        spinner.onItemSelectedListener = this

        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.classifications,
            android.R.layout.simple_spinner_item)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = dataAdapter

        mydb = DBHelper(this)
        val arrayList = mydb!!.getAllCompanies()
        val arrayListIds = mydb!!.getAllCompaniesIds()
        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayList as List<Any?>)

        obj = binding.listView1
        obj!!.adapter = arrayAdapter
        obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = arrayListIds[arg2]
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch.toInt())
            val intent = Intent(applicationContext, DisplayCompany::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }

        val searchText = binding.filterTextName
        searchText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val arrayListName = mydb!!.getByName(s)
                val arrayListNameIds = mydb!!.getByNameIds(s)

                val arrayAdapterName: ArrayAdapter<*> =
                    ArrayAdapter<Any?>(this@MainActivity,android.R.layout.simple_list_item_1, arrayListName as List<Any?>)

                obj!!.adapter = arrayAdapterName
                obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
                    val idToSearch = arrayListNameIds[arg2]
                    val dataBundle = Bundle()
                    dataBundle.putInt("id", idToSearch.toInt())
                    val intent = Intent(applicationContext, DisplayCompany::class.java)
                    intent.putExtras(dataBundle)
                    startActivity(intent)
                }
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        val item = parent.getItemAtPosition(pos)

        if(pos != 0){
            val arrayList = mydb!!.getByClassifications(item.toString())
            val arrayListIds = mydb!!.getByClassificationsIds(item.toString())

            val arrayAdapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayList as List<Any?>)

            obj!!.adapter = arrayAdapter
            obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
                val idToSearch = arrayListIds[arg2]
                val dataBundle = Bundle()
                dataBundle.putInt("id", idToSearch.toInt())
                val intent = Intent(applicationContext, DisplayCompany::class.java)
                intent.putExtras(dataBundle)
                startActivity(intent)
            }

            Toast.makeText(applicationContext, "Filter applied: ".plus(item), Toast.LENGTH_SHORT).show()
        }else{
            val arrayList = mydb!!.getAllCompanies()
            val arrayListIds = mydb!!.getAllCompaniesIds()

            val arrayAdapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayList as List<Any?>)

            obj!!.adapter = arrayAdapter
            obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
                val idToSearch = arrayListIds[arg2]
                val dataBundle = Bundle()
                dataBundle.putInt("id", idToSearch.toInt())
                val intent = Intent(applicationContext, DisplayCompany::class.java)
                intent.putExtras(dataBundle)
                startActivity(intent)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.item1 -> {
                val dataBundle = Bundle()
                dataBundle.putInt("id", 0)
                val intent = Intent(applicationContext, DisplayCompany::class.java)
                intent.putExtras(dataBundle)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keycode: Int, event: KeyEvent?): Boolean {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
        }
        return super.onKeyDown(keycode, event)
    }
}