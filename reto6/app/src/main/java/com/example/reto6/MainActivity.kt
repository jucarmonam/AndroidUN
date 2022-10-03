package com.example.reto6

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.reto6.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var obj: ListView? = null
    private var mydb: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mydb = DBHelper(this)
        val arrayList = mydb!!.getAllCompanies()
        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayList as List<Any?>)

        obj = binding.listView1
        obj!!.adapter = arrayAdapter
        obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = arg2 + 1
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch)
            val intent = Intent(applicationContext, DisplayCompany::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }
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