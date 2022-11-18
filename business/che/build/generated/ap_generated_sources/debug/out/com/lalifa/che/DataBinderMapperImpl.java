package com.lalifa.che;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.lalifa.che.databinding.ItemCheBindingImpl;
import com.lalifa.che.databinding.ItemPlBindingImpl;
import com.lalifa.che.databinding.ItemPlChildBindingImpl;
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
  private static final int LAYOUT_ITEMCHE = 1;

  private static final int LAYOUT_ITEMPL = 2;

  private static final int LAYOUT_ITEMPLCHILD = 3;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(3);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.lalifa.che.R.layout.item_che, LAYOUT_ITEMCHE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.lalifa.che.R.layout.item_pl, LAYOUT_ITEMPL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.lalifa.che.R.layout.item_pl_child, LAYOUT_ITEMPLCHILD);
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
        case  LAYOUT_ITEMCHE: {
          if ("layout/item_che_0".equals(tag)) {
            return new ItemCheBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_che is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPL: {
          if ("layout/item_pl_0".equals(tag)) {
            return new ItemPlBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_pl is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPLCHILD: {
          if ("layout/item_pl_child_0".equals(tag)) {
            return new ItemPlChildBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_pl_child is invalid. Received: " + tag);
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
      sKeys.put("layout/item_che_0", com.lalifa.che.R.layout.item_che);
      sKeys.put("layout/item_pl_0", com.lalifa.che.R.layout.item_pl);
      sKeys.put("layout/item_pl_child_0", com.lalifa.che.R.layout.item_pl_child);
    }
  }
}
