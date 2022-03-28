package com.talhaoz.testzone

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talhaoz.testzone.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val dataStoreManager = DataStoreManager(this)

    private var DATASTORE_TYPE = DataStoreType.STANDART

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSpinnerAdapter()
        setViews()

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                save(
                    binding.etEnterKeyToSave.text.toString(),
                    binding.etEnterValueToSave.text.toString().toIntOrNull()
                )
            }
        }

        binding.btnRead.setOnClickListener {
            lifecycleScope.launch {
                when(DATASTORE_TYPE){
                    DataStoreType.STANDART -> {
                        dataStoreManager.getUserFromPreferences.collect {
                            binding.tvReadValue.text = "Name= ${it.name}\nAge= ${it.age}"
                        }
                    }
                    DataStoreType.PROTO_DATASTORE -> {
                        dataStoreManager.getUserFromProto.collect {
                            binding.tvReadValue.text = "Name= ${it.name}\nAge= ${it.age}"
                        }
                    }
                }
            }
        }
    }

    private fun setViews() {
        when(DATASTORE_TYPE){
            DataStoreType.STANDART -> {
                binding.btnSave.text = "Save to DataStore"
                binding.btnRead.text = "Read from DataStore"
            }
            DataStoreType.PROTO_DATASTORE -> {
                binding.btnSave.text = "Save to Proto"
                binding.btnRead.text = "Read from Proto"
            }
        }
    }


    private fun setSpinnerAdapter() {
        val spinner: Spinner = binding.spinnerSelection
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.datastore_stype,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.setSelection(0)
        spinner.onItemSelectedListener = this
    }

    private suspend fun save(name: String, age: Int?) {
        val user = User(
            name = name,
            age = age ?: 0
        )

        when(DATASTORE_TYPE){
            DataStoreType.STANDART -> {
                dataStoreManager.addUserToPreferences(user)
                Toast.makeText(this,"Saved to DataStore!",Toast.LENGTH_SHORT).show()
            }
            DataStoreType.PROTO_DATASTORE -> {
                dataStoreManager.addUserToProto(user)
                Toast.makeText(this,"Saved to Proto DataStore!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(p2) {
            0 -> dataStoreSelected()
            1 -> protoDataStoreSelected()
        }
    }

    private fun dataStoreSelected() {
        DATASTORE_TYPE = DataStoreType.STANDART
        setViews()
    }


    private fun protoDataStoreSelected() {
        DATASTORE_TYPE = DataStoreType.PROTO_DATASTORE
        setViews()
    }

}

enum class DataStoreType {
    PROTO_DATASTORE, STANDART
}