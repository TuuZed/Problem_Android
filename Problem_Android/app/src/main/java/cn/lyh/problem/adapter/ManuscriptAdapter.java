package cn.lyh.problem.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.lyh.problem.activity.AddReplyActivity;
import cn.lyh.problem.R;
import cn.lyh.problem.db.ManuscriptDb;
import cn.lyh.problem.model.Problem;

/**
 * Created by LYH on 2015/9/27.
 * 草稿
 */
public class ManuscriptAdapter extends RecyclerView.Adapter<ManuscriptAdapter.VH>
                        implements View.OnClickListener{

    private Context cxt;
    private List<Problem> datas = null;
    public ManuscriptAdapter(Context cxt) {
        super();
        this.cxt = cxt;
        this.datas = new ManuscriptDb(cxt).query();
    }

    class VH extends RecyclerView.ViewHolder{
        private TextView tv_problem,tv_reply;
        public VH(View view) {
            super(view);
            this.tv_problem = (TextView)view.findViewById(R.id.tv_problem);
            this.tv_reply = (TextView)view.findViewById(R.id.tv_reply);
        }
    }

    public void Refresh(){
        datas.clear();
        datas = new ManuscriptDb(cxt).query();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_manuscript_layout,parent,false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {

        holder.tv_problem.setText(datas.get(position).getProblem());
        holder.tv_reply.setText(datas.get(position).getReply());
        holder.tv_reply.setTag(datas.get(position));


        holder.tv_reply.setOnClickListener(this);
        holder.tv_reply.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
                builder.setTitle(cxt.getResources().getString(R.string.delete))
                        .setMessage(cxt.getResources().getString(R.string.sure_delete_manuscript))
                        .setNegativeButton(cxt.getResources().getString(R.string.cancel),null)
                        .setPositiveButton(cxt.getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               new ManuscriptDb(cxt)
                                       .delect(datas.get(position).getpId(),datas.get(position).getrId());
                                Refresh();
                            }
                        }).show();



                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(cxt, AddReplyActivity.class);
        Problem p = ((Problem) view.getTag());
        int rid = p.getrId();
        String reply = p.getReply();
        String problem = p.getProblem();
        int pid = p.getpId();
        intent.putExtra("rid", rid);
        intent.putExtra("reply", reply);
        intent.putExtra("problem", problem);
        intent.putExtra("pid", pid);
        intent.putExtra("isUpdate",true);
        cxt.startActivity(intent);
    }


}
