package cn.lyh.problem.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lyh.problem.activity.AddReplyActivity;
import cn.lyh.problem.activity.ProblemActivity;
import cn.lyh.problem.activity.ProblemReplyActivity;
import cn.lyh.problem.R;
import cn.lyh.problem.activity.TopicActivity;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.ImagesUtils;

public class METAdapter extends RecyclerView.Adapter<METAdapter.VH>
        implements View.OnClickListener {

    private Context cxt;
    private boolean isLast;
    private List<Problem> datas = new ArrayList<>();
    private boolean isTopic;
    private boolean isEnshrine;
    private boolean isReplied;

    public METAdapter(Context cxt, boolean isTopic, boolean isEnshrine, boolean isReplied) {
        this.cxt = cxt;
        this.isTopic = isTopic;
        this.isEnshrine = isEnshrine;
        this.isReplied = isReplied;
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tv_from_topic, tv_problem, tv_reply, tv_praise;
        private ImageView iv_avater;
        private RelativeLayout rl_footer;
        private RelativeLayout ll_topic;
        private View view;

        public VH(View view) {
            super(view);
            this.view = view;
            this.tv_from_topic = (TextView) view.findViewById(R.id.tv_from_topic);
            this.tv_problem = (TextView) view.findViewById(R.id.tv_problem);
            this.tv_reply = (TextView) view.findViewById(R.id.tv_reply);
            this.tv_praise = (TextView) view.findViewById(R.id.tv_praise);
            this.iv_avater = (ImageView) view.findViewById(R.id.iv_avater);
            this.rl_footer = (RelativeLayout) view.findViewById(R.id.rl_footer);
            this.ll_topic = (RelativeLayout) view.findViewById(R.id.ll_topic);
        }
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
        notifyDataSetChanged();
    }

    /**
     * 添加一些数据
     */
    public void addItem(Problem data) {
        this.datas.add(data);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.datas.clear();
        notifyDataSetChanged();
    }

    private void removeOne(int position) {
        this.datas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_layout, parent, false);
        return new VH(view);

    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        if (isTopic) {
            holder.ll_topic.setVisibility(View.GONE);
        }

        try {
            holder.tv_from_topic.setOnClickListener(this);
            holder.tv_from_topic.setText(datas.get(position).getTopic());
            holder.tv_from_topic.setTag(datas.get(position).gettId());
        } catch (Exception e) {
        }

        holder.view.setTag(datas.get(position));

        holder.tv_reply.setOnClickListener(this);
        holder.tv_reply.setText(datas.get(position).getReply());
        holder.tv_reply.setTag(datas.get(position));


        holder.tv_problem.setOnClickListener(this);
        holder.tv_problem.setText(datas.get(position).getProblem());
        holder.tv_problem.setTag(datas.get(position).getpId());

        holder.tv_praise.setText(datas.get(position).getPraise() + "");
        int sex = 1;
        if (datas.get(position).getSex().equals("女")) {
            sex = 2;
        }
        holder.iv_avater.setImageBitmap(ImagesUtils.drawHeadIcon(cxt, datas.get(position).getName(), false, sex));


        if (position != getItemCount() - 1) {
            holder.rl_footer.setVisibility(View.GONE);
        } else {
            if (isLast) {
                holder.rl_footer.setVisibility(View.GONE);
            } else {
                holder.rl_footer.setVisibility(View.VISIBLE);
            }
        }
        if (isEnshrine) {
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    enshrine(position, holder);
                    return false;
                }
            });
            holder.tv_problem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    enshrine(position, holder);
                    return false;
                }
            });
            holder.tv_reply.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    enshrine(position, holder);
                    return false;
                }
            });
        }
        if (isReplied) {
            replied(holder);
        }


    }


    //修改回答监听器
    private void replied(final VH holder) {

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                replied(holder.view);
                return false;
            }
        });
        holder.tv_problem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                replied(holder.view);
                return false;
            }
        });
        holder.tv_reply.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                replied(holder.view);
                return false;
            }
        });
    }

    private void replied(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(cxt.getResources().getString(R.string.title_activity_update_reply))
                .setMessage(cxt.getResources().getString(R.string.sure_update_reply))
                .setNegativeButton(cxt.getResources().getString(R.string.cancel), null)
                .setPositiveButton(cxt.getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Problem p = ((Problem) view.getTag());
                        int rid = p.getrId();
                        String reply = p.getReply();
                        String problem = p.getProblem();
                        Intent intent = new Intent(cxt, AddReplyActivity.class);
                        intent.putExtra("rid", rid);
                        intent.putExtra("reply", reply);
                        intent.putExtra("problem", problem);
                        cxt.startActivity(intent);
                    }
                }).show();


    }


    //移除收藏监听器
    private void enshrine(final int position, final VH holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(cxt.getResources().getString(R.string.remove))
                .setMessage(cxt.getResources().getString(R.string.remove)
                        + holder.tv_problem.getText().toString()
                        + cxt.getResources().getString(R.string.this_enshrine))
                .setNegativeButton(cxt.getResources().getString(R.string.cancel), null)
                .setPositiveButton(cxt.getResources().getString(R.string.sure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FinalHttp fh = new FinalHttp();
                                AjaxParams params = new AjaxParams();
                                params.put("uid", ConfigInfo.user.getuId() + "");
                                params.put("rid", datas.get(position).getrId() + "");
                                fh.post(ConfigInfo.URL.UNENSHRINE, params, new AjaxCallBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        super.onSuccess(s);
                                        try {
                                            JSONObject object = new JSONObject(s);
                                            if (object.getInt("code") == 100) {
                                                Toast.makeText(cxt, cxt.getResources().getString(R.string.remove_success),
                                                        Toast.LENGTH_SHORT).show();
                                                removeOne(position);
                                            } else {

                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(cxt, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                                        super.onFailure(t, errorNo, strMsg);
                                    }
                                });
                            }
                        }).show();
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_from_topic:
                intent.setClass(cxt, TopicActivity.class);
                intent.putExtra("topic", ((TextView) view).getText().toString());
                intent.putExtra("tid", (int) view.getTag());
                cxt.startActivity(intent);
                break;
            case R.id.tv_reply:
                intent.setClass(cxt, ProblemReplyActivity.class);
                intent.putExtra("reply", ((TextView) view).getText().toString());
                Problem p = (Problem) view.getTag();
                intent.putExtra("problem", p.getProblem());
                intent.putExtra("rid", p.getrId());
                cxt.startActivity(intent);
                break;
            case R.id.tv_problem:
                intent.setClass(cxt, ProblemActivity.class);
                intent.putExtra("problem", ((TextView) view).getText().toString());
                intent.putExtra("pid", (int) view.getTag());
                cxt.startActivity(intent);
                break;
            case R.id.tv_name:
                break;
            default:
                break;
        }
    }

}
