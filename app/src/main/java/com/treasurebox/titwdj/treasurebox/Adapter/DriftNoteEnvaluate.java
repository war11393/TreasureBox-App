package com.treasurebox.titwdj.treasurebox.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.treasurebox.titwdj.treasurebox.Model.nother.DriftNote;
import com.treasurebox.titwdj.treasurebox.R;

import java.util.List;

public class DriftNoteEnvaluate extends RecyclerView.Adapter<DriftNoteEnvaluate.ViewHolder> {

    private Context mContext;
    private List<DriftNote.DriftEvaluateListBean> driftEvaluateList;

    public DriftNoteEnvaluate(List<DriftNote.DriftEvaluateListBean> driftEvaluateList) {
        this.driftEvaluateList = driftEvaluateList;
    }

    @Override
    public void onBindViewHolder(DriftNoteEnvaluate.ViewHolder holder, int position) {
        DriftNote.DriftEvaluateListBean driftEvaluateListBean = driftEvaluateList.get(position);
        holder.simpleText.setText(driftEvaluateListBean.getUserName() + "：" + driftEvaluateListBean.getDrifContent());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView simpleText;
        public ViewHolder(View itemView) {
            super(itemView);
            simpleText = itemView.findViewById(R.id.simple_text);
        }
    }

    @Override
    public DriftNoteEnvaluate.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_text_left, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return driftEvaluateList.size();
    }
}
