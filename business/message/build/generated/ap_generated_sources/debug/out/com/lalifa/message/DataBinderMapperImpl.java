package com.lalifa.message;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.lalifa.message.databinding.ItemApplyBindingImpl;
import com.lalifa.message.databinding.ItemFriendBindingImpl;
import com.lalifa.message.databinding.ItemNewFriendBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ITEMAPPLY = 1;

  private static final int LAYOUT_ITEMFRIEND = 2;

  private static final int LAYOUT_ITEMNEWFRIEND = 3;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(3);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.lalifa.message.R.layout.item_apply, LAYOUT_ITEMAPPLY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.lalifa.message.R.layout.item_friend, LAYOUT_ITEMFRIEND);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.lalifa.message.R.layout.item_new_friend, LAYOUT_ITEMNEWFRIEND);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ITEMAPPLY: {
          if ("layout/item_apply_0".equals(tag)) {
            return new ItemApplyBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_apply is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMFRIEND: {
          if ("layout/item_friend_0".equals(tag)) {
            return new ItemFriendBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_friend is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMNEWFRIEND: {
          if ("layout/item_new_friend_0".equals(tag)) {
            return new ItemNewFriendBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_new_friend is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(3);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    result.add(new com.drake.brv.DataBinderMapperImpl());
    result.add(new com.lalifa.base.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(1);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(3);

    static {
      sKeys.put("layout/item_apply_0", com.lalifa.message.R.layout.item_apply);
      sKeys.put("layout/item_friend_0", com.lalifa.message.R.layout.item_friend);
      sKeys.put("layout/item_new_friend_0", com.lalifa.message.R.layout.item_new_friend);
    }
  }
}
