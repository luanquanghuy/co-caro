package vn.huy.cocaroonline.viewmodel;

import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import vn.huy.cocaroonline.model.CoCaro;
import vn.huy.cocaroonline.model.Postion;

public class CaroViewModel extends ViewModel {
    private MutableLiveData<List<Postion>> dataBuocDi;
    private MutableLiveData<Boolean> current;

    public MutableLiveData<List<Postion>> getBuocDi() {
        if (this.dataBuocDi == null) {
            dataBuocDi = new MutableLiveData<>();
            List<Postion> postionList = new ArrayList<>();
            dataBuocDi.setValue(postionList);
        }
        return dataBuocDi;
    }

    public void addBuocDi(int x, int y){
        MutableLiveData<List<Postion>> dataBuocDi = getBuocDi();
        List<Postion> postionList = dataBuocDi.getValue();
        postionList.add(new Postion(getCurrent().getValue(), x, y));
        dataBuocDi.setValue(postionList);
        switchCurrent();
    }


    public MutableLiveData<Boolean> getCurrent() {
        if (current == null) {
            current = new MutableLiveData<>();
            current.setValue(true);
        }
        return current;
    }

    public void switchCurrent() {
        MutableLiveData<Boolean> current = getCurrent();
        current.setValue(!current.getValue());
    }
}
