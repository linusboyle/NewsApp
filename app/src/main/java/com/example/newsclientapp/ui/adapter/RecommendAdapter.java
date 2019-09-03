/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newsclientapp.R;
import com.example.newsclientapp.core.ShareUtils;
import com.example.newsclientapp.listener.OnItemClickListener;
import com.example.newsclientapp.network.NewsEntity;
import com.example.newsclientapp.storage.StorageManager;
import com.example.newsclientapp.ui.view.FuncItem;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;

import java.util.ArrayList;
import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_ITEM = 1;

	private Context mContext;
	private List<NewsEntity> mList = new ArrayList<>();
	private final float dp;

	private OnItemClickListener<NewsEntity> mOnItemClickListener;

	public RecommendAdapter(Context context) {
		mContext = context;
		dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mContext.getResources().getDisplayMetrics());
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.rv_item_news, parent, false);
		return new NewsViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof NewsViewHolder && mList.size() != 0) {
			((NewsViewHolder) holder).loadViewHolder(mList.get(position));
		}
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	@Override
	public int getItemViewType(int position) {
		return TYPE_ITEM;
	}

	public void newDataItem(List<NewsEntity> newDataList) {
		if (newDataList != null && newDataList.size() != 0) {
			mList.clear();
			addMoreItem(newDataList);
		}
	}

	private void addMoreItem(List<NewsEntity> newDataList) {
		if (newDataList != null && newDataList.size() != 0) {
			mList.addAll(newDataList);
			notifyDataSetChanged();
		}
	}

	private class NewsViewHolder extends RecyclerView.ViewHolder {

		private ImageView photo;
		private TextView title, description, datetime;
		private ImageView funcIcon;
		private FuncMenuAdapter funcMenuAdapter;
		private CustomPowerMenu funcMenu;

		NewsViewHolder (View itemView) {
			super(itemView);
			photo = itemView.findViewById(R.id.news_item_photo);
			title = itemView.findViewById(R.id.news_item_title);
			description = itemView.findViewById(R.id.news_item_description);
			datetime = itemView.findViewById(R.id.news_item_datetime);
			funcIcon = itemView.findViewById(R.id.news_item_function);
		}

		void setTitleReadColor () {
			title.setTextColor(mContext.getResources().getColor(R.color.dark_subtext));
		}

		void setTitleUnreadColor () {
			title.setTextColor(mContext.getResources().getColor(R.color.dark_text));
		}

		@SuppressLint("SetTextI18n")
		void loadViewHolder(final NewsEntity newsEntity) {
			String[] picUrls = newsEntity.getImageURLs();
			if (picUrls == null || picUrls.length == 0 || TextUtils.isEmpty(picUrls[0])) {
				photo.setVisibility(View.GONE);
			} else {
				String picUrl = picUrls[0];
				Glide.with(mContext)
						.load(picUrl)
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.glide_pic_default)
						.error(R.drawable.glide_pic_failed)
						.into(photo);
				photo.setVisibility(View.VISIBLE);
			}
			title.setText(newsEntity.getTitle());
			if (StorageManager.getInstance().getCachesList().contains(newsEntity.getNewsID())) {
				setTitleReadColor();
			} else {
				setTitleUnreadColor();
			}
			description.setText("来源：" + newsEntity.getPublisher());
			datetime.setText(newsEntity.getPublishTime());

			//noinspection Convert2Lambda
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(view, newsEntity);
						setTitleReadColor();
					}
				}
			});

			//noinspection Convert2Lambda
			View.OnClickListener shareListener = new View.OnClickListener() {
				@Override
				public void onClick (View view) {
					ShareUtils.share(view.getContext(), newsEntity);
				}
			};

			//noinspection Convert2Lambda
			View.OnClickListener galleryListener = new View.OnClickListener() {
				@Override
				public void onClick (View view) {
					boolean isFavourite = StorageManager.getInstance().getFavoritesList().contains(newsEntity.getNewsID());
					if (isFavourite) {
						if (StorageManager.getInstance().unsetFavorite(newsEntity)) {
							Toast.makeText(view.getContext(), "已删除收藏", Toast.LENGTH_LONG).show();
							((ImageView) view).setImageDrawable(mContext.getDrawable(R.drawable.ic_star_border_white_24dp));
							((ImageView) view).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.dark_text));
						} else {
							Toast.makeText(view.getContext(), "操作失败", Toast.LENGTH_LONG).show();
						}
					} else {
						if (StorageManager.getInstance().setFavorite(newsEntity)) {
							Toast.makeText(view.getContext(), "已添加到收藏", Toast.LENGTH_LONG).show();
							((ImageView) view).setImageDrawable(mContext.getDrawable(R.drawable.ic_star_white_24dp));
							((ImageView) view).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.dark_second_primary));
						} else {
							Toast.makeText(view.getContext(), "操作失败", Toast.LENGTH_LONG).show();
						}
					}
				}
			};

			funcMenuAdapter = new FuncMenuAdapter();
			funcMenu = new CustomPowerMenu.Builder<>(mContext, funcMenuAdapter)
					.addItem(new FuncItem(shareListener, galleryListener))
					.setAnimation(MenuAnimation.ELASTIC_BOTTOM_RIGHT)
					.setMenuRadius(30f)
					.setMenuShadow(10f)
					.setShowBackground(false)
					.setWidth(Math.round(100*dp))
					.build();

			//noinspection Convert2Lambda
			funcIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					funcMenuAdapter.updateFavourite(StorageManager.getInstance().getFavoritesList().contains(newsEntity.getNewsID()));
					funcMenu.showAsDropDown(view,
							view.getMeasuredWidth()/2 - funcMenu.getContentViewWidth(),
							view.getMeasuredHeight()/2 - funcMenu.getContentViewHeight());
				}
			});
		}
	}

	public void setOnItemClickListener(OnItemClickListener<NewsEntity> itemClickListener) {
		this.mOnItemClickListener = itemClickListener;
	}
}
