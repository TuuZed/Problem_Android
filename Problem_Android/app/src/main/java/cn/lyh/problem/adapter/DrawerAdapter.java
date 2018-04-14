package cn.lyh.problem.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lyh.problem.activity.EnshrineActivity;
import cn.lyh.problem.activity.ManuscriptActivity;
import cn.lyh.problem.activity.MoreActivity;
import cn.lyh.problem.activity.QuizActivity;
import cn.lyh.problem.R;
import cn.lyh.problem.activity.UserInfoActivity;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.ImagesUtils;

/**
 * Created by LYH on 2015/9/25.
 */
public class DrawerAdapter extends BaseAdapter{
    private Drawable[] pics = new Drawable[5];
    private String[] strs = new String[5];

    private Context cxt;
    private DrawerLayout drawer;

    private final int TYPE0 = 0;
    private final int TYPE1 = 1;
    private final int TYPE2 = 2;

    public DrawerAdapter(Context cxt,DrawerLayout drawer){
        this.cxt = cxt;
        this.drawer = drawer;
        initdates();

    }
    private void initdates(){
        strs[0] = cxt.getResources().getString(R.string.home);
        strs[1] = cxt.getResources().getString(R.string.quiz);
        strs[2] = cxt.getResources().getString(R.string.enshrine);
        strs[3] = cxt.getResources().getString(R.string.manuscript);
        strs[4] = cxt.getResources().getString(R.string.more);
        pics[0] = cxt.getResources().getDrawable(R.drawable.ic_home);
        pics[1] = cxt.getResources().getDrawable(R.drawable.ic_quiz);
        pics[2] = cxt.getResources().getDrawable(R.drawable.ic_enshrine);
        pics[3] = cxt.getResources().getDrawable(R.drawable.ic_manuscript);
        pics[4] = cxt.getResources().getDrawable(R.drawable.ic_more);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE0;
        if(position>=1||position<=5)
            return TYPE1;
        return TYPE2;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i)==TYPE0){
            return type0(i,view,viewGroup);
        }
        if (getItemViewType(i)==TYPE1){
            return type1(i, view, viewGroup);
        }
        if (getItemViewType(i)==TYPE2){

        }
        return view;
    }


    private View type0(int i, View view, ViewGroup viewGroup){
        ViewHolder holder = new ViewHolder();
        if (view == null) {
            view = LayoutInflater.from(cxt).inflate(R.layout.item_drawer_layout_0, viewGroup, false);
            holder.iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_intro = (TextView) view.findViewById(R.id.tv_intro);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        //填充数据
        Drawable drawable = cxt.getResources().getDrawable(R.drawable.ic_avatar);
        BitmapDrawable bd = (BitmapDrawable)drawable;
        int sex = 1;
        if (ConfigInfo.user.getuSex().equals("女")){
            sex = 2;
        }

        holder.iv_avatar.setImageBitmap(ImagesUtils.
                drawHeadIcon(cxt,ConfigInfo.user.getuName(),false,sex));
        holder.tv_name.setText(ConfigInfo.user.getuName());
        holder.tv_intro.setText(ConfigInfo.user.getuIntro());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cxt.startActivity(new Intent(cxt, UserInfoActivity.class));
                drawer.closeDrawer(Gravity.LEFT);
            }
        });

        return view;
    }
    private View type1(final int i, View view, ViewGroup viewGroup){
        ViewHolder holder = new ViewHolder();
        if (view == null){
            view = LayoutInflater.from(cxt).inflate(R.layout.item_drawer_layout_1,viewGroup,false);
            holder.imageView = (ImageView)view.findViewById(R.id.imageView);
            holder.textView = (TextView)view.findViewById(R.id.textView);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        //填充数据
        holder.imageView.setImageDrawable(pics[i - 1]);
        holder.textView.setText(strs[i - 1]);
        //设置监听器
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (i){
                    case 1:
                        break;
                    case 2:
                        intent = new Intent(cxt, QuizActivity.class);
                        break;
                    case 3:
                        intent = new Intent(cxt, EnshrineActivity.class);
                        break;
                    case 4:

                        intent = new Intent(cxt, ManuscriptActivity.class);
                        break;
                    case 5:
                        intent = new Intent(cxt, MoreActivity.class);
                        break;
                    default:
                        break;
                }
                if (intent != null)
                    cxt.startActivity(intent);
                drawer.closeDrawer(Gravity.LEFT);

            }
        });
        return view;
    }

    class ViewHolder{
        ImageView imageView,iv_avatar;
        TextView textView,tv_name,tv_intro;
    }

}
