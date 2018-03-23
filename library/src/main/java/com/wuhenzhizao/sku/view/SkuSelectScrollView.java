package com.wuhenzhizao.sku.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wuhenzhizao.sku.bean.Sku;
import com.wuhenzhizao.sku.bean.SkuAttribute;
import com.wuhenzhizao.sku.utils.ViewUtils;
import com.wuhenzhizao.sku.widget.SkuMaxHeightScrollView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhenzhizao on 2017/7/31.
 */
public class SkuSelectScrollView extends SkuMaxHeightScrollView implements SkuItemLayout.OnSkuItemSelectListener {
    private LinearLayout skuContainerLayout;
    private List<Sku> skuList;
    private List<SkuAttribute> selectedAttributeList;  // 存放当前属性选中信息
    private OnSkuListener listener;                    // sku选中状态回调接口

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

    /**
     * 设置SkuView委托，MVVM + Databinding模式下使用
     *
     * @param delegate
     */
    public void setSkuViewDelegate(SkuViewDelegate delegate) {
        this.listener = delegate.getListener();
    }

    /**
     * 设置监听接口
     *
     * @param listener {@link OnSkuListener}
     */
    public void setListener(OnSkuListener listener) {
        this.listener = listener;
    }

    /**
     * 绑定sku数据
     *
     * @param skuList
     */
    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
        // 清空sku视图
        skuContainerLayout.removeAllViews();

        // 获取分组的sku集合
        Map<String, List<String>> dataMap = getSkuGroupByName(skuList);
        selectedAttributeList = new LinkedList<>();
        int index = 0;
        for (Iterator<Map.Entry<String, List<String>>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<String>> entry = it.next();

            // 构建sku视图
            SkuItemLayout itemLayout = new SkuItemLayout(getContext());
            itemLayout.setId(ViewUtils.generateViewId());
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            itemLayout.buildItemLayout(index++, entry.getKey(), entry.getValue());
            itemLayout.setListener(this);
            skuContainerLayout.addView(itemLayout);
            // 初始状态下，所有属性信息设置为空
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

    /**
     * 将sku根据属性名进行分组
     *
     * @param list
     * @return 如{ "颜色": {"白色", "红色", "黑色"}, "尺寸": {"M", "L", "XL", "XXL"}}
     */
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

    /**
     * 重置所有属性的选中状态
     */
    private void clearAllLayoutStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            itemLayout.clearItemViewStatus();
        }
    }

    /**
     * 设置所有属性的Enable状态，即是否可点击
     */
    private void optionLayoutEnableStatus() {
        int childCount = skuContainerLayout.getChildCount();
        if (childCount <= 1) {
            optionLayoutEnableStatusSingleProperty();
        } else {
            optionLayoutEnableStatusMultipleProperties();
        }
    }

    private void optionLayoutEnableStatusSingleProperty() {
        SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(0);
        // 遍历sku列表
        for (int i = 0; i < skuList.size(); i++) {
            // 属性值是否可点击flag
            Sku sku = skuList.get(i);
            List<SkuAttribute> attributeBeanList = skuList.get(i).getAttributes();
            if (sku.getStockQuantity() > 0) {
                String attributeValue = attributeBeanList.get(0).getValue();
                itemLayout.optionItemViewEnableStatus(attributeValue);
            }
        }
    }

    private void optionLayoutEnableStatusMultipleProperties() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            // 遍历sku列表
            for (int j = 0; j < skuList.size(); j++) {
                // 属性值是否可点击flag
                boolean flag = false;
                Sku sku = skuList.get(j);
                List<SkuAttribute> attributeBeanList = sku.getAttributes();
                // 遍历选中信息列表
                for (int k = 0; k < selectedAttributeList.size(); k++) {
                    // i = k，跳过当前属性，避免多次设置是否可点击
                    if (i == k) continue;
                    // 选中信息为空，则说明未选中，无法判断是否有不可点击的情形，跳过
                    if ("".equals(selectedAttributeList.get(k).getValue())) continue;
                    // 选中信息列表中不包含当前sku的属性，则sku组合不存在，设置为不可点击
                    // 库存为0，设置为不可点击
                    if (!selectedAttributeList.get(k).getValue().equals(attributeBeanList.get(k).getValue())
                            || sku.getStockQuantity() == 0) {
                        flag = true;
                        break;
                    }
                }
                // flag 为false时，可点击
                if (!flag) {
                    String attributeValue = attributeBeanList.get(i).getValue();
                    itemLayout.optionItemViewEnableStatus(attributeValue);
                }
            }
        }
    }

    /**
     * 设置所有属性的选中状态
     */
    private void optionLayoutSelectStatus() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            itemLayout.optionItemViewSelectStatus(selectedAttributeList.get(i));
        }
    }

    /**
     * 是否有sku选中
     *
     * @return
     */
    private boolean isSkuSelected() {
        for (SkuAttribute attribute : selectedAttributeList) {
            if (TextUtils.isEmpty(attribute.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取第一个未选中的属性名
     *
     * @return
     */
    public String getFirstUnelectedAttributeName() {
        for (int i = 0; i < skuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) skuContainerLayout.getChildAt(i);
            if (!itemLayout.isSelected()) {
                return itemLayout.getAttributeName();
            }
        }
        return "";
    }

    /**
     * 获取选中的Sku
     *
     * @return
     */
    public Sku getSelectedSku() {
        // 判断是否有选中的sku
        if (!isSkuSelected()) {
            return null;
        }
        for (Sku sku : skuList) {
            List<SkuAttribute> attributeList = sku.getAttributes();
            // 将sku的属性列表与selectedAttributeList匹配，完全匹配则为已选中sku
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

    /**
     * 设置选中的sku
     *
     * @param sku
     */
    public void setSelectedSku(Sku sku) {
        selectedAttributeList.clear();
        for (SkuAttribute attribute : sku.getAttributes()) {
            selectedAttributeList.add(new SkuAttribute(attribute.getKey(), attribute.getValue()));
        }
        // 清除所有选中状态
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }

    /**
     * 是否为同一个SkuAttribute
     *
     * @param previousAttribute
     * @param nextAttribute
     * @return
     */
    private boolean isSameSkuAttribute(SkuAttribute previousAttribute, SkuAttribute nextAttribute) {
        return previousAttribute.getKey().equals(nextAttribute.getKey())
                && previousAttribute.getValue().equals(nextAttribute.getValue());
    }

    @Override
    public void onSelect(int position, boolean selected, SkuAttribute attribute) {
        if (selected) {
            // 选中，保存选中信息
            selectedAttributeList.set(position, attribute);
        } else {
            // 取消选中，清空保存的选中信息
            selectedAttributeList.get(position).setValue("");
        }
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
        // 回调接口
        if (isSkuSelected()) {
            listener.onSkuSelected(getSelectedSku());
        } else if (selected) {
            listener.onSelect(attribute);
        } else {
            listener.onUnselected(attribute);
        }
    }
}