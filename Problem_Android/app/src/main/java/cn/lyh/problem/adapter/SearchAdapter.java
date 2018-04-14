package cn.lyh.problem.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lyh.problem.activity.ProblemActivity;
import cn.lyh.problem.R;
import cn.lyh.problem.model.Problem;

/**
 * Created by LYH on 2015/10/4.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> implements View.OnClickListener {

    private boolean isLast;

    private List<Problem> datas = new ArrayList<Problem>();

    private Context cxt;

    public SearchAdapter(Context cxt) {
        super();
        this.cxt = cxt;
    }


    class VH extends RecyclerView.ViewHolder{
        private TextView tv;
        private RelativeLayout rl_footer;
        public VH(View view) {
            super(view);
            this.tv = (TextView)view.findViewById(R.id.tv);
            this.rl_footer = (RelativeLayout)view.findViewById(R.id.rl_footer);
        }
    }


    public void setIsLast(boolean isLast){
        this.isLast = isLast;
        notifyDataSetChanged();
    }
    public void addItem(Problem p){
        datas.add(p);
        notifyDataSetChanged();
    }
    public void removeAll(){
        datas.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_1_layout, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.tv.setText(datas.get(position).getProblem());
        holder.tv.setTag(datas.get(position).getpId());
        holder.tv.setOnClickListener(this);
        if (position != getItemCount()-1){
            holder.rl_footer.setVisibility(View.GONE);
        }else{
            if (isLast) {
                holder.rl_footer.setVisibility(View.GONE);
            }else{
                holder.rl_footer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.tv:
                intent.setClass(cxt, ProblemActivity.class);
                intent.putExtra("problem",((TextView)view).getText().toString());
                intent.putExtra("pid",(int)view.getTag());
                cxt.startActivity(intent);
                break;
            default:
                break;
        }
    }
}
