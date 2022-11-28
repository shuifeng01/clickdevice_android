package com.example.clickdevice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickdevice.R;
import com.example.clickdevice.databinding.ItemScriptListBinding;
import com.example.clickdevice.db.ScriptListBean;

import java.util.List;

public class ScriptListAdapter extends RecyclerView.Adapter {
    private static final String TAG ="ScriptAdapter" ;
    private List<ScriptListBean> mData;
    private Context context;
    private ClickListener clickListener;

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public ScriptListAdapter(List<ScriptListBean> mData, Context context) {
        this.mData = mData;
        this.context = context;

    }

    public List<ScriptListBean> getmData() {
        return mData;
    }

    public void setmData(List<ScriptListBean> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScriptViewHolder(LayoutInflater.from(context).inflate(R.layout.item_script_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScriptViewHolder) {
            ((ScriptViewHolder) holder).itemScriptBinding.setScriptList(mData.get(position));

            if (clickListener != null) {
                ((ScriptViewHolder) holder).itemScriptBinding.btnDelete.setOnClickListener(v -> {
                    clickListener.delete(mData.get(position));
                });
                ((ScriptViewHolder) holder).itemScriptBinding.btnEdit.setOnClickListener(v->{
                    clickListener.edit(mData.get(position));
                });
                ((ScriptViewHolder) holder).itemScriptBinding.tvForTImeEdit.setOnClickListener(v->{
                    clickListener.forTimeEdit(mData.get(position));
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class ScriptViewHolder extends RecyclerView.ViewHolder {
        ItemScriptListBinding itemScriptBinding;

        public ScriptViewHolder(@NonNull View itemView) {
            super(itemView);
            itemScriptBinding = ItemScriptListBinding.bind(itemView);
        }
    }

    public interface ClickListener {
        void delete(ScriptListBean scriptDataBean);

        void edit(ScriptListBean scriptDataBean);

        void forTimeEdit(ScriptListBean scriptDataBean);
    }
}
