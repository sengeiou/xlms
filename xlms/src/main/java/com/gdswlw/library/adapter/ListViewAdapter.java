
package com.gdswlw.library.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gdswlw.library.adapter.viewholders.ListGridViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * ListView通用 Adapter
 */
public abstract class ListViewAdapter<T> extends BaseAdapter {
    /**
     * 数据集
     */
    protected final List<T> mDataSet = new ArrayList<>();
    /**
     * Item Layout
     */
    private int mItemLayoutId;

    /**
     *
     * @param layoutId
     */
    public ListViewAdapter(int layoutId) {
        mItemLayoutId = layoutId;
    }

    public ListViewAdapter(int layoutId, List<T> datas) {
        mItemLayoutId = layoutId;
        mDataSet.addAll(datas) ;
    }

    /**
     * @param item
     */
    public void addItem(T item) {
        mDataSet.add(item);
        notifyDataSetChanged();
    }

    /**
     * @param items
     */
    public void addItems(List<T> items) {
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * @param item
     */
    public void addItemToHead(T item) {
        mDataSet.add(0, item);
        notifyDataSetChanged();
    }

    /**
     * @param items
     */
    public void addItemsToHead(List<T> items) {
        mDataSet.addAll(0, items);
        notifyDataSetChanged();
    }

    /**
     * @param position
     */
    public void remove(int position) {
        mDataSet.remove(position);
        notifyDataSetChanged();
    }

    /**
     * @param item
     */
    public void remove(T item) {
        mDataSet.remove(item);
        notifyDataSetChanged();
    }

    /**
     * clear all data
     */
    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * @return
     */
    @Override
    public int getCount() {
        return mDataSet.size();
    }

    /**
     * @param position
     * @return
     */
    @Override
    public T getItem(int position) {
        return mDataSet.get(position);
    }

    /**
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 根据View Type返回布局资源
     *
     * @param type
     * @return
     */
    protected int getItemLayout(int type) {
        return mItemLayoutId;
    }

    /**
     * 封装getView逻辑,将根据viewType获取布局资源、解析布局资源、创建ViewHolder等逻辑封装起来,简化使用流程
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType( position ) ;
        ListGridViewHolder viewHolder = ListGridViewHolder.get(convertView, parent, getItemLayout(viewType));
        // 绑定数据
        onBindData(viewHolder, position, getItem(position));
        return viewHolder.getItemView();
    }

    /**
     * 绑定数据到Item View上
     *
     * @param viewHolder
     * @param position   数据的位置
     * @param item       数据项
     */
    protected abstract void onBindData(ListGridViewHolder viewHolder, int position, T item);

}
