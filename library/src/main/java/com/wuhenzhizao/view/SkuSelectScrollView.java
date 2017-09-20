package com.wuhenzhizao.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wuhenzhizao.bean.Sku;
import com.wuhenzhizao.bean.SkuAttribute;
import com.wuhenzhizao.utils.ViewUtils;
import com.wuhenzhizao.widget.SkuMaxHeightScrollView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wuhenzhizao on 2017/7/31.
 */
public class SkuSelectScrollView extends SkuMaxHeightScrollView implements SkuItemLayout.OnSkuItemSelectListener {
    private LinearLayout skuContainerLayout;
    private List<Sku> skuList;
    private List<SkuAttribute> selectedAttributeList;
    private OnSkuListener listener;

    public SkuSelectScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public SkuSelectScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFillViewport(true);
        setOverScrollMode(OVER_SCROLL_NEVER);
        skuContainerLayout = new LinearLayout(context, attrs);
        skuContainerLayout.setId(ViewUtils.generateViewId());
        skuContainerLayout.setOrientation(LinearLayout.VERTICAL);
        skuContainerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(skuContainerLayout);
    }

    public void setSkuViewProxy(SkuViewProxy proxy) {
        this.listener = proxy.getListener();
    }

    public void setListener(OnSkuListener listener) {
        this.listener = listener;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
        skuContainerLayout.removeAllViews();

        Map<String, List<String>> dataMap = getSkuGroupByName(skuList);
        selectedAttributeList = new LinkedList<>();
        int index = 0;
        for (Iterator<Map.Entry<String, List<String>>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<String>> entry = it.next();

            SkuItemLayout itemLayout = new SkuItemLayout(getContext());
            itemLayout.setId(ViewUtils.generateViewId());
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            itemLayout.buildItemLayout(index++, entry.getKey(), entry.getValue());
            itemLayout.setListener(this);
            skuContainerLayout.addView(itemLayout);
            selectedAttributeList.add(new SkuAttribute(entry.getKey(), ""));
        }
        // 一个sku时，默认选中
        if (skuList.size() == 1) {
            selectedAttributeList.clear();
            for (SkuAttribute attribute : this.skuList.get(0).getAttributes()) {
                selectedAttributeList.add(new SkuAttribute(attribute.getKey(), attribute.getValue()));
            }
        }
        // 清除所有选中状态
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }

    private Map<String, List<String>> getSkuGroupByName(List<Sku> list) {
        Map<String, List<String>> dataMap = new LinkedHashMap<>();
        for (Sku sku : list) {
            for (SkuAttribute attribute : sku.getAttributes()) {
                String attributeName = attribute.getKey();
                String attributeValue = attribute.getValue();
                if (!dataMap.containsKey(attributeName)) {
                    dataMap.put(attributeName, new LinkedList<String>());
                }
                List<String> valueList = dataMap.get(attributeName);
                if (!valueList.contains(attributeValue)) {
                    dataMap.get(attributeName).add(attributeValue);
                }
            }
        }
        return dataMap;
    }

    private void clearAllLayoutStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            itemLayout.clearItemViewStatus();
        }
    }

    private void optionLayoutEnableStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            for (int j = 0; j < skuList.size(); j++) {
                boolean filter = false;
                Sku sku = skuList.get(j);
                List<SkuAttribute> attributeBeanList = sku.getAttributes();
                for (int k = 0; k < selectedAttributeList.size(); k++) {
                    if (i == k || "".equals(selectedAttributeList.get(k).getValue())) {
                        continue;
                    }
                    if (!selectedAttributeList.get(k).getValue().equals(attributeBeanList.get(k).getValue())
                            || sku.getStockQuantity() == 0) {
                        filter = true;
                        break;
                    }
                }
                if (!filter) {
                    itemLayout.optionItemViewEnableStatus(attributeBeanList.get(i).getValue());
                }
            }
        }
    }

    private void optionLayoutSelectStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            itemLayout.optionItemViewSelectStatus(selectedAttributeList);
        }
    }

    private boolean isSkuSelected() {
        for (SkuAttribute attribute : selectedAttributeList) {
            if (TextUtils.isEmpty(attribute.getValue())) {
                return false;
            }
        }
        return true;
    }

    private Sku getSelectedSku() {
        if (!isSkuSelected()) {
            return null;
        }
        for (Sku sku : skuList) {
            List<SkuAttribute> attributeList = sku.getAttributes();
            boolean flag = true;
            for (int i = 0; i < attributeList.size(); i++) {
                if (!isSameSkuAttribute(attributeList.get(i), selectedAttributeList.get(i))) {
                    flag = false;
                }
            }
            if (flag) {
                return sku;
            }
        }
        return null;
    }

    private boolean isSameSkuAttribute(SkuAttribute previousAttribute, SkuAttribute nextAttribute) {
        return previousAttribute.getKey().equals(nextAttribute.getKey())
                && previousAttribute.getValue().equals(nextAttribute.getValue());
    }

    @Override
    public void onSelect(int position, boolean selected, SkuAttribute attribute) {
        if (selected) {
            selectedAttributeList.set(position, attribute);
            listener.onSelect(attribute);
        } else {
            selectedAttributeList.get(position).setValue("");
            listener.onUnselect(attribute);
        }
        if (isSkuSelected()) {
            listener.onSkuSelected(getSelectedSku());
        }
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }
}