package com.bejaranix.ecommerce.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.MatrixCursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bejaranix.ecommerce.adapter.CatalogRVAdapter
import com.bejaranix.ecommerce.database.AppDatabase
import com.bejaranix.ecommerce.database.Search
import com.bejaranix.ecommerce.databinding.FragmentCatalogListBinding
import com.bejaranix.ecommerce.model.CatalogItem
import com.bejaranix.ecommerce.service.CatalogModel
import com.bejaranix.ecommerce.service.CatalogService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * Use the [CatalogListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CatalogListFragment : Fragment() {

    private val service = CatalogService()
    private val disposable = CompositeDisposable()
    private lateinit var recycleView: RecyclerView;
    private lateinit var binding:FragmentCatalogListBinding
    private lateinit var adapter:CatalogRVAdapter
    private lateinit var searchView: SearchView
    private lateinit var db: AppDatabase
    private lateinit var button:Button
    private lateinit var progressBar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        adapter = CatalogRVAdapter(inflater)
        binding = FragmentCatalogListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleView = binding.recyclerView
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(this.context)
        searchView = binding.search
        button = binding.button
        progressBar = binding.progressBar
        progressBar.isVisible = false

        context?.let {
            db = Room.databaseBuilder(
                    it,
                    AppDatabase::class.java, "search-database"
            ).build()
        }

        setSearchListener()
        setButtonListener()
    }

    private fun setSearchListener(){


        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchForCatalogItems(it)
                    CoroutineScope(Dispatchers.Default).launch {
                        db.searchDao().insertAll(Search(it))
                    }
                }
                progressBar.isVisible = true
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun setButtonListener(){
        button.setOnClickListener {
            val builder =  AlertDialog.Builder(context)
            builder.setTitle("Choose from the history")
            CoroutineScope(Dispatchers.Main).launch {
                var values: Array<String>? = null
                withContext(Dispatchers.IO) {
                    values = db.searchDao().getAll().map { it.search }.toTypedArray()
                }
                values?.let {
                    builder.setItems(it) { dialog, which ->
                        searchView.setQuery(it[which], true)
                    }
                    val dialog = builder.create();
                    dialog.show()
                }
            }
        }

    }


    private fun searchForCatalogItems(query: String) {

        disposable.add(
                service.fetchCatalogQuery(query)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<CatalogModel>() {
                            override fun onSuccess(t: CatalogModel) {
                                adapter.updateValues(convertToCatalogItem(t))
                                progressBar.isVisible = false
                            }

                            override fun onError(e: Throwable) {
                                progressBar.isVisible = false

                            }
                        }
                        ))
    }


    private fun convertToCatalogItem(catalogModel: CatalogModel):ArrayList<CatalogItem> = ArrayList(catalogModel.items.map {
        CatalogItem(it.title, it.price, it.image)
    })


}