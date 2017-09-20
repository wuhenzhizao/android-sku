package com.wuhenzhizao.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuhenzhizao.R;
import com.wuhenzhizao.bean.SkuAttribute;
import com.wuhenzhizao.utils.ScreenUtils;
import com.wuhenzhizao.utils.ViewUtils;
import com.wuhenzhizao.widget.SkuFlowLayout;

import java.util.List;
import java.util.UUID;

/**
 * Created by wuhenzhizao on 2017/7/31.
 */

public class SkuItemLayout extends LinearLayout {
    private TextView attributeNameTv;
    private SkuFlowLayout attributeValueLayout;
    private OnSkuItemSelectListener listener;

    public SkuItemLayout(Context context) {
        super(context);
        init(context);
    }

    public SkuItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SkuItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        attributeNameTv = new TextView(context);
        attributeNameTv.setId(ViewUtils.generateViewId());
        attributeNameTv.setTextColor(context.getResources().getColor(R.color.comm_text_gray_light));
        attributeNameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        attributeNameTv.setIncludeFontPadding(false);
        LayoutParams attributeNameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        attributeNameParams.leftMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeNameParams.topMargin = ScreenUtils.dp2PxInt(context, 10);
        attributeNameTv.setLayoutParams(attributeNameParams);
        addView(attributeNameTv);

        attributeValueLayout = new SkuFlowLayout(context);
        attributeValueLayout.setId(ViewUtils.generateViewId());
        attributeValueLayout.setMinimumHeight(ScreenUtils.dp2PxInt(context, 30));
        attributeValueLayout.setChildSpacing(ScreenUtils.dp2PxInt(context, 20));
        attributeValueLayout.setRowSpacing(ScreenUtils.dp2PxInt(context, 15));
        LayoutParams attributeValueParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        attributeValueParams.leftMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeValueParams.rightMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeValueParams.topMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeValueParams.bottomMargin = ScreenUtils.dp2PxInt(context, 10);
        attributeValueLayout.setLayoutParams(attributeValueParams);
        addView(attributeValueLayout);
    }

    public void setListener(OnSkuItemSelectListener listener) {
        this.listener = listener;
    }

    public void buildItemLayout(int position, String attributeName, List<String> attributeValueList) {
        attributeNameTv.setText(attributeName);
        attributeValueLayout.removeAllViewsInLayout();
        for (int i = 0; i < attributeValueList.size(); i++) {
            SkuItemView itemView = new SkuItemView(getContext());
            itemView.setId(ViewUtils.generateViewId());
            itemView.setAttributeValue(attributeValueList.get(i));
            itemView.setOnClickListener(new ItemClickListener(position, itemView));
            itemView.setLayoutParams(new SkuFlowLayout.LayoutParams(
                    SkuFlowLayout.LayoutParams.WRAP_CONTENT,
                    ScreenUtils.dp2PxInt(getContext(), 30)));
            attributeValueLayout.addView(itemView);
        }
    }

    public void clearItemViewStatus() {
        for (int i = 0; i < attributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) attributeValueLayout.getChildAt(i);
            itemView.setSelected(false);
            itemView.setEnabled(false);
            Log.d("sku", attributeNameTv.getText().toString() + "---" + itemView.getAttributeValue() + "---disabled");
        }
    }

    public void optionItemViewEnableStatus(String attributeValue) {
        for (int i = 0; i < attributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) attributeValueLayout.getChildAt(i);
            if (attributeValue.equals(itemView.getAttributeValue())) {
                itemView.setEnabled(true);
                Log.d("sku", attributeNameTv.getText().toString() + "---" + itemView.getAttributeValue() + "---enabled");
            }
        }
    }

    public void optionItemViewSelectStatus(List<SkuAttribute> selectValueList) {
        for (int i = 0; i < attributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) attributeValueLayout.getChildAt(i);
            for (int j = 0; j < selectValueList.size(); j++) {
                if (selectValueList.get(j).getValue().equals(itemView.getAttributeValue())) {
                    itemView.setEnabled(true);
                    itemView.setSelected(true);
                    Log.d("sku", attributeNameTv.getText().toString() + "---" + itemView.getAttributeValue() + "---selected");
                }
            }
        }
    }

    public boolean isSelected() {
        for (int i = 0; i < attributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) attributeValueLayout.getChildAt(i);
            if (itemView.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void onSkuItemClicked(int position, SkuItemView view) {
        boolean selected = !view.isSelected();
        SkuAttribute attribute = new SkuAttribute();
        attribute.setKey(attributeNameTv.getText().toString());
        attribute.setValue(view.getAttributeValue());
        listener.onSelect(position, selected, attribute);
    }

    private class ItemClickListener implements OnClickListener {
        private int position;
        private SkuItemView view;

        ItemClickListener(int position, SkuItemView view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            onSkuItemClicked(position, view);
        }
    }

    interface OnSkuItemSelectListener {
        void onSelect(int position, boolean select, SkuAttribute attribute);
    }
}
