package com.gdswlw.library.adapter;

import android.graphics.Paint;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdswlw.library.IImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shihuanzhang on 2017-11-21.
 */

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseRecycleAdapter.BaseViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private static IImageLoader iImageLoader;
    public static void setImageLoader(@NonNull IImageLoader iImageLoader){
        BaseRecycleAdapter.iImageLoader = iImageLoader;
    }
    private View headView,footView;
    public void addHeadView(@NonNull  View headView){
        if(headView != this.headView){
            if(headView == footView){
                throw new IllegalArgumentException("headView and footView can't be the same instance!");
            }
            this.headView = headView;
            notifyDataSetChanged();
        }

    }
    public void addFootVeiew(@NonNull  View footView){
        if(footView != this.footView){
            if(headView == footView){
                throw new IllegalArgumentException("headView and footView can't be the same instance!");
            }
            this.footView = footView;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;

        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return position == 0 && headView != null;
    }

    public boolean isPositionFooter(int position) {
        return position == datas.size() && footView != null;
    }
    protected List<T> datas;

    public BaseRecycleAdapter(List<T> datas) {
        this.datas = datas;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(),parent,false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null && ( view.getTag() instanceof Integer)){
                        onItemClickListener.onItemClick((Integer) view.getTag());
                    }
                }
            });
            return  new BaseViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            return new BaseViewHolder( this.headView);
        }else if(viewType == TYPE_FOOTER){
            return new BaseViewHolder( this.footView);
        }
        throw new RuntimeException("there is no type that matches the type = " + viewType + " , make sure your using types correctly");
    }


    public void onBindViewHolder(BaseViewHolder holder,  int position) {
        if(headView != null){
            position -= 1;
        }
        if(position >= 0 && getItemViewType(headView == null? position:position+1) == TYPE_ITEM ){
            bindData(holder,position);
            holder.getContentView().setTag(position);
        }
    }


    /**
     * 刷新数据
     * @param datas
     */
    public void refresh(List<T> datas){
        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }


    /**
     * 添加数据
     * @param datas
     */
    public void addData(List<T> datas){
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     *  绑定数据
     * @param holder  具体的viewHolder
     * @param position  对应的索引
     */
    protected abstract void bindData(BaseViewHolder holder, int position);



    @Override
    public int getItemCount() {
        int headCount =(this.headView == null ? 0: 1);
        int footCount =(this.footView == null ? 0: 1);
        return (datas==null?0:datas.size())+headCount+footCount;
    }




    /**
     * 封装ViewHolder ,子类可以直接使用
     */
    public static class BaseViewHolder extends  RecyclerView.ViewHolder{

        private Map<Integer, View> mViewMap;
        private View itemView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mViewMap = new HashMap<>();
        }

        public View getContentView(){
            return  itemView;
        }

        /**
         * 获取设置的view
         * @param id
         * @return
         */
        public View getView(int id) {
            View view = mViewMap.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViewMap.put(id, view);
            }
            return view;
        }

        /**
         * Set text for view
         * @param id
         * @param text
         */
        public void setText(@IdRes int id,String text){
            View view = getView(id);
            if(view != null){
                if(view instanceof TextView){
                    ((TextView)view).setText(text);
                }else if(view instanceof Button){
                    ((Button)view).setText(text);
                }
            }
        }

        public void setTextHTML(@IdRes int id,String text){
            View view = getView(id);
            if(view != null && view instanceof TextView){
                ((TextView)view).setText(Html.fromHtml(text));
            }
        }

        /**
         * set center line for textView
         * @param id
         */
        public void setCenterLine(@IdRes int id){
            View view = getView(id);
            if(view != null && view instanceof TextView) {
                ((TextView) view).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                ((TextView) view).getPaint().setAntiAlias(true);//抗锯齿
            }
        }

        /**
         * Set image for view
         * @param id
         * @param url
         */
        public void image(@IdRes int id,@NonNull String url){
            View view = getView(id);
            if(view != null && view instanceof ImageView){
                if(iImageLoader != null){
                    iImageLoader.loadImage((ImageView)view,url);
                }
            }
        }

        public void checked(@IdRes int id,boolean checked){
            View view = getView(id);
            if(view != null && view instanceof CompoundButton){
                ((CompoundButton)view).setChecked(checked);
            }
        }
    }

    /**
     * 获取子item
     * @return
     */
    public abstract int getLayoutId();


    private OnItemClickListener onItemClickListener;
    /**
     * 设置点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    /**
     * 点击事件Listener
     */
    public  interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}