package com.wuhenzhizao.sku;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wuhenzhizao.bean.Sku;
import com.wuhenzhizao.bean.SkuAttribute;
import com.wuhenzhizao.sku.databinding.ActivityMainBinding;
import com.wuhenzhizao.widget.OnSkuListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.sku.setSkuList(Data.skuList);
        binding.sku.setListener(new OnSkuListener() {
            @Override
            public void onUnselect(SkuAttribute unselectAttribute) {
                Toast.makeText(MainActivity.this, "取消选中属性：" + unselectAttribute.getKey() + " -- " + unselectAttribute.getValue(), Toast.LENGTH_SHORT).show();
                binding.screen.setText("--:--");
            }

            @Override
            public void onSelect(SkuAttribute selectAttribute) {
                Toast.makeText(MainActivity.this, "选中属性：" + selectAttribute.getKey() + " -- " + selectAttribute.getValue(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSkuSelected(Sku sku) {
                List<SkuAttribute> attributeList = sku.getAttributes();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < attributeList.size(); i++) {
                    if (i != 0) {
                        builder.append("  ");
                    }
                    SkuAttribute attribute = attributeList.get(i);
                    builder.append(attribute.getKey()).append(":").append(attribute.getValue());
                }
                binding.screen.setText(builder.toString());
            }
        });
    }
}
