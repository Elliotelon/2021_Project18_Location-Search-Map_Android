package fastcampus.aop.part4.chapter03

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part4.chapter03.databinding.ViewholderSearchResultItemBinding
import fastcampus.aop.part4.chapter03.model.SearchResultEntity

class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultItemViewHolder>() {

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit


    class SearchResultItemViewHolder(private val binding: ViewholderSearchResultItemBinding, val searchResultClickListener: (SearchResultEntity) -> Unit): RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: SearchResultEntity) = with(binding) {
            textTextView.text = data.name
            subtextTextView.text = data.fullAddress
        }

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ViewholderSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultItemViewHolder(view, searchResultClickListener)
    }

    override fun getItemCount(): Int = searchResultList.size

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    fun setSearchResultList(searchResultList: List<SearchResultEntity>, searchResultClickListener: (SearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}