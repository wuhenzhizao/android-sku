package com.wuhenzhizao;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wuhenzhizao.bean.Product;
import com.wuhenzhizao.databinding.ActivityMainBinding;
import com.wuhenzhizao.databinding.LayoutProductShoppingcartBinding;
import com.wuhenzhizao.sku.bean.Sku;
import com.wuhenzhizao.titlebar.utils.AppUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private LayoutProductShoppingcartBinding cartBinding;

    private ProductSkuDialog dialog;
    private int shoppingCartNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        cartBinding = DataBindingUtil.bind(binding.titlebar.getRightCustomView());
        binding.titlebar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    Process.killProcess(Process.myPid());
                }
            }
        });
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkuDialog();
            }
        });

        AndPermission.with(this)
                .requestCode(0)
                .permission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                            AndPermission.defaultSettingDialog(MainActivity.this).show();
                        }
                    }
                })
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this, rationale);
                    }
                }).start();
    }

    private void showSkuDialog() {
        if (dialog == null) {
            dialog = new ProductSkuDialog(this);
            dialog.setData(Product.get(this), new ProductSkuDialog.Callback() {
                @Override
                public void onAdded(Sku sku, int quantity) {
                    shoppingCartNum += quantity;
                    cartBinding.tvShoppingCartNum.setVisibility(View.VISIBLE);

                    // 获取SKU面板Logo拷贝
                    ImageView logoImageView = new ImageView(MainActivity.this);
                    binding.ivAddCartAnim.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(binding.ivAddCartAnim.getDrawingCache());
                    logoImageView.setImageBitmap(bitmap);
                    binding.ivAddCartAnim.setDrawingCacheEnabled(false);

                    int[] startLocation = new int[2];
                    binding.ivAddCartAnim.getLocationOnScreen(startLocation);
                    // 执行动画
                    startAddToCartAnimation(logoImageView, startLocation);
                }
            });
        }
        dialog.show();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AppUtils.transparencyBar(getWindow());
        AppUtils.supportStatusBarLightMode(getWindow());
    }

    /**
     * ********************************添加到购物车动画***********************************
     */
    private ViewGroup mAnimationMaskLayout;      // 动画浮层

    private ViewGroup buildAddToCartAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToCartAnimLayout(View view, int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    private void startAddToCartAnimation(final View v, int[] startLocation) {
        mAnimationMaskLayout = buildAddToCartAnimLayout();
        mAnimationMaskLayout.addView(v);
        final View view = addViewToCartAnimLayout(v, startLocation);
        int[] bottomCartLocation = new int[2];                    // 这是用来存储动画结束位置的X、Y坐标
        cartBinding.ivShoppingCart.getLocationInWindow(bottomCartLocation);

        AddToCartAnimation animation = new AddToCartAnimation(startLocation, bottomCartLocation);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                mAnimationMaskLayout.removeView(v);
                cartBinding.tvShoppingCartNum.setText(String.valueOf(shoppingCartNum));
            }
        });
    }
}
