package com.catsic.biz.yh.adapter;

import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.catsic.R;
import com.catsic.biz.yh.activity.LSLocationActivity;
import com.catsic.biz.yh.activity.LSViewActivity;
import com.catsic.biz.yh.bean.LsJbxx;
import com.catsic.biz.yh.service.LsJbxxService;
import com.catsic.biz.yh.utils.ShbzUtils;
import com.catsic.core.AppConstants;
import com.catsic.core.AppConstants.State;
import com.catsic.core.tools.ProgressDialogUtil;
import com.catsic.core.tools.StringUtil;

/**
 * @Description: 路损列表Adapter
 * @author wuxianling
 * @date 2014年8月18日 下午5:07:20
 */
public class LSListViewAdapter extends BaseAdapter implements View.OnClickListener{
	
	private Context context; // 运行上下文
	public List<Map<String, Object>> listItems; // 信息集合
	private LayoutInflater listContainer; // 视图容器
	public State state = State.NORMAL;
	public boolean[] hasChecked; // 记录Item选中状态
	
	public final class ListItemView { // 自定义控件集合
		public String crowid;
		public TextView tv_lx;
		public TextView tv_qzzh;
		public TextView tv_fxsj;
		public Button btn_item_oper;
		public Button btn_item_location;
		public CheckBox cb_item_del;
	}

	public LSListViewAdapter(Context context,List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		hasChecked = new boolean[getCount()];
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	/**  
	  * @Title: isChecked  
	  * @Description: 是否存在被选中的checkbox 
	  * @param @return     
	  * @return boolean   
	  * @throws  
	  */ 
	public boolean isChecked(){
		for (int i = 0; i < hasChecked.length; i++) {
			if (hasChecked[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ListView Item设置
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int selectID = position;
		// 自定义视图
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.activity_yhxc_ls_list_item, null);
			// 获取控件对象
			listItemView.tv_lx = (TextView) convertView.findViewById(R.id.tv_lx);
			listItemView.tv_qzzh = (TextView) convertView.findViewById(R.id.tv_qzzh);
			listItemView.tv_fxsj = (TextView) convertView.findViewById(R.id.tv_fxsj);
			listItemView.btn_item_oper = (Button) convertView.findViewById(R.id.btn_item_oper);
			listItemView.btn_item_location = (Button) convertView.findViewById(R.id.btn_item_location);
			listItemView.cb_item_del = (CheckBox) convertView.findViewById(R.id.cb_item_del);
			
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		
		if (state == State.NORMAL) {
			listItemView.btn_item_oper.setVisibility(View.VISIBLE);
			listItemView.btn_item_location.setVisibility(View.VISIBLE);
			listItemView.cb_item_del.setVisibility(View.INVISIBLE);
		}else if(state == State.BATCH){
			listItemView.btn_item_oper.setVisibility(View.INVISIBLE);
			listItemView.btn_item_location.setVisibility(View.INVISIBLE);
			listItemView.cb_item_del.setVisibility(View.VISIBLE);
		}
		
		// 设置文字
		listItemView.crowid = listItems.get(position).get("crowid").toString();
		listItemView.tv_lx.setText(listItems.get(position).get("lxmc")+"("+listItems.get(position).get("lxbm")+")");
		listItemView.tv_qzzh.setText("K"+listItems.get(position).get("qdzh")+"~K"+listItems.get(position).get("zdzh"));
		listItemView.tv_fxsj.setText(StringUtil.toString(listItems.get(position).get("fxsj")));
		String shbz = StringUtil.toString(listItems.get(position).get("shbz"));
		String oper = ShbzUtils.getOperByShbz(shbz);
		//上报
		if (ShbzUtils.OPER_WSB.equals(oper)) {
			listItemView.btn_item_oper.setEnabled(true);
			listItemView.btn_item_oper.setBackgroundResource(R.color.icon_common_upload_selector);
		}//已上报
		else if (ShbzUtils.OPER_YSB.equals(oper)) {
			listItemView.btn_item_oper.setEnabled(false);
			listItemView.btn_item_oper.setBackgroundResource(R.color.icon_common_success_selector);
		}//被退回
		else if (ShbzUtils.OPER_TH.equals(oper)) {
			listItemView.btn_item_oper.setEnabled(true);
			listItemView.btn_item_oper.setBackgroundResource(R.color.icon_common_upload_selector);
		}
		listItemView.cb_item_del.setChecked(false);
		listItemView.btn_item_location.setBackgroundResource(R.color.icon_commmon_location_selector);
		
		
		/**
		 * 上报
		 */
		listItemView.btn_item_oper.setTag(selectID);
		listItemView.btn_item_oper.setOnClickListener(this);

		
		/**
		 * 定位
		 */
		LsJbxx obj = new LsJbxx();
		obj.setLxbm(StringUtil.toString(listItems.get(position).get("lxbm")));
		obj.setLxmc(StringUtil.toString(listItems.get(position).get("lxmc")));
		if (listItems.get(position).get("qdzh")!=null) {
			obj.setQdzh(Float.parseFloat(listItems.get(position).get("qdzh").toString()));
			
		}
		if (listItems.get(position).get("zdzh")!=null) {
			obj.setZdzh(Float.parseFloat(listItems.get(position).get("zdzh").toString()));
		}
		listItemView.btn_item_location.setTag(obj);
		listItemView.btn_item_location.setOnClickListener(this);
		
		/**
		 * 删除
		 */
		listItemView.cb_item_del.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				// 记录Item选中状态
				hasChecked[selectID] = !hasChecked[selectID];
			}
		});
		
		/**
		 * 明细
		 */
		convertView.setOnClickListener(this);
		
		/**
		 * 长按view 菜单处理
		 */
		convertView.setOnCreateContextMenuListener(new ContextMenuListener(selectID));

		final View tagView = convertView;
		tagView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				tagView.showContextMenu();
				return true;
			}
		});

		return tagView;
	}
	
	@Override
	public void onClick(View v) {
		ProgressDialogUtil.show(context, "正在加载...", true);
		switch (v.getId()) {
			case R.id.yhxc_ls_list_item:
				ListItemView liv =  (ListItemView) v.getTag();
				
				Intent intent = new Intent(context,LSViewActivity.class);
				intent.putExtra("crowid", liv.crowid);
				context.startActivity(intent);
				break;
			case R.id.btn_item_location:
				LsJbxx obj = (LsJbxx) v.getTag();
				Intent intentLocation = new Intent(context,LSLocationActivity.class);
				intentLocation.putExtra("lxbm", obj.getLxbm());
				intentLocation.putExtra("lxmc", obj.getLxmc());
				intentLocation.putExtra("qdzh", obj.getQdzh());
				intentLocation.putExtra("zdzh", obj.getZdzh());
				context.startActivity(intentLocation);
				break;
			case R.id.btn_item_oper:
				int selectID = (Integer) v.getTag();
				try {
					new LsJbxxService(context).operationSB(listItems, selectID);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				//refresh listview
				notifyDataSetChanged();
				break;
		}
		ProgressDialogUtil.dismiss();
	}
	
	
	
	/**  
	  * @Description: 菜单处理 
	  * @author wuxianling  
	  * @date 2014年11月15日 上午11:28:33    
	  */ 
	private class ContextMenuListener implements OnCreateContextMenuListener{
		private int selectID;
		
		public ContextMenuListener(int selectID){
			this.selectID = selectID;
		}
		
		/**
		 * 
		  * @Description: OnMenuItemClickListener 
		  * @author wuxianling  
		  * @date 2014年8月20日 上午11:34:44
		 */
		class MenuItemClickListener implements MenuItem.OnMenuItemClickListener{
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case 1://上报
						try {
							new LsJbxxService(context).operationSB(listItems, selectID);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						break;
					case 2://删除
						new LsJbxxService(context).operationDel(listItems, selectID);
						break;

				}
				return true;
			}
			
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
			MenuItemClickListener menuItemClickListener= new MenuItemClickListener();
			ListItemView liv = (ListItemView) v.getTag();
			
			FinalDb db = FinalDb.create(context,AppConstants.DB_NAME);
			LsJbxx obj = db.findById(liv.crowid, LsJbxx.class);
			
			menu.setHeaderTitle("操作");
			if (ShbzUtils.OPER_SB.equals(ShbzUtils.getOperByShbz(obj.getShbz()))) {
				menu.add(0, 1, 1, "上报").setOnMenuItemClickListener(menuItemClickListener);
				menu.add(0, 2, 2, "删除").setOnMenuItemClickListener(menuItemClickListener);
			}else if (ShbzUtils.OPER_SH.equals(ShbzUtils.getOperByShbz(obj.getShbz()))) {
				menu.add(0, 2, 2, "删除").setOnMenuItemClickListener(menuItemClickListener);
			}
			
		}
	}

	
	
}
