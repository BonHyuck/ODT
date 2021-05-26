package android.study.ordertokemployee.Back;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.ParameterizedType;

import cz.msebera.android.httpclient.Header;

/*통신 결과 처리를 수행할 클래스 정의*/
// [개선1] JSON으로 표현하고자 하는 클래스를 동적으로 식별하기 위한 제너릭 선언
public abstract class BaseResponse<T> extends TextHttpResponseHandler {

    Context context;
    Dialog dialog;
    public BaseResponse(Context context){
        this.context = context;
    }

    /*이 클래스를 상속받는 하위 클래스가 반드시 재정의 해야하는 추상 클래스*/
    // 통신 성공시 서버에서 수신한 결과를 이 메서드에게 전달한다.
    // [개선2] JSON문자열이 아닌 파싱 처리가 완료된 클래스의 객체를 전달 받도록 구성
    public abstract void onResponse(T jsonObject);

    /*통신 시작시에 호출된다.*/
    @Override
    public void onStart(){
        super.onStart();

        if(dialog == null){
            dialog = new Dialog(context);
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0);
            dialog.setContentView(new ProgressBar(context));
            dialog.show();
        }
    }

    /*통신 성공, 실패에 상관없이 종료시에 호출된다.*/
    @Override
    public void onFinish(){
        super.onFinish();

        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 통신접속 성공시 호출된다.
     * @param statusCode 상태코드 (200: 정상, 404 : Page Not Found, 50x : Server Error)
     * @param headers HTTP Header 정보
     * @param responseString HTTP Body (브라우저에 보여지는 내용)
     */
    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString){
        // 정의한 추상 메서드를 호출한다.
        // 이 클래스를 상속받는 자식 클래스에 정의되어 있는 onResponse() 메서드가 호출된다
        // this.onResponse(responseString);

        // [개선3] 제너릭으로 전달된 객체 선언
        T object = null;

        // 서버로부터 응답이 있다면?
        if(responseString != null){
            // Gson 객체 생성
            Gson gson = new Gson();

            // 제너릭으로 설정된 클래스의 이름들을 가져온다. --> T로 전달된 클래스 이름
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            // 제너릭 타입 중에서 0번째 타입(T)이 가리키는 클래스 타입을 참조한다.
            Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
            // 추출한 클래스 타입으로 JSON 문자열을 맵핑한다.
            object = gson.fromJson(responseString, clazz);
        }
        this.onResponse(object);
    }

    /**
     * 통신접속 실패시 호출된다.
     * @param statusCode 상태코드
     * @param headers HTTP Header 정보
     * @param responseString HTTP Body 정보
     * @param throwable 에러정보 객체
     */
    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
        // 에러메시지 표시
        // ex) 500 Error - Server Error
        String errMsg = statusCode + " Error - " +throwable.getLocalizedMessage();
        Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show();
    }
}
