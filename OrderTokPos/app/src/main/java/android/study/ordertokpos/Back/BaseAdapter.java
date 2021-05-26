package android.study.ordertokpos.Back;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, H> extends ArrayAdapter<T> {

    /*Activity에 대한 참조 --> LayoutInflater 객체 생성을 위함*/
    private Activity activity;

    /*레이아웃 리소스 아이디*/
    private int resource;

    public BaseAdapter(@NonNull Activity activity, int resource){
        // List객체를 파라미터로 전달받지 않고 내부에서 직접 생성하도록 수정
        super(activity, resource, new ArrayList<T>());

        // activity 참조
        this.activity = activity;
        // 레이아웃 리소스 id를 멤버변수에 연결
        this.resource = resource;
    }

    /**
     * 자식클래스가 Override해야하는 추상 메서드 정의 1
     * 자식 클래스는 파라미터로 전달받은 holder에 선언된 컴포넌트의 객체에
     * LayoutInflater에 의해서 생성된 view에서 컴포넌트를 추출하여 참조시켜야 한다.
     * @param holder - Holder의 객체
     * @param view -  LayoutInflater에 의해서 생성된 객체
     */
    public abstract void setListHolder(H holder, View view);

    /**
     * 자식클래스가 Override해야하는 추상 메서드 정의 2
     * 컴포넌트 할당이 완료된 holder와 표시해야 할 데이터가 저장된 item 객체를 전달 받아서
     * 데이터를 컴포넌트에 출력하는 기능을 구현해야한다.
     * @param holder - Holder의 객체
     * @param item - Holder에 표시할 내용을 저장하고 있는 데이터 객체
     */
    public abstract void setListItem(H holder, T item);

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // ListView에게서 받은 View 객체를 저장한다. -> 최초 1회 호출시 무조건 null
        //food_item.xml에 대한 객체
        View view = convertView;

        // 1. Holder객체 선언
        H holder = null;

        // view가 null인 경우 this.resource가 의미하는 Layout XML파일을 로드한다.
        if(view == null){
            // view가 null인 경우 resourceId가 가리키는 Layout XML을 객체로 생성하고
            // 그 안에 포함된 컴포넌트들을 Holder에 참조, view에 Tag로 저장한다.
            LayoutInflater inflater = this.activity.getLayoutInflater();
            view = inflater.inflate(this.resource, null);

            // 제너릭으로 설덩된 클래스들의 타입을 가져온다
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            // 제너릭 타입 중에서 1번째 타입이 가리키는 클래스 타입을 참조한다.
            Class<H> clazz = (Class<H>) type.getActualTypeArguments()[1];

            try{
                // H 로 전달된 클래스를 참조하는 객체(clazz)를 통하여 생성자를 참조
                Constructor<H> constructor = clazz.getConstructor();
                // 참조한 생성자를 호출하여 holder객체 할당
                holder = constructor.newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }

            // 추상메서드에게 holder객체와 view 객체를 전달하여
            // view의 컴포넌트를 holder에 참조시키도록 한다. --> MainActivity에서 직접 구현
            this.setListHolder(holder, view);
            view.setTag(holder);
        } else{
            /*view에 tag로 저장된 holder를 꺼내 재사용한다.*/
            holder = (H) view.getTag();
        }

        // Holder의 컴포넌트에 데이터 출력하기
        T item = (T) getItem(position);
        if(item != null){
            this.setListItem(holder, item);
        }
        return view;
    }

    public BaseAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }
}
