package cn.lyh.problem.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lyh.problem.AddReplyActivity;
import cn.lyh.problem.ProblemActivity;
import cn.lyh.problem.ProblemReplyActivity;
import cn.lyh.problem.R;
import cn.lyh.problem.TopicActivity;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ImagesUtils;

/**
 * Created by LYH on 2015/10/4.
 */
public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.VH> implements View.OnClickListener {

    private boolean isLast;

    private List<Problem> datas = new ArrayList<Problem>();
    private Problem head = null;

    private Context cxt;

    private int pid;

    public ProblemAdapter(Context cxt,int pid) {
        super();
        this.cxt = cxt;
        this.pid = pid;
    }


    class VH extends RecyclerView.ViewHolder{
        private TextView tv_from_topic,tv_problem,tv_explain,tv_add_reply;

        private ImageView iv_avatar;
        private TextView tv_name,tv_praise,tv_reply;


        private RelativeLayout rl_footer;
        public VH(View view) {
            super(view);
            this.tv_from_topic = (TextView)view.findViewById(R.id.tv_from_topic);
            this.tv_problem = (TextView)view.findViewById(R.id.tv_problem);
            this.tv_explain = (TextView)view.findViewById(R.id.tv_explain);
            this.tv_add_reply = (TextView)view.findViewById(R.id.tv_add_reply);


            this.iv_avatar = (ImageView)view.findViewById(R.id.iv_avatar);
            this.tv_name = (TextView)view.findViewById(R.id.tv_name);
            this.tv_praise = (TextView)view.findViewById(R.id.tv_praise);
            this.tv_reply = (TextView)view.findViewById(R.id.tv_reply);

            this.rl_footer = (RelativeLayout)view.findViewById(R.id.rl_footer);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }else{
            return 1;
        }

    }

    public void setIsLast(boolean isLast){
        this.isLast = isLast;
        notifyDataSetChanged();
    }

    public void setHead(Problem p){
        head = p;
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
        if (head == null){
            return datas.size();
        }
        return datas.size()+1;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =null;
        if (viewType ==0 ){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_problem_layout_0, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_problem_layout_1, parent, false);
        }
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (position == 0) {
            holder.tv_from_topic.setText(head.getTopic());
            holder.tv_from_topic.setTag(head.gettId());
            holder.tv_from_topic.setOnClickListener(this);
            holder.tv_problem.setText(head.getProblem());
            holder.tv_explain.setText(head.getExplain());

            holder.tv_add_reply.setOnClickListener(this);
            holder.tv_add_reply.setTag(head.getProblem());

        }else{
            Problem p = datas.get(position - 1);
            holder.tv_praise.setText(p.getPraise()+"");
            holder.tv_reply.setText(p.getReply());
            holder.tv_reply.setTag(p);
            holder.tv_reply.setOnClickListener(this);
            holder.tv_name.setText(p.getName());
            int sex = 1;
            if (p.getSex().equals("女")){
                sex = 2;
            }
            holder.iv_avatar.setImageBitmap(ImagesUtils.drawHeadIcon(cxt,p.getName(),false,sex));


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
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.tv_from_topic:
                intent.setClass(cxt, TopicActivity.class);
                intent.putExtra("tid", (int) view.getTag());
                intent.putExtra("topic",((TextView)view).getText()+"");
                cxt.startActivity(intent);
                break;
            case R.id.tv_add_reply:
                intent.setClass(cxt, AddReplyActivity.class);
                intent.putExtra("pid", pid);
                intent.putExtra("problem",(String)view.getTag());
                cxt.startActivity(intent);
                break;
            case R.id.tv_reply:
                intent.setClass(cxt, ProblemReplyActivity.class);
                intent.putExtra("reply", ((TextView) view).getText().toString());
                Problem p = (Problem)view.getTag();
                intent.putExtra("problem",head.getProblem());
                intent.putExtra("rid",p.getrId());
                cxt.startActivity(intent);
                break;
            default:
                break;
        }
    }
}
