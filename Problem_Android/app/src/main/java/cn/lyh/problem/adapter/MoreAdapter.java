package cn.lyh.problem.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lyh.problem.activity.ProblemActivity;
import cn.lyh.problem.activity.QuizActivity;
import cn.lyh.problem.R;
import cn.lyh.problem.activity.TopicActivity;
import cn.lyh.problem.model.Problem;


public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.VH>
        implements View.OnClickListener,View.OnLongClickListener {

    private boolean isLast;

    private List<Problem> datas = new ArrayList<Problem>();

    private Context cxt;

    private boolean isQuized;

    public MoreAdapter(Context cxt,boolean isQuized) {
        super();
        this.cxt = cxt;
        this.isQuized = isQuized;
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tv_from_topic, tv_problem;
        private RelativeLayout rl_footer;
        private View view;
        public VH(View view) {
            super(view);
            this.view = view;
            this.tv_problem = (TextView) view.findViewById(R.id.tv_problem);
            this.tv_from_topic = (TextView) view.findViewById(R.id.tv_from_topic);
            this.rl_footer = (RelativeLayout) view.findViewById(R.id.rl_footer);
            ;
        }
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
        notifyDataSetChanged();
    }

    public void addItem(Problem p) {
        datas.add(p);
        notifyDataSetChanged();
    }

    public void removeAll() {
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
                .inflate(R.layout.item_more_layout, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.tv_from_topic.setText(datas.get(position).getTopic());
        holder.tv_from_topic.setTag(datas.get(position));
        holder.tv_problem.setText(datas.get(position).getProblem());
        holder.tv_problem.setTag(datas.get(position));
        holder.view.setTag(datas.get(position));
        holder.tv_problem.setOnClickListener(this);
        holder.tv_from_topic.setOnClickListener(this);
        if (isQuized) {
            holder.view.setOnLongClickListener(this);
            holder.tv_problem.setOnLongClickListener(this);
            holder.tv_from_topic.setOnLongClickListener(this);
        }
        if (position != getItemCount() - 1) {
            holder.rl_footer.setVisibility(View.GONE);
        } else {
            if (isLast) {
                holder.rl_footer.setVisibility(View.GONE);
            } else {
                holder.rl_footer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_problem:
                intent.setClass(cxt, ProblemActivity.class);
                intent.putExtra("problem", ((TextView) view).getText().toString());
                intent.putExtra("pid", (int) ((Problem)view.getTag()).getpId());
                cxt.startActivity(intent);
                break;
            case R.id.tv_from_topic:
                intent.setClass(cxt, TopicActivity.class);
                intent.putExtra("topic", ((TextView) view).getText().toString());
                intent.putExtra("tid", (int) ((Problem)view.getTag()).gettId());
                cxt.startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(cxt.getResources().getString(R.string.title_activity_update_problem))
                .setMessage(cxt.getResources().getString(R.string.sure_update_problem))
                .setNegativeButton(cxt.getResources().getString(R.string.cancel), null)
                .setPositiveButton(cxt.getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pid = ((Problem) view.getTag()).getpId();
                        String problem = ((Problem) view.getTag()).getProblem();
                        String topic = ((Problem) view.getTag()).getTopic();
                        String explain = ((Problem) view.getTag()).getExplain();


                        Intent intent = new Intent(cxt, QuizActivity.class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("problem", problem);
                        intent.putExtra("topic", topic);
                        intent.putExtra("explain", explain);
                        cxt.startActivity(intent);
                    }
                }).show();

        return false;
    }
}
